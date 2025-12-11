package com.estimate.domain.usecase;

import com.estimate.domain.model.Material;
import com.estimate.domain.model.Work;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CreateWorkUseCase {
    Mono<Work> execute(String userId, String name, String unit, List<Material> materials);
}
