package es.upm.etsisi.fis.server.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Peticion DTO para la validacion del codigo PIN militar de 6 digitos enviado por correo.
 */
public class VerifyEmailRequest {

    @NotBlank(message = "Username or email is required")
    private String identifier;

    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Verification code must be exactly 6 digits")
    private String code;

    public VerifyEmailRequest() {}

    public VerifyEmailRequest(String identifier, String code) {
        this.identifier = identifier;
        this.code = code;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
