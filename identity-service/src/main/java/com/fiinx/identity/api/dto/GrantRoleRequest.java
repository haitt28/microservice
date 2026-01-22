package com.fiinx.identity.api.dto;

import lombok.Data;

@Data
public class GrantRoleRequest {
    private String userId;
    private String roleName;
}
