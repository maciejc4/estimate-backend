package com.estimate.application.service;

import com.estimate.domain.model.*;
import com.estimate.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        List<RenovationTemplate> templates = createSampleTemplates(userId, works);
        
        // Create sample estimate
        createSampleEstimate(userId, works, templates);
        
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
        paintingMaterials.add(new Material("Farba akrylowa", "l", 0.15));
        paintingMaterials.add(new Material("Grunt głęboko penetrujący", "l", 0.1));
        paintingMaterials.add(new Material("Taśma malarska", "mb", 0.5));
        painting.setMaterials(paintingMaterials);
        works.add(workRepository.save(painting));
        
        // Work 2: Gruntowanie
        Work priming = new Work();
        priming.setUserId(userId);
        priming.setName("Gruntowanie ścian");
        priming.setUnit("m²");
        
        List<Material> primingMaterials = new ArrayList<>();
        primingMaterials.add(new Material("Grunt", "l", 0.12));
        priming.setMaterials(primingMaterials);
        works.add(workRepository.save(priming));
        
        // Work 3: Układanie płytek
        Work tiling = new Work();
        tiling.setUserId(userId);
        tiling.setName("Układanie płytek ceramicznych");
        tiling.setUnit("m²");
        
        List<Material> tilingMaterials = new ArrayList<>();
        tilingMaterials.add(new Material("Płytki ceramiczne", "m²", 1.1));
        tilingMaterials.add(new Material("Klej do płytek", "kg", 5.0));
        tilingMaterials.add(new Material("Fuga", "kg", 0.5));
        tiling.setMaterials(tilingMaterials);
        works.add(workRepository.save(tiling));
        
        // Work 4: Montaż listew
        Work baseboard = new Work();
        baseboard.setUserId(userId);
        baseboard.setName("Montaż listew przypodłogowych");
        baseboard.setUnit("mb");
        
        List<Material> baseboardMaterials = new ArrayList<>();
        baseboardMaterials.add(new Material("Listwa PCV", "mb", 1.05));
        baseboardMaterials.add(new Material("Klej montażowy", "ml", 50.0));
        baseboard.setMaterials(baseboardMaterials);
        works.add(workRepository.save(baseboard));
        
        // Work 5: Szpachlowanie
        Work plastering = new Work();
        plastering.setUserId(userId);
        plastering.setName("Szpachlowanie ścian");
        plastering.setUnit("m²");
        
        List<Material> plasteringMaterials = new ArrayList<>();
        plasteringMaterials.add(new Material("Gładź szpachlowa", "kg", 1.2));
        plasteringMaterials.add(new Material("Siatka do szpachlowania", "m²", 0.1));
        plastering.setMaterials(plasteringMaterials);
        works.add(workRepository.save(plastering));
        
        // Work 6: Wykończenie elektryczne
        Work electrical = new Work();
        electrical.setUserId(userId);
        electrical.setName("Montaż gniazdka elektrycznego");
        electrical.setUnit("szt");
        
        List<Material> electricalMaterials = new ArrayList<>();
        electricalMaterials.add(new Material("Gniazdko", "szt", 1.0));
        electricalMaterials.add(new Material("Puszka podtynkowa", "szt", 1.0));
        electricalMaterials.add(new Material("Przewód YDY", "mb", 2.0));
        electrical.setMaterials(electricalMaterials);
        works.add(workRepository.save(electrical));
        
        log.info("Created {} sample works", works.size());
        return works;
    }

    private List<RenovationTemplate> createSampleTemplates(String userId, List<Work> works) {
        List<RenovationTemplate> templates = new ArrayList<>();
        
        if (works.size() >= 3) {
            // Template 1: Malowanie pokoju
            RenovationTemplate paintingTemplate = new RenovationTemplate();
            paintingTemplate.setUserId(userId);
            paintingTemplate.setName("Malowanie pokoju");
            
            List<String> paintingWorkIds = new ArrayList<>();
            paintingWorkIds.add(works.get(0).getId()); // Malowanie
            paintingWorkIds.add(works.get(1).getId()); // Gruntowanie
            paintingWorkIds.add(works.get(4).getId()); // Szpachlowanie
            paintingTemplate.setWorkIds(paintingWorkIds);
            templates.add(templateRepository.save(paintingTemplate));
            
            // Template 2: Remont łazienki
            RenovationTemplate bathroomTemplate = new RenovationTemplate();
            bathroomTemplate.setUserId(userId);
            bathroomTemplate.setName("Remont łazienki");
            
            List<String> bathroomWorkIds = new ArrayList<>();
            bathroomWorkIds.add(works.get(2).getId()); // Płytki
            bathroomWorkIds.add(works.get(5).getId()); // Elektryka
            bathroomWorkIds.add(works.get(0).getId()); // Malowanie
            bathroomTemplate.setWorkIds(bathroomWorkIds);
            templates.add(templateRepository.save(bathroomTemplate));
        }
        
        log.info("Created {} sample templates", templates.size());
        return templates;
    }

    private void createSampleEstimate(String userId, List<Work> works, List<RenovationTemplate> templates) {
        if (works.isEmpty()) {
            return;
        }
        
        Estimate estimate = new Estimate();
        estimate.setUserId(userId);
        estimate.setInvestorName("Jan Kowalski");
        estimate.setInvestorAddress("ul. Przykładowa 123, Warszawa");
        
        // Add template IDs
        List<String> templateIds = new ArrayList<>();
        if (!templates.isEmpty()) {
            templateIds.add(templates.get(0).getId());
        }
        estimate.setTemplateIds(templateIds);
        
        // Add works with prices
        List<EstimateWork> estimateWorks = new ArrayList<>();
        
        // Example work with prices
        EstimateWork estimateWork = new EstimateWork();
        estimateWork.setWorkId(works.get(0).getId());
        estimateWork.setQuantity(45.0); // 45 m²
        estimateWork.setLaborPrice(25.0); // 25 PLN per m²
        
        List<MaterialPrice> materialPrices = new ArrayList<>();
        for (Material material : works.get(0).getMaterials()) {
            MaterialPrice mp = new MaterialPrice();
            mp.setMaterialName(material.getName());
            mp.setPricePerUnit(15.0);
            materialPrices.add(mp);
        }
        estimateWork.setMaterialPrices(materialPrices);
        estimateWorks.add(estimateWork);
        
        estimate.setWorks(estimateWorks);
        estimate.setMaterialDiscount(5.0); // 5% discount
        estimate.setLaborDiscount(0.0);
        estimate.setNotes("Przykładowy kosztorys wygenerowany automatycznie");
        estimate.setValidUntil(LocalDate.now().plusMonths(1));
        estimate.setStartDate(LocalDate.now().plusWeeks(2));
        
        estimateRepository.save(estimate);
        log.info("Created sample estimate");
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
