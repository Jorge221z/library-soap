package com.library.api.repository;

import com.library.api.domain.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {

  StudentEntity findById(long id);

}
