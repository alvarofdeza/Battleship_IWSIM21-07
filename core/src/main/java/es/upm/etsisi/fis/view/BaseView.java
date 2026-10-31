package es.upm.etsisi.fis.view;

/**
 * Contrato base común para las vistas de la aplicación.
 */
public interface BaseView {
    void showSuccessMessage(String message);
    void showErrorMessage(String message);
}
