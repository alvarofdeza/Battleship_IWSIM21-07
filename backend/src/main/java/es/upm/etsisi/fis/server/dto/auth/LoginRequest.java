package es.upm.etsisi.fis.server.dto.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Peticion DTO para la autenticacion de credenciales exclusivamente mediante correo electronico y obtencion de token JWT.
 */
public class LoginRequest {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @JsonAlias({"username", "user"})
    private String email;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email != null ? email.trim().toLowerCase() : null;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
