package com.library.api.repository;

import com.library.api.domain.PenaltyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PenaltyRepository extends JpaRepository<PenaltyEntity, Long> {

  // This will be managed by the default JPA methods

}
