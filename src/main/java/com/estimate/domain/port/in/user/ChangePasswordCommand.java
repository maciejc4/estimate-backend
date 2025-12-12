package com.estimate.domain.port.in.user;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ChangePasswordCommand {
    String userId;
    String oldPassword;
    String newPassword;
}
