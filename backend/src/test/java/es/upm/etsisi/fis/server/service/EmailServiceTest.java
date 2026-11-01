package es.upm.etsisi.fis.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para el servicio de despacho de correos electronicos via Brevo API.
 */
class EmailServiceTest {

    @Test
    @DisplayName("Modo Mock cuando la clave de API esta vacia no realiza peticiones HTTP")
    void testMockWhenApiKeyEmpty() throws Exception {
        HttpClient mockClient = mock(HttpClient.class);
        EmailService service = new EmailService(mockClient, new ObjectMapper(), "", "fleetgrid.command@gmail.com");

        service.sendVerificationCode("officer@naval.org", "Admiral", "123456");

        verifyNoInteractions(mockClient);
    }

    @Test
    @DisplayName("Modo Mock cuando el destinatario termina en @test.com no realiza peticiones HTTP")
    void testMockWhenTestEmail() throws Exception {
        HttpClient mockClient = mock(HttpClient.class);
        EmailService service = new EmailService(mockClient, new ObjectMapper(), "xkeysib-mock-key", "fleetgrid.command@gmail.com");

        service.sendVerificationCode("cadet@test.com", "Cadet", "654321");

        verifyNoInteractions(mockClient);
    }

    @Test
    @DisplayName("Generacion de plantilla HTML tactica contiene nombre de usuario y PIN militar")
    void testBuildTacticalEmailHtml() {
        EmailService service = new EmailService(mock(HttpClient.class), new ObjectMapper(), "", "fleetgrid.command@gmail.com");

        String html = service.buildTacticalEmailHtml("Nimitz", "789012");

        assertNotNull(html);
        assertTrue(html.contains("Nimitz"));
        assertTrue(html.contains("789012"));
        assertTrue(html.contains("FLEETGRID // FLEET COMMAND"));
    }

    @Test
    @DisplayName("Despacho exitoso via Brevo API genera peticion HTTP POST con encabezados correctos")
    @SuppressWarnings("unchecked")
    void testSendVerificationCodeHttpSuccess() throws Exception {
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(201);
        when(mockResponse.body()).thenReturn("{\"messageId\":\"<msg-123>\"}");
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);

        EmailService service = new EmailService(mockClient, new ObjectMapper(), "xkeysib-secret-token", "fleetgrid.command@gmail.com");
        service.sendVerificationCode("realuser@fleetgrid.org", "Commander", "998877");

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockClient, times(1)).send(requestCaptor.capture(), any());

        HttpRequest sentRequest = requestCaptor.getValue();
        assertEquals("POST", sentRequest.method());
        assertEquals("https://api.brevo.com/v3/smtp/email", sentRequest.uri().toString());
        assertEquals("xkeysib-secret-token", sentRequest.headers().firstValue("api-key").orElse(""));
        assertEquals("application/json", sentRequest.headers().firstValue("content-type").orElse(""));
    }

    @Test
    @DisplayName("Fallo HTTP en Brevo se captura con resiliencia sin lanzar excepcion a la capa superior")
    @SuppressWarnings("unchecked")
    void testSendVerificationCodeHttpFailureHandledGracefully() throws Exception {
        HttpClient mockClient = mock(HttpClient.class);
        when(mockClient.send(any(HttpRequest.class), any())).thenThrow(new IOException("Connection reset"));

        EmailService service = new EmailService(mockClient, new ObjectMapper(), "xkeysib-secret-token", "fleetgrid.command@gmail.com");

        assertDoesNotThrow(() -> service.sendVerificationCode("error@fleetgrid.org", "Ensign", "112233"));
    }
}
