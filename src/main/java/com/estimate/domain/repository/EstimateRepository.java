package com.estimate.domain.repository;

import com.estimate.domain.model.Estimate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstimateRepository extends MongoRepository<Estimate, String> {
    
    List<Estimate> findByUserId(String userId);
}
