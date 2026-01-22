package com.fiinx.analytics.application.service;

import com.fiinx.analytics.application.dto.DashboardOverview;
import com.fiinx.analytics.domain.entity.DailyStatistic;
import com.fiinx.analytics.domain.repository.DailyStatisticRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final DailyStatisticRepository dailyStatisticRepository;

    @Transactional
    public void recordOrder(BigDecimal amount) {
        LocalDate today = LocalDate.now();
        DailyStatistic stats = dailyStatisticRepository.findByDate(today)
                .orElse(DailyStatistic.builder().date(today).build());
        
        stats.setTotalOrders(stats.getTotalOrders() + 1);
        stats.setTotalRevenue(stats.getTotalRevenue().add(amount));
        dailyStatisticRepository.save(stats);
    }

    @Transactional
    public void recordCustomer() {
        LocalDate today = LocalDate.now();
        DailyStatistic stats = dailyStatisticRepository.findByDate(today)
                .orElse(DailyStatistic.builder().date(today).build());
        
        stats.setTotalNewCustomers(stats.getTotalNewCustomers() + 1);
        dailyStatisticRepository.save(stats);
    }

    @Transactional(readOnly = true)
    public DashboardOverview getOverview(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        List<DailyStatistic> stats = dailyStatisticRepository.findByDateBetweenOrderByDateAsc(startDate, endDate);
        
        BigDecimal totalRevenue = stats.stream()
                .map(DailyStatistic::getTotalRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Integer totalOrders = stats.stream()
                .mapToInt(DailyStatistic::getTotalOrders)
                .sum();

        Integer totalCustomers = stats.stream()
                .mapToInt(DailyStatistic::getTotalNewCustomers)
                .sum();

        return DashboardOverview.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .totalNewCustomers(totalCustomers)
                .build();
    }
}
