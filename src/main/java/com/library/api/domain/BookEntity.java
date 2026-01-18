package com.library.api.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BOOKS")
public class BookEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false)
  private String isbn;

  @Column(nullable = false)
  private String title;

  @Column(nullable = true)
  private String authorName;

  @Column(nullable = true)
  private Integer publicationYear;

  // A book can have many copies
  @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
  private List<BookCopyEntity> copies = new ArrayList<>();

  public BookEntity() {
  }

  public BookEntity(String isbn, String title, String authorName, Integer publicationYear) {
    this.isbn = isbn;
    this.title = title;
    this.authorName = authorName;
    this.publicationYear = publicationYear;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
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

  public List<BookCopyEntity> getCopies() {
    return copies;
  }

  public void setCopies(List<BookCopyEntity> bookCopies) {
    this.copies = bookCopies;
  }
}
