package com.estimate.domain.port.in.work;

import com.estimate.domain.model.Material;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class UpdateWorkCommand {
    String workId;
    String userId;
    String name;
    String unit;
    List<Material> materials;
}
