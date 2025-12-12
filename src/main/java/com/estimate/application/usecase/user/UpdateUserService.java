package com.estimate.application.usecase.user;

import com.estimate.domain.exception.ResourceNotFoundException;
import com.estimate.domain.model.User;
import com.estimate.domain.port.in.user.UpdateUserCommand;
import com.estimate.domain.port.in.user.UpdateUserUseCase;
import com.estimate.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateUserService implements UpdateUserUseCase {
    
    private final UserRepositoryPort userRepository;
    
    @Override
    public Mono<User> update(UpdateUserCommand command) {
        return userRepository.findById(command.getUserId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found")))
                .flatMap(user -> {
                    user.setCompanyName(command.getCompanyName());
                    user.setPhone(command.getPhone());
                    
                    return userRepository.save(user);
                })
                .doOnNext(updated -> log.info("User updated: {}", updated.getId()));
    }
}
