package com.estimate.domain.repository;

import com.estimate.domain.model.Work;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkRepository extends MongoRepository<Work, String> {
    
    List<Work> findByUserId(String userId);
    
    List<Work> findByUserIdAndIdIn(String userId, List<String> ids);
    
    void deleteByUserId(String userId);
}
