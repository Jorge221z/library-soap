package com.library.api.repository;

import com.library.api.domain.BookCopyEntity;
import com.library.api.domain.BookEntity;
import com.library.api.domain.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<LoanEntity, Long> {

  // We will check availabilty with the BookCopyRepository methods

}
