package com.h3.reservation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.h3.reservation.slack.RequestType;
import com.h3.reservation.slack.dto.request.BlockActionRequest;
import com.h3.reservation.slack.dto.request.EventCallbackRequest;
import com.h3.reservation.slack.dto.request.VerificationRequest;
import com.h3.reservation.slack.dto.request.viewsubmission.CancelRequest;
import com.h3.reservation.slack.dto.request.viewsubmission.ChangeRequest;
import com.h3.reservation.slack.dto.request.viewsubmission.ReserveRequest;
import com.h3.reservation.slack.dto.request.viewsubmission.RetrieveRequest;
import com.h3.reservation.slack.dto.response.common.ModalClearResponse;
import com.h3.reservation.slack.dto.response.common.ModalSubmissionResponse;
import com.h3.reservation.slack.dto.response.common.ModalSubmissionType;
import com.h3.reservation.slack.service.SlackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * @author heebg
 * @version 1.0
 * @date 2019-12-02
 */
@RestController
public class BotController {
    private static final Logger logger = LoggerFactory.getLogger(BotController.class);

    private static final String TYPE = "type";
    private static final String PAYLOAD = "payload";

    private final ObjectMapper objectMapper;
    private final SlackService service;

    public BotController(ObjectMapper objectMapper, SlackService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @PostMapping("/slack/action")
    public ResponseEntity<String> action(@RequestBody JsonNode reqJson) throws JsonProcessingException {
        switch (RequestType.of(reqJson.get(TYPE).asText())) {
            case URL_VERIFICATION:
                return ResponseEntity.ok(service.verify(jsonToDto(reqJson, VerificationRequest.class)));
            case EVENT_CALLBACK:
                service.showMenu(jsonToDto(reqJson, EventCallbackRequest.class));
                return ResponseEntity.ok().build();
            default:
                return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/slack/interaction")
    public ResponseEntity<?> interaction(@RequestParam Map<String, String> req) throws IOException {
        JsonNode reqJson = objectMapper.readTree(req.get(PAYLOAD));
        switch (RequestType.of(reqJson.get(TYPE).asText())) {
            case BLOCK_ACTIONS:
                service.showModal(jsonToDto(reqJson, BlockActionRequest.class));
                return ResponseEntity.ok().build();
            case VIEW_SUBMISSION:
                return ResponseEntity.ok(Objects.requireNonNull(generateModalSubmissionResponse(reqJson)));
            default:
                return ResponseEntity.badRequest().build();
        }
    }

    private ModalSubmissionResponse generateModalSubmissionResponse(JsonNode reqJson) throws IOException {
        switch (ModalSubmissionType.of(reqJson.get("view").get("callback_id").asText())) {
            case RETRIEVE_START:
                return service.pushRetrieveFirstModal(jsonToDto(reqJson, RetrieveRequest.class));
            case RESERVE_START:
                return service.pushReserveFirstModal(jsonToDto(reqJson, ReserveRequest.class));
            case CHANGE_AND_CANCEL_START:
                return service.pushChangeAndCancelFirstModal(jsonToDto(reqJson, ChangeRequest.class));
            case CHANGE_SECOND_PUSH:
                return service.updateChangeFinishedModal(jsonToDto(reqJson, ReserveRequest.class));
            case CANCEL_SECOND_PUSH:
                return service.updateCancelFinishedModal(jsonToDto(reqJson, CancelRequest.class));
        }
        return new ModalClearResponse();
    }

    private <T> T jsonToDto(JsonNode json, Class<T> type) throws JsonProcessingException {
        return objectMapper.treeToValue(json, type);
    }
}
