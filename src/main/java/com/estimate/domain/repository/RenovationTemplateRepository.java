package com.estimate.domain.repository;

import com.estimate.domain.model.RenovationTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RenovationTemplateRepository extends MongoRepository<RenovationTemplate, String> {
    
    List<RenovationTemplate> findByUserId(String userId);
    
    void deleteByUserId(String userId);
}
