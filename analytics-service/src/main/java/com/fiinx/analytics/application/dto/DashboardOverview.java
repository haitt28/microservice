package com.fiinx.analytics.application.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverview {
    private BigDecimal totalRevenue;
    private Integer totalOrders;
    private Integer totalNewCustomers;
    private List<DailyStatDto> chartData;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DailyStatDto {
    private LocalDate date;
    private BigDecimal revenue;
    private Integer orders;
}
