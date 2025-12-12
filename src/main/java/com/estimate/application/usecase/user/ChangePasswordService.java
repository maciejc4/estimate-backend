package com.estimate.application.usecase.user;

import com.estimate.domain.exception.InvalidPasswordException;
import com.estimate.domain.exception.ResourceNotFoundException;
import com.estimate.domain.port.in.user.ChangePasswordCommand;
import com.estimate.domain.port.in.user.ChangePasswordUseCase;
import com.estimate.domain.port.out.PasswordEncoderPort;
import com.estimate.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangePasswordService implements ChangePasswordUseCase {
    
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    
    @Override
    public Mono<Void> changePassword(ChangePasswordCommand command) {
        return userRepository.findById(command.getUserId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found")))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(command.getOldPassword(), user.getPasswordHash())) {
                        return Mono.error(new InvalidPasswordException("Old password is incorrect"));
                    }
                    
                    user.setPasswordHash(passwordEncoder.encode(command.getNewPassword()));
                    return userRepository.save(user);
                })
                .doOnNext(user -> log.info("Password changed for user: {}", user.getId()))
                .then();
    }
}
