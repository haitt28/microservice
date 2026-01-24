package com.fiinx.shipping.application.service;

import com.fiinx.shipping.application.dto.*;
import com.fiinx.shipping.domain.entity.Shipment;
import com.fiinx.shipping.domain.entity.ShippingProvider;
import com.fiinx.shipping.domain.repository.ShipmentRepository;
import com.fiinx.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingService {

    private final ShipmentRepository shipmentRepository;

    @Transactional(readOnly = true)
    public ShippingFeeResponse calculateFee(ShippingFeeRequest request) {
        log.info("Calculating shipping fee from {} to {}", request.getFromAddress(), request.getToAddress());
        
        // Mocking fee calculation logic
        BigDecimal baseFee = new BigDecimal("30000"); // 30k default
        if (request.getWeight() != null) {
            baseFee = baseFee.add(request.getWeight().multiply(new BigDecimal("5000")));
        }
        
        return ShippingFeeResponse.builder()
                .provider(request.getProvider() != null ? request.getProvider() : ShippingProvider.GHN)
                .fee(baseFee)
                .estimatedDelivery(Instant.now().plus(java.time.Duration.ofDays(3)))
                .build();
    }

    @Transactional
    public ShipmentResponse createShipment(CreateShipmentRequest request) {
        log.info("Creating shipment for order: {}", request.getOrderId());
        
        // Mocking provider API call to get tracking code
        String trackingCode = "FIINX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        ShippingFeeRequest feeReq = ShippingFeeRequest.builder()
                .fromAddress(request.getSenderAddress())
                .toAddress(request.getReceiverAddress())
                .weight(request.getWeight())
                .provider(request.getProvider())
                .build();
        
        BigDecimal fee = calculateFee(feeReq).getFee();

        Shipment shipment = Shipment.builder()
                .orderId(request.getOrderId())
                .provider(request.getProvider())
                .trackingCode(trackingCode)
                .senderAddress(request.getSenderAddress())
                .receiverAddress(request.getReceiverAddress())
                .weight(request.getWeight())
                .shippingFee(fee)
                .status("CREATED")
                .estimatedDelivery(Instant.now().plus(java.time.Duration.ofDays(3)))
                .build();

        shipment = shipmentRepository.save(shipment);
        
        return ShipmentResponse.builder()
                .id(shipment.getId())
                .orderId(shipment.getOrderId())
                .provider(shipment.getProvider())
                .trackingCode(shipment.getTrackingCode())
                .shippingFee(shipment.getShippingFee())
                .status(shipment.getStatus())
                .estimatedDelivery(shipment.getEstimatedDelivery())
                .build();
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getTrackingInfo(String trackingCode) {
        Shipment shipment = shipmentRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "trackingCode", trackingCode));
        
        return ShipmentResponse.builder()
                .id(shipment.getId())
                .orderId(shipment.getOrderId())
                .provider(shipment.getProvider())
                .trackingCode(shipment.getTrackingCode())
                .shippingFee(shipment.getShippingFee())
                .status(shipment.getStatus())
                .estimatedDelivery(shipment.getEstimatedDelivery())
                .build();
    }
}
