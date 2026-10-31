package es.upm.etsisi.fis.server.dto.auth;

/**
 * Respuesta DTO con el token JWT de acceso concedido y perfil de sesion.
 */
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private String role;
    private boolean emailVerified;

    public AuthResponse() {}

    public AuthResponse(String token, String username, String email, String role) {
        this(token, username, email, role, true);
    }

    public AuthResponse(String token, String username, String email, String role, boolean emailVerified) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
        this.emailVerified = emailVerified;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
