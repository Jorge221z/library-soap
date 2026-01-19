package com.library.api.service;

import com.library.api.domain.BookCopyEntity;
import com.library.api.domain.BookEntity;
import com.library.api.domain.LoanEntity;
import com.library.api.domain.StudentEntity;
import com.library.api.domain.enums.BookCopyStatus;
import com.library.api.repository.*;
import com.library.api.service.exception.LoanException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Activate Mockito
class LibraryServiceImplTest {

  // Mock our repositories
  @Mock
  private BookRepository bookRepository;
  @Mock
  private StudentRepository studentRepository;
  @Mock
  private LoanRepository loanRepository;
  @Mock
  private PenaltyRepository penaltyRepository;
  @Mock
  private BookCopyRepository bookCopyRepository;

  // Instance our real service and inject the above mocks
  @InjectMocks
  private LibraryServiceImpl libraryService;

  // --- TESTING CASES ---

  // CASE 1: Borrow Book Happy Path
  @Test
  void borrowBook_ShouldReturnLoan_WhenDataIsCorrect() {
    String isbn = "978-3-16-148410-0";
    Long studentId = 4L;

    BookEntity bookEntity = new BookEntity(isbn, "El Camino", "Bukowski", 2010);
    StudentEntity studentEntity = new StudentEntity(studentId, "Jorge", "jorge@library.com", null);

    BookCopyEntity bookCopyEntity = new BookCopyEntity();
    bookCopyEntity.setId(10L);
    bookCopyEntity.setBook(bookEntity);
    bookCopyEntity.setStatus(BookCopyStatus.AVAILABLE);

    // 'Train' our mocks
    when(bookRepository.findByIsbn(isbn)).thenReturn(bookEntity);
    when(studentRepository.findById(studentId)).thenReturn(Optional.of(studentEntity));
    when(bookCopyRepository.findByBookIsbnAndStatus(isbn, BookCopyStatus.AVAILABLE)).thenReturn(List.of(bookCopyEntity));
    when(loanRepository.save(any(LoanEntity.class))).thenAnswer(i -> i.getArgument(0));

    LoanEntity result = libraryService.borrowBook(isbn, studentId);

    Assertions.assertNotNull(result);
    Assertions.assertNotNull(result.getBookCopy());

    Assertions.assertEquals(studentId, result.getStudent().getId());
    Assertions.assertEquals(isbn, result.getBookCopy().getBook().getIsbn());
    Assertions.assertEquals(bookEntity, result.getBookCopy().getBook());

    Assertions.assertEquals(BookCopyStatus.LOANED, result.getBookCopy().getStatus());
    Assertions.assertTrue(result.isActive()); // Now should be true

    verify(bookCopyRepository, times(1)).findByBookIsbnAndStatus(isbn, BookCopyStatus.AVAILABLE);
    verify(bookCopyRepository, times(1)).save(any(BookCopyEntity.class));
    verify(loanRepository, times(1)).save(any(LoanEntity.class));
  }

  // CASE 2: Book does not exist
  @Test
  void borrowBook_ShouldThrowException_WhenBookNotFound() {
    String isbn = "978-3-16-148410-0";

    when(bookRepository.findByIsbn(isbn)).thenReturn(null); // Force the mock to do not find any book

    Assertions.assertThrows(EntityNotFoundException.class, () -> libraryService.borrowBook(isbn, null));

    verify(bookRepository, times(1)).findByIsbn(isbn);
    verify(studentRepository, times(0)).findById(anyLong());
    verify(loanRepository, times(0)).save(any(LoanEntity.class));
    verify(bookCopyRepository, never()).findByBookIsbnAndStatus(isbn, BookCopyStatus.AVAILABLE);
  }

