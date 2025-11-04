package com.library.api.service;

import com.library.api.ws.dto.Book;
import org.springframework.stereotype.Service;

@Service
public class LibraryServiceImpl implements LibraryService {

  public Book getBookByIdentifier(String isbn) {
    if (isbn == null) {
      return null;
    }
    System.out.println("Received request for ISBN: " + isbn);

    Book book = new Book();
    book.setIsbn(isbn);
    book.setTitle("The Spring Journey with Gemini");
    book.setAuthorName("J.M.C Mentor");
    book.setPublicationYear(2025);

    return book;
  }

}
