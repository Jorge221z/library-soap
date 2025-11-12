package com.library.api.service;

import com.library.api.domain.BookEntity;
import com.library.api.repository.BookRepository;
import com.library.api.ws.dto.Book;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class LibraryServiceImpl implements LibraryService {

  BookRepository bookRepository;
  // DI inyection by Constructor
  public LibraryServiceImpl(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
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



  private Book convertEntityToDto(BookEntity entity) {
    Book dto = new Book();

    // El DTO(Book) no tiene ID de BD
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

}
