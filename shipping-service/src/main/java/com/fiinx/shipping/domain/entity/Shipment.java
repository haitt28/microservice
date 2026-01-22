package com.fiinx.shipping.domain.entity;

import com.fiinx.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment extends BaseEntity {

    @Column(nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingProvider provider;

    private String trackingCode;

    @Column(nullable = false)
    private String senderAddress;

    @Column(nullable = false)
    private String receiverAddress;

    @Column(precision = 10, scale = 2)
    private BigDecimal weight;

    @Column(precision = 12, scale = 2)
    private BigDecimal shippingFee;

    @Column(nullable = false)
    private String status; // Initial status like "CREATED", "PICKED_UP"...

    private Instant estimatedDelivery;

    private Instant actualDelivery;
}
