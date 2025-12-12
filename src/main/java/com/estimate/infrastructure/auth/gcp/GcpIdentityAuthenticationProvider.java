package com.estimate.infrastructure.auth.gcp;

import com.estimate.domain.exception.EmailAlreadyExistsException;
import com.estimate.domain.exception.InvalidCredentialsException;
import com.estimate.domain.model.AuthProviderType;
import com.estimate.domain.model.AuthenticationResult;
import com.estimate.domain.model.RegisterUserData;
import com.estimate.domain.model.User;
import com.estimate.domain.model.UserAuthInfo;
import com.estimate.domain.port.out.AuthenticationProviderPort;
import com.estimate.domain.port.out.UserRepositoryPort;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Profile("gcp")
@RequiredArgsConstructor
public class GcpIdentityAuthenticationProvider implements AuthenticationProviderPort {
    
    private final UserRepositoryPort userRepository;
    private final FirebaseAuth firebaseAuth;
    
    @Override
    public Mono<AuthenticationResult> authenticate(String email, String password) {
        return Mono.error(new UnsupportedOperationException(
                "Direct password authentication not supported with GCP Identity Platform. " +
                "Use Firebase Client SDK for authentication and send the ID token to the backend."
        ));
    }
    
    @Override
    public Mono<AuthenticationResult> registerUser(RegisterUserData userData) {
        return Mono.fromCallable(() -> {
                    UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                            .setEmail(userData.getEmail())
                            .setPassword(userData.getPassword())
                            .setEmailVerified(false);
                    
                    try {
                        UserRecord userRecord = firebaseAuth.createUser(request);
                        log.info("Created Firebase user: {}", userRecord.getUid());
                        return userRecord;
                    } catch (FirebaseAuthException e) {
                        if (e.getAuthErrorCode().name().equals("EMAIL_ALREADY_EXISTS")) {
                            throw new EmailAlreadyExistsException(userData.getEmail());
                        }
                        throw new RuntimeException("Failed to create Firebase user", e);
                    }
                })
                .flatMap(userRecord -> {
                    User user = User.builder()
                            .id(userRecord.getUid())
                            .email(userRecord.getEmail())
                            .companyName(userData.getCompanyName())
                            .phone(userData.getPhone())
                            .role(User.Role.USER)
                            .build();
                    
                    return userRepository.save(user)
                            .map(savedUser -> {
                                String customToken;
                                try {
                                    customToken = firebaseAuth.createCustomToken(userRecord.getUid());
                                } catch (FirebaseAuthException e) {
                                    throw new RuntimeException("Failed to create custom token", e);
                                }
                                
                                return AuthenticationResult.builder()
                                        .token(customToken)
                                        .userId(savedUser.getId())
                                        .email(savedUser.getEmail())
                                        .role(savedUser.getRole().name())
                                        .providerType(AuthProviderType.GCP_IDENTITY)
                                        .build();
                            });
                });
    }
    
    @Override
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                firebaseAuth.verifyIdToken(token);
                return true;
            } catch (FirebaseAuthException e) {
                log.warn("Invalid Firebase token: {}", e.getMessage());
                return false;
            }
        });
    }
    
    @Override
    public Mono<UserAuthInfo> extractUserInfo(String token) {
        return Mono.fromCallable(() -> {
                    try {
                        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
                        return decodedToken;
                    } catch (FirebaseAuthException e) {
                        throw new InvalidCredentialsException();
                    }
                })
                .flatMap(decodedToken -> {
                    String uid = decodedToken.getUid();
                    String email = decodedToken.getEmail();
                    
                    return userRepository.findById(uid)
                            .switchIfEmpty(syncUserFromFirebase(uid, email))
                            .map(user -> new UserAuthInfo(
                                    user.getId(),
                                    user.getEmail(),
                                    user.getRole().name()
                            ));
                });
    }
    
    @Override
    public boolean managesPasswords() {
        return false;
    }
    
    private Mono<User> syncUserFromFirebase(String uid, String email) {
        log.info("Syncing user from Firebase: {}", uid);
        
        User user = User.builder()
                .id(uid)
                .email(email)
                .role(User.Role.USER)
                .build();
        
        return userRepository.save(user);
    }
}
