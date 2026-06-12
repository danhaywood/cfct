package com.danhaywood.cfct.webapp.automation;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/automation")
public class AutomationController {

    private final AutomationComparisonService automationComparisonService;

    public AutomationController(final AutomationComparisonService automationComparisonService) {
        this.automationComparisonService = automationComparisonService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh() {
        try {
            final AutomationComparisonService.AutomationRefreshResult result = automationComparisonService.refresh();
            if (result.conflict()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of(
                                "status", "in_progress",
                                "message", "Automation comparison refresh is already running."));
            }
            final AutomationComparisonService.LatestAutomationResult latestResult = result.latestResult();
            return ResponseEntity.ok(Map.of(
                    "status", "completed",
                    "completedAt", latestResult.completedAt().toString(),
                    "tableCount", latestResult.tableCount()));
        } catch (AutomationComparisonService.AutomationDisabledException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", "disabled",
                            "message", "Automation API is not enabled."));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "failed",
                            "message", conciseMessage(ex)));
        }
    }

    @GetMapping(value = "/comparison.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> downloadJson() {
        try {
            final AutomationComparisonService.LatestAutomationResult latestResult = automationComparisonService.latestResult();
            if (latestResult == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of(
                                "status", "not_found",
                                "message", "No successful automation comparison result is available."));
            }
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDisposition(ContentDisposition.attachment().filename(latestResult.filename()).build());
            return new ResponseEntity<>(latestResult.json().getBytes(StandardCharsets.UTF_8), headers, HttpStatus.OK);
        } catch (AutomationComparisonService.AutomationDisabledException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "status", "disabled",
                            "message", "Automation API is not enabled."));
        }
    }

    private static String conciseMessage(final RuntimeException ex) {
        final String message = ex.getMessage();
        if (message != null && !message.isBlank()) {
            return message;
        }
        return ex.getClass().getSimpleName();
    }
}
