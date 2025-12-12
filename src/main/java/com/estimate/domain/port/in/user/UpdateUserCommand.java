package com.estimate.domain.port.in.user;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UpdateUserCommand {
    String userId;
    String companyName;
    String phone;
}
