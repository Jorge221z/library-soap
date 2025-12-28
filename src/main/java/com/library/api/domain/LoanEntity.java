package com.library.api.domain;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "LOANS")
public class LoanEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long loanId;

  // MANY-TO-ONE RELATION: Many loans belongs to a Student
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false) // Foreign key in the DB
  private StudentEntity student;

  // MANY-TO-ONE RELATION: Many loans belogs to a book(historically, not currently)
  private BookEntity book;

  private LocalDate loanDate;
  private LocalDate returnDate;
  private boolean active;

  public LoanEntity() {
  }

  public LoanEntity(long loanId, boolean active, LocalDate returnDate, BookEntity book, StudentEntity student, LocalDate loanDate) {
    this.loanId = loanId;
    this.active = active;
    this.returnDate = returnDate;
    this.book = book;
    this.student = student;
    this.loanDate = loanDate;
  }

  public long getLoanId() {
    return loanId;
  }

  public void setLoanId(long loanId) {
    this.loanId = loanId;
  }

  public BookEntity getBook() {
    return book;
  }

  public void setBook(BookEntity book) {
    this.book = book;
  }

  public StudentEntity getStudent() {
    return student;
  }

  public void setStudent(StudentEntity student) {
    this.student = student;
  }

  public LocalDate getLoanDate() {
    return loanDate;
  }

  public void setLoanDate(LocalDate loanDate) {
    this.loanDate = loanDate;
  }

  public LocalDate getReturnDate() {
    return returnDate;
  }

  public void setReturnDate(LocalDate returnDate) {
    this.returnDate = returnDate;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
