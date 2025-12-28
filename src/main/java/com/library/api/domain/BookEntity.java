package com.library.api.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BOOKS")
public class BookEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String isbn;

  @Column(nullable = false)
  private String title;

  @Column(nullable = true)
  private String authorName;

  @Column(nullable = true)
  private Integer publicationYear;

  @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
  private List<LoanEntity> loans = new ArrayList<>();

  public BookEntity() {
  }

  public BookEntity(String isbn, String title, String authorName, Integer publicationYear) {
    this.isbn = isbn;
    this.title = title;
    this.authorName = authorName;
    this.publicationYear = publicationYear;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  public String getAuthorName() {
    return authorName;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  public Integer getPublicationYear() {
    return publicationYear;
  }

  public void setPublicationYear(Integer publicationYear) {
    this.publicationYear = publicationYear;
  }

  public List<LoanEntity> getLoans() {
    return loans;
  }

  public void setLoans(List<LoanEntity> loans) {
    this.loans = loans;
  }
}
