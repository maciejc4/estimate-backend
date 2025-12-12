package com.estimate.application.usecase.work;

import com.estimate.domain.exception.UnauthorizedAccessException;
import com.estimate.domain.exception.WorkNotFoundException;
import com.estimate.domain.port.in.work.DeleteWorkUseCase;
import com.estimate.domain.port.out.WorkRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteWorkService implements DeleteWorkUseCase {
    
    private final WorkRepositoryPort workRepository;
    
    @Override
    public Mono<Void> delete(String workId, String userId) {
        return workRepository.findById(workId)
                .switchIfEmpty(Mono.error(new WorkNotFoundException(workId)))
                .flatMap(work -> {
                    if (!work.getUserId().equals(userId)) {
                        return Mono.error(new UnauthorizedAccessException("Not authorized to delete this work"));
                    }
                    return workRepository.deleteById(workId);
                })
                .doOnSuccess(v -> log.info("Work deleted: {} by user: {}", workId, userId));
    }
}
