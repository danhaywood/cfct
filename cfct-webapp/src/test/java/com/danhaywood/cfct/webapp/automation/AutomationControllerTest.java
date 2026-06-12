package com.danhaywood.cfct.webapp.automation;

import com.danhaywood.cfct.webapp.config.WebappComparisonProperties;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AutomationControllerTest {

    @Test
    void rejectsMissingBasicCredentials() throws Exception {
        final MockMvc mockMvc = mockMvc(mock(AutomationComparisonService.class));

        mockMvc.perform(get("/api/automation/comparison.json"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, containsString("Basic")));
    }

    @Test
    void rejectsInvalidBasicCredentials() throws Exception {
        final MockMvc mockMvc = mockMvc(mock(AutomationComparisonService.class));

        mockMvc.perform(get("/api/automation/comparison.json")
                        .header(HttpHeaders.AUTHORIZATION, basic("robot", "wrong")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void downloadRefreshesAndReturnsJsonForValidBasicCredentials() throws Exception {
        final AutomationComparisonService service = mock(AutomationComparisonService.class);
        when(service.refresh()).thenReturn(AutomationComparisonService.AutomationRefreshResult.success(
                new AutomationComparisonService.LatestAutomationResult(
                        "{\"hasDifferences\":false,\"differingTables\":[],\"comparedTables\":[],\"command\":{\"interactionId\":\"newest-ok\",\"timestamp\":\"2026-06-12T07:00:00\"},\"backgroundCommands\":{\"pending\":2}}\n",
                        Instant.parse("2026-06-12T07:00:00Z"),
                        0,
                        new AutomationComparisonService.CommandMetadata("newest-ok", "2026-06-12T07:00:00"),
                        new AutomationComparisonService.BackgroundCommandsMetadata(2))));
        final MockMvc mockMvc = mockMvc(service);

        mockMvc.perform(get("/api/automation/comparison.json")
                        .header(HttpHeaders.AUTHORIZATION, basic("robot", "secret")))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, containsString("application/json")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("comparison-2026-06-12T07-00-00Z.json")))
                .andExpect(content().json("{\"hasDifferences\":false,\"differingTables\":[],\"comparedTables\":[],\"command\":{\"interactionId\":\"newest-ok\",\"timestamp\":\"2026-06-12T07:00:00\"},\"backgroundCommands\":{\"pending\":2}}"))
                .andExpect(jsonPath("$.command.interactionId").value("newest-ok"))
                .andExpect(jsonPath("$.command.timestamp").value("2026-06-12T07:00:00"))
                .andExpect(jsonPath("$.backgroundCommands.pending").value(2));

        verify(service).refresh();
    }

    @Test
    void downloadReportsConflictWhenAlreadyRunning() throws Exception {
        final AutomationComparisonService service = mock(AutomationComparisonService.class);
        when(service.refresh()).thenReturn(AutomationComparisonService.AutomationRefreshResult.inProgress());
        final MockMvc mockMvc = mockMvc(service);

        mockMvc.perform(get("/api/automation/comparison.json")
                        .header(HttpHeaders.AUTHORIZATION, basic("robot", "secret")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("in_progress"));
    }

    @Test
    void downloadReportsExecutionFailure() throws Exception {
        final AutomationComparisonService service = mock(AutomationComparisonService.class);
        when(service.refresh()).thenThrow(new IllegalStateException("database unavailable"));
        final MockMvc mockMvc = mockMvc(service);

        mockMvc.perform(get("/api/automation/comparison.json")
                        .header(HttpHeaders.AUTHORIZATION, basic("robot", "secret")))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("database unavailable"));
    }

    @Test
    void downloadReportsDisabledAutomation() throws Exception {
        final AutomationComparisonService service = mock(AutomationComparisonService.class);
        when(service.refresh()).thenThrow(new AutomationComparisonService.AutomationDisabledException());
        final MockMvc mockMvc = mockMvc(service);

        mockMvc.perform(get("/api/automation/comparison.json")
                        .header(HttpHeaders.AUTHORIZATION, basic("robot", "secret")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("disabled"));
    }

    private static MockMvc mockMvc(final AutomationComparisonService service) {
        return MockMvcBuilders.standaloneSetup(new AutomationController(service))
                .addFilters(new AutomationBasicAuthFilter(properties()))
                .build();
    }

    private static WebappComparisonProperties properties() {
        final WebappComparisonProperties properties = new WebappComparisonProperties();
        properties.getAutomation().setEnabled(true);
        properties.getAutomation().setUsername("robot");
        properties.getAutomation().setPassword("secret");
        return properties;
    }

    private static String basic(final String username, final String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    }
}
