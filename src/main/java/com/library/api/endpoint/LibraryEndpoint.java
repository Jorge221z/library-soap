package com.library.api.endpoint;

import com.library.api.ws.dto.GetBookRequest;
import com.library.api.ws.dto.GetBookResponse;
import com.library.api.ws.dto.Book;

// 1. Anotación: Indica a Spring que esta clase es un Endpoint SOAP.
import org.springframework.ws.server.endpoint.annotation.Endpoint;
// 2. Anotaciones clave para mapear el mensaje XML entrante.
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.math.BigInteger;

@Endpoint
public class LibraryEndpoint {

  // El TARGET_NAMESPACE debe coincidir con el que definimos en el XSD y en el spring-ws-context.xml
  private static final String NAMESPACE_URI = "http://api.library.com/ws";

  /**
   * Maneja la petición SOAP GetBookRequest.
   *
   * @PayloadRoot: Anotación CLAVE. Mapea la petición al método por el namespace y el nombre del elemento raíz.
   * - namespace: Debe coincidir con el NAMESPACE_URI.
   * - localPart: Debe coincidir con el nombre del elemento XML que definiste: <getBookRequest>.
   *
   * @RequestPayload: Indica que el argumento de entrada es el objeto Java creado a partir del XML.
   * @ResponsePayload: Indica que el valor de retorno debe ser serializado de vuelta a XML para la respuesta.
   */
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBookRequest")
  @ResponsePayload
  public GetBookResponse getBook(@RequestPayload GetBookRequest request) {

    // --- 1. Lógica del ENDPOINT ---
    // Aquí es donde típicamente llamaríamos a la capa Service para la lógica de negocio.
    // Por ahora, simularemos la lógica.
    System.out.println("Received request for ISBN: " + request.getIsbn());

    // --- 2. Creación de la Respuesta ---
    GetBookResponse response = new GetBookResponse();
    Book book = new Book();
    book.setIsbn(request.getIsbn());
    book.setTitle("The Spring Journey with Gemini");
    book.setAuthorName("J.M.C Mentor");
    book.setPublicationYear(2025); // Usando el Integer que corregimos

    response.setBook(book);

    // La respuesta Java será automáticamente convertida a XML por JAXB.
    return response;
  }
}