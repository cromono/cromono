package gabia.cronMonitoring.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gabia.cronMonitoring.dto.request.WebhookDTO;
import gabia.cronMonitoring.dto.response.WebhookInfoDTO;
import gabia.cronMonitoring.entity.Enum.WebhookEndpoint;
import gabia.cronMonitoring.exception.webhook.ExistingWebhookException;
import gabia.cronMonitoring.exception.webhook.NoticeSubscriptionNotFoundException;
import gabia.cronMonitoring.exception.webhook.WebhookNotFoundException;
import gabia.cronMonitoring.service.WebhookSubscriptionService;
import gabia.cronMonitoring.util.jwt.JwtAccessDeniedHandler;
import gabia.cronMonitoring.util.jwt.JwtAuthenticationEntryPoint;
import gabia.cronMonitoring.util.jwt.TokenProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WebhookController.class)
@WithMockUser(roles = "USER")
class WebhookControllerTest {

    @MockBean
    WebhookSubscriptionService webhookSubscriptionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Test
    public void ??????_??????_GET_??????() throws Exception {
        // Given
        String userId = "test";
        UUID cronJobId = UUID.randomUUID();
        List<WebhookInfoDTO> response = new ArrayList<>();
        response.add(WebhookInfoDTO.builder()
            .id(1L)
            .url("test")
            .endPoint(WebhookEndpoint.SLACK)
            .build());
        response.add(WebhookInfoDTO.builder()
            .id(2L)
            .url("test")
            .endPoint(WebhookEndpoint.HIWORKS)
            .build());
        // When
        when(webhookSubscriptionService.getWebhooks(userId, cronJobId)).thenReturn(response);
        // Then
        mockMvc.perform(
            get("/notifications/users/{userId}/crons/{cronJobId}/webhooks", userId, cronJobId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value(response.get(0)))
            .andExpect(jsonPath("$[1]").value(response.get(1)))
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void ??????_??????_GET_??????() throws Exception {
        // Given
        String userId = "test";
        UUID cronJobId = UUID.randomUUID();
        // When
        when(webhookSubscriptionService.getWebhooks(userId, cronJobId)).thenThrow(
            NoticeSubscriptionNotFoundException.class);
        // Then
        mockMvc.perform(
            get("/notifications/users/{userId}/crons/{cronJobId}/webhooks", userId, cronJobId))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void ??????_??????_POST_??????() throws Exception {
        // Given
        String userId = "test";
        UUID cronJobId = UUID.randomUUID();
        WebhookDTO request = WebhookDTO.builder()
            .url("test")
            .endPoint(WebhookEndpoint.SLACK)
            .build();
        WebhookInfoDTO response = WebhookInfoDTO.builder()
            .id(1L)
            .endPoint(request.getEndPoint())
            .url(request.getUrl())
            .build();
        // When
        when(webhookSubscriptionService.addWebhook(userId, cronJobId, request))
            .thenReturn(response);
        // Then
        mockMvc.perform(
            post("/notifications/users/{userId}/crons/{cronJobId}/webhooks", userId, cronJobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$").value(response));
    }

    @Test
    public void ????????????_??????_?????????_??????_??????_??????_POST_??????() throws Exception {
        // Given
        String userId = "test";
        UUID cronJobId = UUID.randomUUID();
        WebhookDTO request = WebhookDTO.builder()
            .url("test")
            .endPoint(WebhookEndpoint.SLACK)
            .build();
        // When
        when(webhookSubscriptionService.addWebhook(userId, cronJobId, request))
            .thenThrow(NoticeSubscriptionNotFoundException.class);
        // Then
        mockMvc.perform(
            post("/notifications/users/{userId}/crons/{cronJobId}/webhooks", userId, cronJobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void ??????_?????????_??????_??????_POST_??????() throws Exception {
        // Given
        String userId = "test";
        UUID cronJobId = UUID.randomUUID();
        WebhookDTO request = WebhookDTO.builder()
            .url("test")
            .endPoint(WebhookEndpoint.SLACK)
            .build();
        // When
        when(webhookSubscriptionService.addWebhook(userId, cronJobId, request)).thenThrow(
            ExistingWebhookException.class);
        // Then
        mockMvc.perform(
            post("/notifications/users/{userId}/crons/{cronJobId}/webhooks", userId, cronJobId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @Test
    public void ??????_??????_PATCH_??????() throws Exception {
        // Given
        String userId = "test";
        UUID cronJobId = UUID.randomUUID();
        Long webhookId = 1L;
        WebhookDTO request = WebhookDTO.builder()
            .url("test")
            .endPoint(WebhookEndpoint.SLACK)
            .build();
        WebhookInfoDTO response = WebhookInfoDTO.builder()
            .id(webhookId)
            .endPoint(request.getEndPoint())
            .url(request.getUrl())
            .build();
        // When
        when(webhookSubscriptionService.updateWebhook(userId, cronJobId, webhookId, request))
            .thenReturn(response);
        // Then
        mockMvc.perform(
            patch("/notifications/users/{userId}/crons/{cronJobId}/webhooks/{webhookId}", userId,
                cronJobId, webhookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(response));
    }

    @Test
    public void ????????????_??????_?????????_??????_??????_??????_PATCH_??????() throws Exception {
        // Given
        String userId = "test";
        UUID cronJobId = UUID.randomUUID();
        Long webhookId = 1L;
        WebhookDTO request = WebhookDTO.builder()
            .url("test")
            .endPoint(WebhookEndpoint.SLACK)
            .build();
        // When
        when(webhookSubscriptionService.updateWebhook(userId, cronJobId, webhookId, request))
            .thenThrow(NoticeSubscriptionNotFoundException.class);
        // Then
        mockMvc.perform(
            patch("/notifications/users/{userId}/crons/{cronJobId}/webhooks/{webhookId}", userId,
                cronJobId, webhookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void ??????_?????????_????????????_?????????_PATCH_??????() throws Exception {
        // Given
        String userId = "test";
        UUID cronJobId = UUID.randomUUID();
        Long webhookId = 1L;
        WebhookDTO request = WebhookDTO.builder()
            .url("test")
            .endPoint(WebhookEndpoint.SLACK)
            .build();
        // When
        when(webhookSubscriptionService.updateWebhook(userId, cronJobId, webhookId, request))
            .thenThrow(ExistingWebhookException.class);
        // Then
        mockMvc.perform(
            patch("/notifications/users/{userId}/crons/{cronJobId}/webhooks/{webhookId}", userId,
                cronJobId, webhookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isConflict());
    }

    @Test
    public void ????????????_??????_?????????_PATCH_??????() throws Exception {
        // Given
        String userId = "test";
        UUID cronJobId = UUID.randomUUID();
        Long webhookId = 1L;
        WebhookDTO request = WebhookDTO.builder()
            .url("test")
            .endPoint(WebhookEndpoint.SLACK)
            .build();
        // When
        when(webhookSubscriptionService.updateWebhook(userId, cronJobId, webhookId, request))
            .thenThrow(
                WebhookNotFoundException.class);
        // Then
        mockMvc.perform(
            patch("/notifications/users/{userId}/crons/{cronJobId}/webhooks/{webhookId}", userId,
                cronJobId, webhookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void ??????_??????_??????_DELETE_??????() throws Exception {
        // Given
        String userId = "test";
        UUID cronJobId = UUID.randomUUID();
        Long webhookId = 1L;
        // When
        doNothing().when(webhookSubscriptionService).deleteWebhookById(webhookId);
        // Then
        mockMvc.perform(
            delete("/notifications/users/{userId}/crons/{cronJobId}/webhooks/{webhookId}", userId,
                cronJobId, webhookId))
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    @Test
    public void ?????????_??????_?????????_DELETE_??????() throws Exception {
        // Given
        String userId = "test";
        UUID cronJobId = UUID.randomUUID();
        Long webhookId = 1L;
        // When
        doThrow(WebhookNotFoundException.class).when(webhookSubscriptionService).deleteWebhookById(webhookId);
        // Then
        mockMvc.perform(
            delete("/notifications/users/{userId}/crons/{cronJobId}/webhooks/{webhookId}", userId,
                cronJobId, webhookId))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
    public void ?????????_??????_??????_??????_??????_DELETE_??????() throws Exception {
        // Given
        String userId = "test";
        UUID cronJobId = UUID.randomUUID();
        // When
        doNothing().when(webhookSubscriptionService).deleteWebhooks(userId, cronJobId);
        // Then
        mockMvc.perform(
            delete("/notifications/users/{userId}/crons/{cronJobId}/webhooks", userId,
                cronJobId))
            .andDo(print())
            .andExpect(status().isNoContent());
    }

    @Test
    public void ????????????_??????_?????????_??????_??????_??????_?????????_DELETE_??????() throws Exception {
        // Given
        String userId = "test";
        UUID cronJobId = UUID.randomUUID();
        // When
        doThrow(NoticeSubscriptionNotFoundException.class).when(webhookSubscriptionService).deleteWebhooks(userId, cronJobId);
        // Then
        mockMvc.perform(
            delete("/notifications/users/{userId}/crons/{cronJobId}/webhooks", userId,
                cronJobId))
            .andDo(print())
            .andExpect(status().isNotFound());
    }
}