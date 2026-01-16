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
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false) // Foreign key in the DB
  private StudentEntity student;

  // MANY-TO-ONE RELATION: Many loans belogs to a book(historically, not currently)
  @ManyToOne
  @JoinColumn(name = "book_id")
  private BookEntity book;

  private LocalDate loanDate;
  private LocalDate dueDate;
  private boolean active;

  public LoanEntity() {
  }

  public LoanEntity(long loanId, boolean active, LocalDate dueDate, BookEntity book, StudentEntity student, LocalDate loanDate) {
    this.loanId = loanId;
    this.active = active;
    this.dueDate = dueDate;
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

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
