package com.estimate.application.usecase.user;

import com.estimate.domain.port.in.user.DeleteUserUseCase;
import com.estimate.domain.port.out.EstimateRepositoryPort;
import com.estimate.domain.port.out.RenovationTemplateRepositoryPort;
import com.estimate.domain.port.out.UserRepositoryPort;
import com.estimate.domain.port.out.WorkRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteUserService implements DeleteUserUseCase {
    
    private final UserRepositoryPort userRepository;
    private final WorkRepositoryPort workRepository;
    private final RenovationTemplateRepositoryPort templateRepository;
    private final EstimateRepositoryPort estimateRepository;
    
    @Override
    public Mono<Void> delete(String userId) {
        return workRepository.deleteByUserId(userId)
                .then(templateRepository.deleteByUserId(userId))
                .then(estimateRepository.deleteByUserId(userId))
                .then(userRepository.deleteById(userId))
                .doOnSuccess(v -> log.info("User and all related data deleted: {}", userId));
    }
}
