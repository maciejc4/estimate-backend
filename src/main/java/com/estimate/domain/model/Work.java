package com.estimate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Work {
    
    private String id;
    private String userId;
    private String name;
    private String unit;
    @Builder.Default
    private List<Material> materials = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
    
    public void addMaterial(Material material) {
        if (this.materials == null) {
            this.materials = new ArrayList<>();
        }
        this.materials.add(material);
    }
    
    public void removeMaterial(String materialName) {
        if (this.materials != null) {
            this.materials.removeIf(m -> m.getName().equals(materialName));
        }
    }
}
