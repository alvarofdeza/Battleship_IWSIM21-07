package es.upm.etsisi.fis.server.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Peticion DTO para solicitar el reenvio de un nuevo codigo PIN militar.
 */
public class ResendOtpRequest {

    @NotBlank(message = "Username or email is required")
    private String identifier;

    public ResendOtpRequest() {}

    public ResendOtpRequest(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
