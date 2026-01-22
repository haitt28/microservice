package com.fiinx.identity.api.dto;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    private String recipientName;
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String detail;
    private Boolean isDefault;
}
