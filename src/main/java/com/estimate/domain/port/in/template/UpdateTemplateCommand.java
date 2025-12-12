package com.estimate.domain.port.in.template;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class UpdateTemplateCommand {
    String templateId;
    String userId;
    String name;
    List<String> workIds;
}
