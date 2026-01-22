package com.fiinx.analytics.domain.repository;

import com.fiinx.analytics.domain.entity.DailyStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyStatisticRepository extends JpaRepository<DailyStatistic, Long> {
    Optional<DailyStatistic> findByDate(LocalDate date);
    List<DailyStatistic> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);
}
