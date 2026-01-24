package com.library.api.soap;


import com.library.api.domain.LoanEntity;
import com.library.api.dto.BookDto;
import com.library.api.service.LibraryService;
import jakarta.jws.WebService;
import org.springframework.stereotype.Component;

@Component
@WebService(endpointInterface = "com.library.api.soap.LibrarySoapService", serviceName = "LibraryService")
public class LibrarySoapServiceImpl implements LibrarySoapService {

  // This class replicates our old LibraryEnpoint.
  // We only have to manage the data, Apache CXF is in charge of the XMLs.
  // Our business logic lives on the LibraryService, we implement it here.

  private final LibraryService libraryService;

  public LibrarySoapServiceImpl(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  @Override
  public BookDto getBook(String isbn) {
    return libraryService.getBookByIdentifier(isbn);
  }

  @Override
  public BookDto createBook(BookDto book) {
    return libraryService.createBook(book);
  }

  @Override
  public Long borrowBook(String isbn, Long studentId) {
    LoanEntity loan = libraryService.borrowBook(isbn, studentId);

    // Only return the ID (CXF converts it into XML)
    return loan.getLoanId();
  }

  @Override
  public String returnBook(Long loanId) {
    LoanEntity loan = libraryService.returnBook(loanId);
    if (loan.getPenalty() != null) {
      return "Book returned late. Penalty amount: " + loan.getPenalty().getAmount();
    }
    return "Book returned on time.";
  }



}
