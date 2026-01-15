package com.library.api.endpoint;

import com.library.api.domain.LoanEntity;
import com.library.api.service.LibraryService;
import com.library.api.service.exception.LoanException;
import com.library.api.ws.dto.*;

// 1. Annotation: Tells Spring that this class is a SOAP Endpoint.
import jakarta.validation.Valid;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
// 2. Key annotations to map the incoming XML message.
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;


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
   * - localPart: Must match the XML element name you defined: getBookRequest
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
   * - localPart: Must match the XML element name you defined: getBookRequest
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


  /**
   * Handles the SOAP borrowBook call.
   *
   * @PayloadRoot: KEY annotation. Maps the request to the method by namespace and root element name.
   * - namespace: Must match NAMESPACE_URI.
   * - localPart: Must match the XML element name you defined: borrowBookRequest
   *
   * @RequestPayload: Indicates the input argument is the Java object created from the XML.
   * @ResponsePayload: Indicates the return value should be serialized back to XML for the response.
   */
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "borrowBookRequest")
  @ResponsePayload
  public BorrowBookResponse borrowBook(@RequestPayload @Valid BorrowBookRequest request) {

    BorrowBookResponse response = new BorrowBookResponse();

    // We need to catch the errors from the Service here
    try {
      String isbn = request.getIsbn();
      Long studentId = request.getStudentId();

      LoanEntity loanEntity = libraryService.borrowBook(isbn, studentId);

      response.setSuccess(true);
      response.setMessage(String.format("Book %s borrowed successfully", loanEntity.getBook().getTitle()));
      response.setLoanId(loanEntity.getLoanId());

    } catch (Exception e) { // We must not stop the code flow
      response.setSuccess(false);
      response.setMessage(e.getMessage());

      System.err.println("Failed while proccessing the borrow: " +  e.getMessage());
      e.printStackTrace();
    }

    return response;
  }

  /**
   * Handles the SOAP borrowBook call.
   *
   * @PayloadRoot: KEY annotation. Maps the request to the method by namespace and root element name.
   * - namespace: Must match NAMESPACE_URI.
   * - localPart: Must match the XML element name you defined: returnBookRequest
   *
   * @RequestPayload: Indicates the input argument is the Java object created from the XML.
   * @ResponsePayload: Indicates the return value should be serialized back to XML for the response.
   */
  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "returnBookRequest")
  @ResponsePayload
  public ReturnBookResponse borrowBook(@RequestPayload @Valid ReturnBookRequest request) {
    ReturnBookResponse response = new ReturnBookResponse();

    try {
      Long loanId = request.getLoanId();
      LoanEntity loanEntity = libraryService.returnBook(loanId);

      response.setSuccess(true);
      response.setMessage(String.format("Book %s returned successfully", loanEntity.getBook().getTitle()));

      LocalDate returnDate = loanEntity.getReturnDate();
      if (returnDate != null) {
        XMLGregorianCalendar xmlDate = toXmlDate(returnDate);
        response.setReturnDate(xmlDate);
      }

    } catch (Exception e) {
      response.setSuccess(false);
      response.setMessage(e.getMessage());

      System.err.println("Failed while proccessing the return: " +  e.getMessage());
      e.printStackTrace();
    }

    return response;
  }

  private XMLGregorianCalendar toXmlDate(LocalDate localDate) {
    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());
    } catch (Exception e) {
      System.err.println("Failed while converting the date to xmlDate: " +  e.getMessage());
      return null; // As the xsd contract have minOcurrs prop, return null here is safe
    }
  }

}