package com.Cross_BorderDataTransferManager.backend;

import com.Cross_BorderDataTransferManager.backend.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleIllegalArgument_shouldReturnBadRequestBody() {
        var response = handler.handleIllegalArgument(new IllegalArgumentException("Bad date"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("message")).isEqualTo("Bad date");
    }

    @Test
    void handleMaxUploadSize_shouldReturnPayloadTooLarge() {
        var response = handler.handleMaxUploadSize(new MaxUploadSizeExceededException(5));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @Test
    void handleAllExceptions_shouldReturnInternalServerError() {
        var response = handler.handleAllExceptions(new RuntimeException("Boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("message")).isEqualTo("Boom");
    }
}
