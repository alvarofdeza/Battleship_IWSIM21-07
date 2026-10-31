package servidor;

import javax.swing.JOptionPane;
import java.awt.GraphicsEnvironment;

/**
 * Emulador ligero del servicio de autenticacion externa LDAP para el cliente de escritorio legacy.
 * Proporciona el contrato necesario para desacoplar el modulo core de bibliotecas JAR externas obsoletas.
 */
public class ExternalLDAP {

    /**
     * Constructor por defecto.
     */
    public ExternalLDAP() {
    }

    /**
     * Solicita el identificador de usuario LDAP mediante cuadro de dialogo grafico si no se ejecuta en entorno headless.
     *
     * @return Identificador alfanumerico del usuario o null si se cancela o si el entorno carece de interfaz grafica.
     */
    public static String LoginLDAP() {
        if (!GraphicsEnvironment.isHeadless()) {
            try {
                String input = JOptionPane.showInputDialog(
                        null,
                        "Enter LDAP User Identifier (e.g. admin, alvaro):",
                        "LDAP Authentication Service",
                        JOptionPane.QUESTION_MESSAGE
                );
                return (input != null && !input.trim().isEmpty()) ? input.trim() : null;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
