package com.cafeops.service;

import com.cafeops.domain.AppUser;
import com.cafeops.domain.Role;
import com.cafeops.repository.AppUserRepository;
import com.cafeops.repository.ReportRepository;
import com.cafeops.web.dto.DailyReportResponse;
import com.cafeops.web.dto.TopMenuItemResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private static final ZoneId ZONE = ZoneId.of("Europe/Belgrade");

    private final ReportRepository reportRepository;
    private final AppUserRepository userRepository;

    public ReportService(ReportRepository reportRepository, AppUserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public DailyReportResponse daily(Long ownerId, LocalDate date) {
        AppUser owner = userRepository.findById(ownerId)
                .filter(u -> u.getRole() == Role.OWNER && u.isEnabled())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));

        if (date == null) {
            date = LocalDate.now(ZONE);
        }

        Instant from = date.atStartOfDay(ZONE).toInstant();
        Instant to = date.plusDays(1).atStartOfDay(ZONE).toInstant();
        Long cafeId = owner.getCafeId();

        long count = reportRepository.countClosedOrders(cafeId, from, to);
        BigDecimal revenue = reportRepository.sumRevenue(cafeId, from, to);
        List<TopMenuItemResponse> top = new ArrayList<>();
        for (Object[] row : reportRepository.topItems(cafeId, from, to)) {
            top.add(new TopMenuItemResponse(
                    ((Number) row[0]).longValue(),
                    (String) row[1],
                    ((Number) row[2]).longValue()));
        }
        return new DailyReportResponse(date, count, revenue, top);
    }
}