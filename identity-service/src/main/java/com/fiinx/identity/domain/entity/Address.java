package com.fiinx.identity.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String userId;

    private String recipientName;
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String detail;
    
    @Builder.Default
    private Boolean isDefault = false;
}
