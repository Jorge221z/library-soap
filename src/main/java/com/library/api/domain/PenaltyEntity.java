package com.library.api.domain;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "PENALTIES")
public class PenaltyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private long amount; // For the moment only allow 1 penalty for every loan
  private LocalDate penaltyDate; // Usually the same as returnDate

  @OneToOne
  @JoinColumn(name = "loan_id") // Foreign key to associate Penalties to Loans
  private LoanEntity loan;

  public PenaltyEntity() {
  }

  public PenaltyEntity(long id, long amount, LocalDate penaltyDate, LoanEntity loan) {
    this.id = id;
    this.amount = amount;
    this.penaltyDate = penaltyDate;
    this.loan = loan;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public LoanEntity getLoan() {
    return loan;
  }

  public void setLoan(LoanEntity loan) {
    this.loan = loan;
  }

  public LocalDate getPenaltyDate() {
    return penaltyDate;
  }

  public void setPenaltyDate(LocalDate penaltyDate) {
    this.penaltyDate = penaltyDate;
  }

  public long getAmount() {
    return amount;
  }

  public void setAmount(long amount) {
    this.amount = amount;
  }

  @Override
  public String toString() {
    return "PenaltyEntity{" +
        "id=" + id +
        ", amount=" + amount +
        ", penaltyDate=" + penaltyDate +
        ", loan=" + loan +
        '}';
  }
}
