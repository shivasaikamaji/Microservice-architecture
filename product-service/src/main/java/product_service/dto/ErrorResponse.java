package product_service.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard error response returned when an API request fails")
public class ErrorResponse {

    @Schema(
        description = "HTTP status code of the error",
        example = "404"
    )
    private int status;

    @Schema(
        description = "Description of the error",
        example = "Product not found with id: 101"
    )
    private String message;

    @Schema(
        description = "Date and time when the error occurred",
        example = "2026-09-11T12:30:00"
    )
    private LocalDateTime timestamp;

    public ErrorResponse() {
    }

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}