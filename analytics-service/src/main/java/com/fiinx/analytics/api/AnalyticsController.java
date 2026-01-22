package com.fiinx.analytics.api;

import com.fiinx.analytics.application.dto.DashboardOverview;
import com.fiinx.analytics.application.service.AnalyticsService;
import com.fiinx.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Business analytics and dashboard API (Admin only)")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get dashboard overview", description = "Get aggregate stats for dashboard (last 30 days default)")
    public ResponseEntity<ApiResponse<DashboardOverview>> getOverview(@RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getOverview(days)));
    }
}
