package com.library.client;

import com.library.api.dto.BookDto;
import com.library.api.soap.LibrarySoapService;
import jakarta.xml.ws.Service;

import javax.xml.namespace.QName;
import java.net.URL;

public class ClientApp {

  public static void main(String[] args) {
    try {
      System.out.println("🚀 Starting SOAP Client (External company simulation)...");

      // WSDL location
      URL url = new URL("http://localhost:8080/library-soap/services/LibraryService?wsdl");

      // Service identification (from @WebService)
      QName qname = new QName("http://api.library.com/soap", "LibraryService");

      // Create service factory
      Service service = Service.create(url, qname);

      // Get service proxy
      LibrarySoapService libraryPort = service.getPort(LibrarySoapService.class);

      System.out.println("Connection established. Calling service...");
      System.out.println("-------------------------------------------------");

      // Use case 1: Create book
      System.out.println("Sending request: createBook...");
      BookDto newBook = new BookDto();
      newBook.setIsbn("SOAP-12345");
      newBook.setTitle("Harry Potter and the SOAP Mystery");
      newBook.setAuthorName("J.K. Rowling");
      newBook.setPublicationYear(2000);

      BookDto createdBook = libraryPort.createBook(newBook);
      System.out.println("Response: Book created with ISBN " + createdBook.getIsbn());

      // Use case 2: Borrow book
      System.out.println("\nSending request: borrowBook...");
      Long studentId = 1L; // User from import.sql

      Long loanId = libraryPort.borrowBook("SOAP-12345", studentId);
      System.out.println("Response: Loan created. Loan ID: " + loanId);

      // Use case 3: Return book
      System.out.println("\nSending request: returnBook...");
      String result = libraryPort.returnBook(loanId);
      System.out.println("Response: " + result);

      System.out.println("\n-------------------------------------------------");
      System.out.println("Process completed successfully!");

    } catch (Exception e) {
      System.err.println("ERROR: Is Tomcat running?");
      e.printStackTrace();
    }
  }
}
