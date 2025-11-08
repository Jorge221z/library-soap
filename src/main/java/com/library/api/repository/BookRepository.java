package com.library.api.repository;

import com.library.api.domain.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

  BookEntity findByIsbn(String isbn);

}
