package com.library.api.service;

import com.library.api.domain.BookEntity;
import com.library.api.domain.LoanEntity;
import com.library.api.domain.PenaltyEntity;
import com.library.api.domain.StudentEntity;
import com.library.api.repository.BookRepository;
import com.library.api.repository.LoanRepository;
import com.library.api.repository.PenaltyRepository;
import com.library.api.repository.StudentRepository;
import com.library.api.service.exception.LoanException;
import com.library.api.ws.dto.Book;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Transactional //Transactional annotation from Spring Framework
@Service
public class LibraryServiceImpl implements LibraryService {

  private static final long FEE_PER_DAY = 1L;

  BookRepository bookRepository;
  StudentRepository studentRepository;
  LoanRepository loanRepository;
  PenaltyRepository penaltyRepository;

  // DI injection by Constructor
  public LibraryServiceImpl(BookRepository bookRepository, StudentRepository studentRepository,
                            LoanRepository loanRepository, PenaltyRepository penaltyRepository)  {
    this.bookRepository = bookRepository;
    this.studentRepository = studentRepository;
    this.loanRepository = loanRepository;
    this.penaltyRepository = penaltyRepository;
  }

  public Book getBookByIdentifier(String isbn) {
    if (isbn == null) {
      return null;
    }
    System.out.println("Received request for ISBN: " + isbn);

    BookEntity bookEntity = bookRepository.findByIsbn(isbn);

    if (bookEntity == null) {
      bookEntity = new BookEntity(isbn, "The Spring Journey with Gemini", "J.M.C Mentor", 2025);
      bookEntity = bookRepository.save(bookEntity);
    }

    return convertEntityToDto(bookEntity);
  }

  @Override
  public Book createBook(Book bookDto) {

    BookEntity book = convertDtoToEntity(bookDto);

    bookRepository.save(book);

    return convertEntityToDto(book);
  }



  private Book convertEntityToDto(BookEntity entity) {
    Book dto = new Book();

    // The DTO (Book) does not have a database ID
    dto.setIsbn(entity.getIsbn());
    dto.setTitle(entity.getTitle());
    dto.setAuthorName(entity.getAuthorName());
    dto.setPublicationYear(entity.getPublicationYear());

    return dto;
  }

  private BookEntity convertDtoToEntity(Book dto) {
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

    // Get the student associated
    StudentEntity studentEntity = studentRepository.findById(studentId)
        .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));

    // Validate availability
    if (loanRepository.existsByBookAndActiveTrue(bookEntity)) {
      throw new LoanException("Book is already borrowed and active");
    }

    LoanEntity loanEntity = new LoanEntity();
    loanEntity.setBook(bookEntity);
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
      throw new LoanException("Book is not borrowed and active yet");
    }

    LocalDate now = LocalDate.now(); // To guarantee syncronization

    loanEntity.setActive(false);
    loanEntity.setReturnDate(now);

    if (LocalDate.now().isAfter(loanEntity.getDueDate())) {
      PenaltyEntity penalty = new PenaltyEntity();
      penalty.setPenaltyDate(now);
      penalty.setAmount(calculatePenaltyAmount(loanEntity.getDueDate(), loanEntity.getReturnDate()));
      penalty.setLoan(loanEntity);

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
