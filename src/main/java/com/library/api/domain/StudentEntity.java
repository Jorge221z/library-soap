package com.library.api.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "STUDENTS")
public class StudentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;

  private String email;

  // mappedBy="student" means that the owner of the relation is the stuent attribute on the LoanEntity class
  @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<LoanEntity> loans = new ArrayList<>();


  public StudentEntity() {}

  public StudentEntity(long id, String name, List<LoanEntity> loans) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.loans = loans;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<LoanEntity> getLoans() {
    return loans;
  }

  public void setLoans(List<LoanEntity> loans) {
    this.loans = loans;
  }
}