  // CASE 3: Student does not exist
  @Test
  void borrowBook_ShouldThrowException_WhenStudentNotFound() {
    String isbn = "978-3-16-148410-0";
    BookEntity bookEntity = new BookEntity(isbn, "El Camino", "Bukowski", 2010);
    Long studentId = 4L;

    BookCopyEntity bookCopyEntity = new BookCopyEntity();
    bookCopyEntity.setId(10L);
    bookCopyEntity.setBook(bookEntity);
    bookCopyEntity.setStatus(BookCopyStatus.AVAILABLE);

    when(bookRepository.findByIsbn(isbn)).thenReturn(bookEntity);
    when(bookCopyRepository.findByBookIsbnAndStatus(isbn, BookCopyStatus.AVAILABLE)).thenReturn(List.of(bookCopyEntity));
    when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

    Assertions.assertThrows(EntityNotFoundException.class, () -> libraryService.borrowBook(isbn, studentId));

    verify(bookRepository, times(1)).findByIsbn(isbn);
    verify(studentRepository, times(1)).findById(anyLong());
    verify(loanRepository, times(0)).save(any(LoanEntity.class));
  }

  // CASE 4: Book copy already loaned (business logic)
  @Test
  void borrowBook_ShouldThrowLoanException_WhenNoCopiesAvailable() {
    String isbn = "978-3-16-148410-0";
    long studentId = 4L;
    BookEntity bookEntity = new BookEntity(isbn, "El Camino", "Bukowski", 2010);
    StudentEntity studentEntity = new StudentEntity(studentId, "Jorge", "jorge@library.com", null);

    when(bookRepository.findByIsbn(isbn)).thenReturn(bookEntity);
    when(bookCopyRepository.findByBookIsbnAndStatus(isbn, BookCopyStatus.AVAILABLE)).thenReturn(Collections.emptyList()); //empty

    Assertions.assertThrows(LoanException.class, () -> libraryService.borrowBook(isbn, studentId));

    verify(loanRepository, never()).save(any(LoanEntity.class)); // as the book was already loaned no saving method were called
    verify(bookRepository, times(1)).findByIsbn(isbn);
    verify(studentRepository, never()).findById(studentId);
  }

  // CASE 5: Return Book Happy Path
  @Test
  void returnBook_ShouldReturnLoan_WhenDataIsCorrect() {
    String isbn = "978-3-16-148410-0";
    BookEntity bookEntity = new BookEntity(isbn, "El Camino", "Bukowski", 2010);
    long studentId = 4L;
    StudentEntity studentEntity = new StudentEntity(studentId, "Jorge", "jorge@library.com", null);

    BookCopyEntity bookCopyEntity = new BookCopyEntity();
    bookCopyEntity.setId(10L);
    bookCopyEntity.setBook(bookEntity);
    bookCopyEntity.setStatus(BookCopyStatus.LOANED);

    Long loanId = 7L;
    LoanEntity loanEntity = new LoanEntity(); // Use this to 'train' the mocks
    loanEntity.setBookCopy(bookCopyEntity);
    loanEntity.setStudent(studentEntity);
    loanEntity.setLoanId(loanId);
    loanEntity.setActive(true);
    loanEntity.setDueDate(LocalDate.now());

    when(loanRepository.findById(loanId)).thenReturn(Optional.of(loanEntity));

    LoanEntity result = libraryService.returnBook(loanId);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(result.getBookCopy().getBook().getIsbn(), loanEntity.getBookCopy().getBook().getIsbn());
    Assertions.assertEquals(bookEntity, result.getBookCopy().getBook());

    // Important: check state changes
    Assertions.assertFalse(loanEntity.isActive());
    Assertions.assertEquals(LocalDate.now(), loanEntity.getReturnDate());
    Assertions.assertSame(BookCopyStatus.AVAILABLE, result.getBookCopy().getStatus());

    verify(loanRepository, times(1)).findById(loanId);
    verify(loanRepository, times(1)).save(any(LoanEntity.class));
    verify(penaltyRepository, never()).save(any());
  }

