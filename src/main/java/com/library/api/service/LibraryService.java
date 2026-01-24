package com.library.api.service;

import com.library.api.domain.LoanEntity;
import com.library.api.dto.BookDto;

public interface LibraryService {

  // Interface based on the logic defined in library.xsd

  BookDto getBookByIdentifier(String isbn);

  BookDto createBook(BookDto bookDto);

  LoanEntity borrowBook(String isbn, Long Id);

  LoanEntity returnBook(Long loanId);

}
