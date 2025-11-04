package com.library.api.service;

import com.library.api.ws.dto.Book;

public interface LibraryService {

  // Interfaz basada en la logica definida en el library.xsd

  Book getBookByIdentifier(String isbn);

}
