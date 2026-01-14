package com.library.api.service;

import com.library.api.domain.LoanEntity;
import com.library.api.ws.dto.Book;

public interface LibraryService {

  // Interface based on the logic defined in library.xsd

  Book getBookByIdentifier(String isbn);

  Book createBook(Book bookDto);

  LoanEntity borrowBook(String isbn, Long Id);

  LoanEntity returnBook(Long loanId);

}
