package com.library.api.service;

import com.library.api.domain.BookEntity;
import com.library.api.domain.LoanEntity;
import com.library.api.domain.StudentEntity;
import com.library.api.repository.BookRepository;
import com.library.api.repository.LoanRepository;
import com.library.api.repository.StudentRepository;
import com.library.api.service.exception.LoanException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

  // Instance our real service and inject the above mocks
  @InjectMocks
  private LibraryServiceImpl libraryService;

  // --- TESTING CASES ---

  // CASE 1: Happy Path
  @Test
  void borrowBook_ShouldReturnLoan_WhenDataIsCorrect() {
    String isbn = "978-3-16-148410-0";
    Long studentId = 4L;
    BookEntity bookEntity = new BookEntity(isbn, "El Camino", "Bukowski", 2010);
    StudentEntity studentEntity = new StudentEntity(studentId, "Jorge", "jorge@library.com", null);

    // 'Train' our mocks
    when(bookRepository.findByIsbn(isbn)).thenReturn(bookEntity);
    when(studentRepository.findById(studentId)).thenReturn(Optional.of(studentEntity));
    when(loanRepository.existsByBookAndActiveTrue(bookEntity)).thenReturn(false);
    when(loanRepository.save(any(LoanEntity.class))).thenAnswer(i -> i.getArgument(0));

    LoanEntity result = libraryService.borrowBook(isbn, studentId);

    Assertions.assertNotNull(result);
    Assertions.assertNotNull(result.getBook());

    Assertions.assertEquals(isbn, result.getBook().getIsbn());
    Assertions.assertEquals(studentId, result.getStudent().getId());

    Assertions.assertEquals(bookEntity, result.getBook());
    Assertions.assertEquals(studentEntity, result.getStudent());

    Assertions.assertTrue(result.isActive()); // Now should be true

    verify(loanRepository, times(1)).existsByBookAndActiveTrue(bookEntity);
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
  }

  // CASE 3: Student does not exist
  @Test
  void borrowBook_ShouldThrowException_WhenStudentNotFound() {
    String isbn = "978-3-16-148410-0";
    BookEntity bookEntity = new BookEntity(isbn, "El Camino", "Bukowski", 2010);
    Long studentId = 4L;

    when(bookRepository.findByIsbn(isbn)).thenReturn(bookEntity);
    when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

    Assertions.assertThrows(EntityNotFoundException.class, () -> libraryService.borrowBook(isbn, studentId));

    verify(bookRepository, times(1)).findByIsbn(isbn);
    verify(studentRepository, times(1)).findById(anyLong());
    verify(loanRepository, times(0)).save(any(LoanEntity.class));
  }

  // CASE 4: Book already loaned (business logic)
  @Test
  void borrowBook_ShouldThrowLoanException_WhenBookIsAlreadyActive() {
    String isbn = "978-3-16-148410-0";
    long studentId = 4L;
    BookEntity bookEntity = new BookEntity(isbn, "El Camino", "Bukowski", 2010);
    StudentEntity studentEntity = new StudentEntity(studentId, "Jorge", "jorge@library.com", null);

    when(bookRepository.findByIsbn(isbn)).thenReturn(bookEntity);
    when(studentRepository.findById(studentId)).thenReturn(Optional.of(studentEntity));
    when(loanRepository.existsByBookAndActiveTrue(bookEntity)).thenReturn(true); // 'true' means that book is already loaned

    Assertions.assertThrows(LoanException.class, () -> libraryService.borrowBook(isbn, studentId));

    verify(loanRepository, never()).save(any(LoanEntity.class)); // as the book was already loaned no saving method were called

    verify(bookRepository, times(1)).findByIsbn(isbn);
    verify(studentRepository, times(1)).findById(studentId);
  }
}