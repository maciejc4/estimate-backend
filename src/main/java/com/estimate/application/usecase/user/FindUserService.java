package com.estimate.application.usecase.user;

import com.estimate.domain.exception.ResourceNotFoundException;
import com.estimate.domain.model.User;
import com.estimate.domain.port.in.user.FindUserUseCase;
import com.estimate.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindUserService implements FindUserUseCase {
    
    private final UserRepositoryPort userRepository;
    
    @Override
    public Mono<User> findById(String userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found")));
    }
}