  // CASE 6: Loan with the given loanId not exist
  @Test
  void returnBook_ShouldThrowEntityNotFoundException_WhenLoanNotExist() {
    Long loanId = 7L;

    // Return null to allow the service to throw the Exception
    when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

    Assertions.assertThrows(EntityNotFoundException.class, () -> libraryService.returnBook(loanId));

    verify(loanRepository, times(1)).findById(loanId);
    verify(loanRepository, never()).save(any(LoanEntity.class));
  }

  // CASE 7: Loan not currently active (business logic)
  @Test
  void returnBook_ShouldThrowLoanException_WhenLoanNotActive() {
    Long loanId = 7L;
    LoanEntity loanEntity = new LoanEntity();
    loanEntity.setActive(false);

    when(loanRepository.findById(loanId)).thenReturn(Optional.of(loanEntity));

    Assertions.assertThrows(LoanException.class, () -> libraryService.returnBook(loanId));

    verify(loanRepository, times(1)).findById(loanId);
    verify(loanRepository, never()).save(any(LoanEntity.class));
  }

  // CASE 8: Penalty test Happy path
  @Test
  void returnBook_ShouldCreatePenalty_WhenIsLate() {
    String isbn = "978-3-16-148410-0";
    BookEntity bookEntity = new BookEntity(isbn, "El Camino", "Bukowski", 2010);

    BookCopyEntity bookCopyEntity = new BookCopyEntity();
    bookCopyEntity.setId(10L);
    bookCopyEntity.setBook(bookEntity);
    bookCopyEntity.setStatus(BookCopyStatus.LOANED);

    Long loanId = 7L;
    LoanEntity loanEntity = new LoanEntity();
    loanEntity.setLoanId(loanId);
    loanEntity.setDueDate(LocalDate.now().minusDays(3));
    loanEntity.setActive(true);
    loanEntity.setBookCopy(bookCopyEntity);

    when(loanRepository.findById(loanId)).thenReturn(Optional.of(loanEntity));

    LoanEntity result = libraryService.returnBook(loanId);

    Assertions.assertNotNull(result);

    // Penalty logic checks
    Assertions.assertNotNull(loanEntity.getPenalty());
    Assertions.assertEquals(3.0, loanEntity.getPenalty().getAmount()); // 3 days late = 3.0 as fee

    // Loan and penalty relations check
    Assertions.assertEquals(result.getPenalty().getLoan().getLoanId(), result.getLoanId());

    verify(penaltyRepository, times(1)).save(any());
    verify(loanRepository, times(1)).findById(loanId);
  }

  // CASE 9: Penalty test -> book returned on the same day(or before) of the loan deadline
  @Test
  void returnBook_ShouldNotCreatePenalty_WhenIsOnTime() {
    String isbn = "978-3-16-148410-0";
    BookEntity bookEntity = new BookEntity(isbn, "El Camino", "Bukowski", 2010);

    BookCopyEntity bookCopyEntity = new BookCopyEntity();
    bookCopyEntity.setId(10L);
    bookCopyEntity.setBook(bookEntity);
    bookCopyEntity.setStatus(BookCopyStatus.LOANED);

    Long loanId = 7L;
    LoanEntity loanEntity = new LoanEntity();
    loanEntity.setLoanId(loanId);
    loanEntity.setActive(true);
    loanEntity.setBookCopy(bookCopyEntity);

    // Return it on the last day should be OK and not create a penalty
    loanEntity.setDueDate(LocalDate.now());

    when(loanRepository.findById(loanId)).thenReturn(Optional.of(loanEntity));

    LoanEntity result = libraryService.returnBook(loanId);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(loanEntity.getLoanDate(), result.getLoanDate());
    Assertions.assertEquals(loanEntity.getDueDate(), result.getDueDate());

    Assertions.assertNull(result.getPenalty());

    verify(loanRepository, times(1)).findById(loanId);
    verify(penaltyRepository, never()).save(any());
  }

}