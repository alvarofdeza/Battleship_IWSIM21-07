package es.upm.etsisi.fis.server.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Peticion DTO para la autenticacion de credenciales y obtencion de token JWT.
 */
public class LoginRequest {

    @NotBlank(message = "Username or email is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
