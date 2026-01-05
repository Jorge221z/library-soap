package com.library.api.endpoint;

import com.library.api.service.LibraryService;
import com.library.api.ws.dto.*;

// 1. Annotation: Tells Spring that this class is a SOAP Endpoint.
import jakarta.validation.Valid;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
// 2. Key annotations to map the incoming XML message.
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@Endpoint
public class LibraryEndpoint {

  // The TARGET_NAMESPACE must match what we defined in the XSD and in spring-ws-context.xml
  private static final String NAMESPACE_URI = "http://api.library.com/ws";

  // Constructor injection
  private final LibraryService libraryService;

  public LibraryEndpoint(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  /**
   * Handles the SOAP GetBookRequest.
   *
   * @PayloadRoot: KEY annotation. Maps the request to the method by namespace and root element name.
   * - namespace: Must match NAMESPACE_URI.
   * - localPart: Must match the XML element name you defined: <getBookRequest>.
   *
   * @RequestPayload: Indicates the input argument is the Java object created from the XML.
   * @ResponsePayload: Indicates the return value should be serialized back to XML for the response.
   */
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBookRequest")
  @ResponsePayload
  public GetBookResponse getBook(@RequestPayload @Valid GetBookRequest request) {

    // --- 1. Simulate searching for the book using our service --- //
    String isbn = request.getIsbn();
    Book book = libraryService.getBookByIdentifier(isbn);

    // --- 2. Create the response ---
    GetBookResponse response = new GetBookResponse();

    response.setBook(book);

    // The Java response will be automatically converted to XML by JAXB.
    return response;
  }


  /**
   * Handles the SOAP createBookRequest.
   *
   * @PayloadRoot: KEY annotation. Maps the request to the method by namespace and root element name.
   * - namespace: Must match NAMESPACE_URI.
   * - localPart: Must match the XML element name you defined: <getBookRequest>.
   *
   * @RequestPayload: Indicates the input argument is the Java object created from the XML.
   * @ResponsePayload: Indicates the return value should be serialized back to XML for the response.
   */
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createBookRequest")
  @ResponsePayload
  public CreateBookResponse getBook(@RequestPayload @Valid CreateBookRequest request) {

    // --- 1. Create the book using our service --- //
    Book book = request.getBook();
    libraryService.createBook(book);

    // --- 2. Create the response ---
    CreateBookResponse response = new CreateBookResponse();
    response.setBook(book);

    // The Java response will be automatically converted to XML by JAXB.
    return response;
  }
}