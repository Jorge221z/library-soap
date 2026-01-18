package com.library.api.domain;


import com.library.api.domain.enums.BookStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BOOK_COPY")
public class BookCopyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String barcode;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private BookStatus status;

  // Many copies of a book belongs to a 'real' book
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id")
  private BookEntity book;

  @OneToMany(mappedBy = "bookCopy", fetch = FetchType.LAZY)
  private List<LoanEntity> loans = new ArrayList<>();

  public BookCopyEntity() {
  }

  public BookCopyEntity(BookEntity book, BookStatus status, String barcode) {
    this.book = book;
    this.status = status;
    this.barcode = barcode;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public BookEntity getBook() {
    return book;
  }

  public void setBook(BookEntity book) {
    this.book = book;
  }

  public BookStatus getStatus() {
    return status;
  }

  public void setStatus(BookStatus status) {
    this.status = status;
  }

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  @Override
  public String toString() {
    return "BookCopyEntity{" +
        "id=" + id +
        ", barcode='" + barcode + '\'' +
        ", status=" + status +
        ", book=" + book +
        '}';
  }
}
