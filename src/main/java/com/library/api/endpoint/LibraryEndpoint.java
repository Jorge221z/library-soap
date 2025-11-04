package com.library.api.endpoint;

import com.library.api.service.LibraryService;
import com.library.api.ws.dto.GetBookRequest;
import com.library.api.ws.dto.GetBookResponse;
import com.library.api.ws.dto.Book;

// 1. Anotación: Indica a Spring que esta clase es un Endpoint SOAP.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
// 2. Anotaciones clave para mapear el mensaje XML entrante.
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@Endpoint
public class LibraryEndpoint {

  // El TARGET_NAMESPACE debe coincidir con el que definimos en el XSD y en el spring-ws-context.xml
  private static final String NAMESPACE_URI = "http://api.library.com/ws";

  // Inyeccion por constructor
  private final LibraryService libraryService;

  public LibraryEndpoint(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

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

    // --- 1. Simulamos la busqueda del libro con nuestro service --- //
    String isbn = request.getIsbn();
    Book book = libraryService.getBookByIdentifier(isbn);

    // --- 2. Creación de la Respuesta ---
    GetBookResponse response = new GetBookResponse();

    response.setBook(book);

    // La respuesta Java será automáticamente convertida a XML por JAXB.
    return response;
  }
}