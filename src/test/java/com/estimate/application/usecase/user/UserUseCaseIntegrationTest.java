package com.estimate.application.usecase.user;

import com.estimate.domain.exception.InvalidPasswordException;
import com.estimate.domain.exception.ResourceNotFoundException;
import com.estimate.domain.model.User;
import com.estimate.domain.port.in.user.*;
import com.estimate.domain.port.out.PasswordEncoderPort;
import com.estimate.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

@SpringBootTest
class UserUseCaseIntegrationTest {
    
    @Autowired
    private FindUserUseCase findUserUseCase;
    
    @Autowired
    private UpdateUserUseCase updateUserUseCase;
    
    @Autowired
    private DeleteUserUseCase deleteUserUseCase;
    
    @Autowired
    private ChangePasswordUseCase changePasswordUseCase;
    
    @Autowired
    private UserRepositoryPort userRepository;
    
    @Autowired
    private PasswordEncoderPort passwordEncoder;
    
    @Test
    void shouldFindUserById() {
        User user = User.builder()
                .email("test@example.com")
                .passwordHash(passwordEncoder.encode("password"))
                .companyName("Test Company")
                .build();
        
        StepVerifier.create(
                userRepository.save(user)
                        .flatMap(saved -> findUserUseCase.findById(saved.getId()))
        )
                .expectNextMatches(found -> found.getEmail().equals("test@example.com"))
                .verifyComplete();
    }
    
    @Test
    void shouldUpdateUser() {
        User user = User.builder()
                .email("update@example.com")
                .passwordHash(passwordEncoder.encode("password"))
                .build();
        
        StepVerifier.create(
                userRepository.save(user)
                        .flatMap(saved -> {
                            UpdateUserCommand command = UpdateUserCommand.builder()
                                    .userId(saved.getId())
                                    .companyName("New Company")
                                    .phone("123456789")
                                    .build();
                            return updateUserUseCase.update(command);
                        })
        )
                .expectNextMatches(updated -> 
                        updated.getCompanyName().equals("New Company") &&
                        updated.getPhone().equals("123456789"))
                .verifyComplete();
    }
    
    @Test
    void shouldChangePassword() {
        String oldPassword = "oldPassword123";
        User user = User.builder()
                .email("password@example.com")
                .passwordHash(passwordEncoder.encode(oldPassword))
                .build();
        
        StepVerifier.create(
                userRepository.save(user)
                        .flatMap(saved -> {
                            ChangePasswordCommand command = ChangePasswordCommand.builder()
                                    .userId(saved.getId())
                                    .oldPassword(oldPassword)
                                    .newPassword("newPassword456")
                                    .build();
                            return changePasswordUseCase.changePassword(command)
                                    .thenReturn(saved.getId());
                        })
                        .flatMap(userId -> userRepository.findById(userId))
        )
                .expectNextMatches(updated -> 
                        passwordEncoder.matches("newPassword456", updated.getPasswordHash()))
                .verifyComplete();
    }
    
    @Test
    void shouldNotChangePasswordWithWrongOldPassword() {
        User user = User.builder()
                .email("wrongpass@example.com")
                .passwordHash(passwordEncoder.encode("correctPassword"))
                .build();
        
        StepVerifier.create(
                userRepository.save(user)
                        .flatMap(saved -> {
                            ChangePasswordCommand command = ChangePasswordCommand.builder()
                                    .userId(saved.getId())
                                    .oldPassword("wrongPassword")
                                    .newPassword("newPassword")
                                    .build();
                            return changePasswordUseCase.changePassword(command);
                        })
        )
                .expectError(InvalidPasswordException.class)
                .verify();
    }
    
    @Test
    void shouldDeleteUser() {
        User user = User.builder()
                .email("delete@example.com")
                .passwordHash(passwordEncoder.encode("password"))
                .build();
        
        StepVerifier.create(
                userRepository.save(user)
                        .flatMap(saved -> deleteUserUseCase.delete(saved.getId())
                                .thenReturn(saved.getId()))
                        .flatMap(userId -> userRepository.findById(userId))
        )
                .expectNextCount(0)
                .verifyComplete();
    }
}
