package com.library.api.dto;

import jakarta.persistence.criteria.CriteriaBuilder;

import java.io.Serializable;

public class BookDto implements Serializable {

  private String isbn;
  private String title;
  private String authorName;
  private Integer publicationYear;

  public BookDto() {
  }

  public BookDto(String isbn, String title, String authorName, Integer publicationYear) {
    this.isbn = isbn;
    this.title = title;
    this.authorName = authorName;
    this.publicationYear = publicationYear;
  }

  public String getAuthorName() {
    return authorName;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getPublicationYear() {
    return publicationYear;
  }

  public void setPublicationYear(Integer publicationYear) {
    this.publicationYear = publicationYear;
  }
}
