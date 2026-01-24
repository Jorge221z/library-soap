package com.library.api.soap;

import com.library.api.dto.BookDto;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

// This annotation generates a WSDL on the given url from this interface
@WebService(targetNamespace = "http://api.library.com/soap")
public interface LibrarySoapService {

  @WebMethod(operationName = "getBook")
  @WebResult(name = "book")
  BookDto getBook(@WebParam(name = "isbn") String isbn);

  @WebMethod(operationName = "createBook")
  @WebResult(name = "createdBook")
  BookDto createBook(@WebParam(name = "book") BookDto book);

  @WebMethod(operationName = "borrowBook")
  @WebResult(name = "borrowedBook")
  Long borrowBook(
      @WebParam(name = "isbn") String isbn,
      @WebParam(name = "studentId") Long studentId
  );

  @WebMethod(operationName = "returnBook")
  @WebResult(name = "returnedBook")
  String returnBook(@WebParam(name = "loanId") Long loanId);

}
