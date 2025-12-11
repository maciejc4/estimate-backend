package com.estimate.application.service;

import com.estimate.domain.model.*;
import com.estimate.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SampleDataService {

    private final WorkRepository workRepository;
    private final RenovationTemplateRepository templateRepository;
    private final EstimateRepository estimateRepository;

    @Transactional
    public void generateSampleData(String userId) {
        log.info("Generating sample data for user: {}", userId);
        
        // Create sample works
        List<Work> works = createSampleWorks(userId);
        
        // Create sample templates
        createSampleTemplates(userId, works);
        
        log.info("Sample data generated successfully for user: {}", userId);
    }

    private List<Work> createSampleWorks(String userId) {
        List<Work> works = new ArrayList<>();
        
        // Work 1: Malowanie ścian
        Work painting = new Work();
        painting.setUserId(userId);
        painting.setName("Malowanie ścian farbą akrylową");
        painting.setUnit("m²");
        
        List<Material> paintingMaterials = new ArrayList<>();
        paintingMaterials.add(Material.builder()
            .name("Farba akrylowa")
            .unit("l")
            .consumptionPerWorkUnit(BigDecimal.valueOf(0.15))
            .build());
        paintingMaterials.add(Material.builder()
            .name("Grunt głęboko penetrujący")
            .unit("l")
            .consumptionPerWorkUnit(BigDecimal.valueOf(0.1))
            .build());
        painting.setMaterials(paintingMaterials);
        works.add(workRepository.save(painting));
        
        // Work 2: Gruntowanie
        Work priming = new Work();
        priming.setUserId(userId);
        priming.setName("Gruntowanie ścian");
        priming.setUnit("m²");
        
        List<Material> primingMaterials = new ArrayList<>();
        primingMaterials.add(Material.builder()
            .name("Grunt")
            .unit("l")
            .consumptionPerWorkUnit(BigDecimal.valueOf(0.12))
            .build());
        priming.setMaterials(primingMaterials);
        works.add(workRepository.save(priming));
        
        // Work 3: Układanie płytek
        Work tiling = new Work();
        tiling.setUserId(userId);
        tiling.setName("Układanie płytek ceramicznych");
        tiling.setUnit("m²");
        
        List<Material> tilingMaterials = new ArrayList<>();
        tilingMaterials.add(Material.builder()
            .name("Płytki ceramiczne")
            .unit("m²")
            .consumptionPerWorkUnit(BigDecimal.valueOf(1.1))
            .build());
        tilingMaterials.add(Material.builder()
            .name("Klej do płytek")
            .unit("kg")
            .consumptionPerWorkUnit(BigDecimal.valueOf(5.0))
            .build());
        tiling.setMaterials(tilingMaterials);
        works.add(workRepository.save(tiling));
        
        // Work 4: Szpachlowanie
        Work plastering = new Work();
        plastering.setUserId(userId);
        plastering.setName("Szpachlowanie ścian");
        plastering.setUnit("m²");
        
        List<Material> plasteringMaterials = new ArrayList<>();
        plasteringMaterials.add(Material.builder()
            .name("Gładź szpachlowa")
            .unit("kg")
            .consumptionPerWorkUnit(BigDecimal.valueOf(1.2))
            .build());
        plastering.setMaterials(plasteringMaterials);
        works.add(workRepository.save(plastering));
        
        log.info("Created {} sample works", works.size());
        return works;
    }

    private void createSampleTemplates(String userId, List<Work> works) {
        if (works.size() < 3) {
            return;
        }
        
        // Template 1: Malowanie pokoju
        RenovationTemplate paintingTemplate = new RenovationTemplate();
        paintingTemplate.setUserId(userId);
        paintingTemplate.setName("Malowanie pokoju");
        
        List<String> paintingWorkIds = new ArrayList<>();
        paintingWorkIds.add(works.get(0).getId()); // Malowanie
        paintingWorkIds.add(works.get(1).getId()); // Gruntowanie
        paintingWorkIds.add(works.get(3).getId()); // Szpachlowanie
        paintingTemplate.setWorkIds(paintingWorkIds);
        templateRepository.save(paintingTemplate);
        
        // Template 2: Remont łazienki
        RenovationTemplate bathroomTemplate = new RenovationTemplate();
        bathroomTemplate.setUserId(userId);
        bathroomTemplate.setName("Remont łazienki");
        
        List<String> bathroomWorkIds = new ArrayList<>();
        bathroomWorkIds.add(works.get(2).getId()); // Płytki
        bathroomWorkIds.add(works.get(0).getId()); // Malowanie
        bathroomTemplate.setWorkIds(bathroomWorkIds);
        templateRepository.save(bathroomTemplate);
        
        log.info("Created 2 sample templates");
    }

    public boolean hasSampleData(String userId) {
        return workRepository.findByUserId(userId).size() > 0;
    }

    @Transactional
    public void deleteSampleData(String userId) {
        workRepository.deleteByUserId(userId);
        templateRepository.deleteByUserId(userId);
        estimateRepository.deleteByUserId(userId);
        log.info("Deleted sample data for user: {}", userId);
    }
}
