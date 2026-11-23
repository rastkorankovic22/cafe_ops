package com.cafeops.web;

import com.cafeops.security.AuthUserPrincipal;
import com.cafeops.service.TableService;
import com.cafeops.web.dto.CreateTableRequest;
import com.cafeops.web.dto.TableResponse;
import com.cafeops.web.dto.UpdateTableRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tables")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public List<TableResponse> list(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @RequestParam(defaultValue = "false") boolean includeInactive
    ) {
        return tableService.list(principal.id(), includeInactive);
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public TableResponse create(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @Valid @RequestBody CreateTableRequest request
    ) {
        return tableService.create(principal.id(), request);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public TableResponse update(
            @AuthenticationPrincipal AuthUserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateTableRequest request
    ) {
        return tableService.update(principal.id(), id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@AuthenticationPrincipal AuthUserPrincipal principal, @PathVariable Long id) {
        tableService.remove(principal.id(), id);
    }
}