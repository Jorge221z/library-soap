package com.library.api.repository;

import com.library.api.domain.BookCopyEntity;
import com.library.api.domain.BookEntity;
import com.library.api.domain.enums.BookCopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookCopyRepository extends JpaRepository<BookCopyEntity, Long> {

  // Search for copies where the associated Book entity has the ISBN and STATUS sent as params
  List<BookCopyEntity> findByBookIsbnAndStatus(String isbn, BookCopyStatus status);

}
