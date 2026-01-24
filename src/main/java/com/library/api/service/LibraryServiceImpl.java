package com.library.api.service;

import com.library.api.domain.*;
import com.library.api.domain.enums.BookCopyStatus;
import com.library.api.repository.*;
import com.library.api.service.exception.LoanException;
import com.library.api.dto.BookDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Transactional //Transactional annotation from Spring Framework
@Service
public class LibraryServiceImpl implements LibraryService {

  private static final long FEE_PER_DAY = 1L;

  BookRepository bookRepository;
  StudentRepository studentRepository;
  LoanRepository loanRepository;
  PenaltyRepository penaltyRepository;
  BookCopyRepository bookCopyRepository;

  // DI injection by Constructor
  public LibraryServiceImpl(BookRepository bookRepository, StudentRepository studentRepository,
                            LoanRepository loanRepository, PenaltyRepository penaltyRepository,
                            BookCopyRepository bookCopyRepository)  {
    this.bookRepository = bookRepository;
    this.studentRepository = studentRepository;
    this.loanRepository = loanRepository;
    this.penaltyRepository = penaltyRepository;
    this.bookCopyRepository = bookCopyRepository;
  }

  public BookDto getBookByIdentifier(String isbn) {
    if (isbn == null) {
      return null;
    }
    System.out.println("Received request for ISBN: " + isbn);

    BookEntity bookEntity = bookRepository.findByIsbn(isbn);

    if (bookEntity == null) {
      throw new EntityNotFoundException("Book with ISBN " + isbn + " not found");
    }

    return convertEntityToDto(bookEntity);
  }

  @Override
  public BookDto createBook(BookDto bookDto) {
    if (bookDto == null) {
      return null;
    }

    BookEntity book = convertDtoToEntity(bookDto);

    if (bookRepository.findByIsbn(book.getIsbn()) != null) {
      throw new RuntimeException("A book with the same ISBN already exists");
    }

    // Create a phisic copy automatically
    // As BookEntity has CascadeType.ALL, while we save the book the copy also gets saved.
    BookCopyEntity automaticCopy = new BookCopyEntity();
    automaticCopy.setBook(book);
    automaticCopy.setStatus(BookCopyStatus.AVAILABLE);
    automaticCopy.setBarcode("AUTO-" + book.getIsbn());

    book.getCopies().add(automaticCopy);

    bookRepository.save(book);
    return convertEntityToDto(book);
  }



  private BookDto convertEntityToDto(BookEntity entity) {
    BookDto dto = new BookDto();

    // The DTO (Book) does not have a database ID
    dto.setIsbn(entity.getIsbn());
    dto.setTitle(entity.getTitle());
    dto.setAuthorName(entity.getAuthorName());
    dto.setPublicationYear(entity.getPublicationYear());

    return dto;
  }

  private BookEntity convertDtoToEntity(BookDto dto) {
    BookEntity entity = new BookEntity();

    entity.setIsbn(dto.getIsbn());
    entity.setTitle(dto.getTitle());
    entity.setAuthorName(dto.getAuthorName());
    entity.setPublicationYear(dto.getPublicationYear());

    return entity;
  }

  @Override
  public LoanEntity borrowBook(String isbn, Long studentId) {

    // Search book
    BookEntity bookEntity = bookRepository.findByIsbn(isbn);
    if (bookEntity == null) {
      throw new EntityNotFoundException("Book not found with ISBN: " + isbn);
    }

    // Search for available book copies
    List<BookCopyEntity> copies = bookCopyRepository.findByBookIsbnAndStatus(isbn, BookCopyStatus.AVAILABLE);
    if (copies.isEmpty()) {
      throw new LoanException("No available copies found for book: " + bookEntity.getTitle());
    }

    BookCopyEntity bookCopy = copies.get(0);
    bookCopy.setStatus(BookCopyStatus.LOANED);
    bookCopyRepository.save(bookCopy);

    // Get the student associated
    StudentEntity studentEntity = studentRepository.findById(studentId)
        .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));


    LoanEntity loanEntity = new LoanEntity();
    loanEntity.setBookCopy(bookCopy);
    loanEntity.setStudent(studentEntity);
    loanEntity.setLoanDate(LocalDate.now());
    loanEntity.setDueDate(LocalDate.now().plusDays(14));
    loanEntity.setActive(true);

    loanRepository.save(loanEntity);

    return loanEntity;
  }

  @Override
  public LoanEntity returnBook(Long loanId) {

    LoanEntity loanEntity = loanRepository.findById(loanId) // This unpacks the Optional object into a LoanEntity one
        .orElseThrow(() -> new EntityNotFoundException("Loan not found with id: " + loanId));

    if (!loanEntity.isActive()) { // Loan not currently active
      throw new LoanException("Book is not currently borrowed and active");
    }

    LocalDate now = LocalDate.now(); // To guarantee syncronization

    loanEntity.setActive(false);
    loanEntity.setReturnDate(now);
    loanEntity.getBookCopy().setStatus(BookCopyStatus.AVAILABLE);

    if (now.isAfter(loanEntity.getDueDate())) {
      PenaltyEntity penalty = new PenaltyEntity();
      penalty.setPenaltyDate(now);
      penalty.setAmount(calculatePenaltyAmount(loanEntity.getDueDate(), loanEntity.getReturnDate()));

      // Associate loan and penalty
      penalty.setLoan(loanEntity);
      loanEntity.setPenalty(penalty);

      penaltyRepository.save(penalty);
    }

    loanRepository.save(loanEntity);
    return loanEntity;
  }

  private long calculatePenaltyAmount(LocalDate dueDate, LocalDate returnDate) {
    if (dueDate == null || returnDate == null) {
      return 0;
    }

    long daysLate = ChronoUnit.DAYS.between(dueDate,  returnDate); // returnDate - dueDate
    if (daysLate <= 0) {
      return 0;
    }

    return daysLate * FEE_PER_DAY;
  }

}
