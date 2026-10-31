package servidor;

import javax.swing.JOptionPane;
import java.awt.GraphicsEnvironment;

/**
 * Emulador ligero del servicio de autenticacion externa LDAP para el cliente de escritorio legacy.
 * Proporciona el contrato necesario para desacoplar el modulo core de bibliotecas JAR externas obsoletas.
 */
public class ExternalLDAP {

    private static boolean interactiveEnabled = !GraphicsEnvironment.isHeadless();

    /**
     * Constructor por defecto.
     */
    public ExternalLDAP() {
    }

    /**
     * Configura si el emulador debe intentar abrir ventanas graficas de dialogo.
     *
     * @param enabled Verdadero para permitir dialogos interactivos, falso para modo silencioso.
     */
    public static void setInteractiveEnabled(boolean enabled) {
        interactiveEnabled = enabled;
    }

    /**
     * Indica si el modo interactivo se encuentra activo.
     *
     * @return Verdadero si interactua graficamente, falso si opera en modo headless o silencioso.
     */
    public static boolean isInteractiveEnabled() {
        return interactiveEnabled;
    }

    /**
     * Solicita el identificador de usuario LDAP mediante cuadro de dialogo grafico si no se ejecuta en entorno headless.
     *
     * @return Identificador alfanumerico del usuario o null si se cancela o si el entorno carece de interfaz grafica.
     */
    public static String LoginLDAP() {
        if (interactiveEnabled && !GraphicsEnvironment.isHeadless()) {
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
