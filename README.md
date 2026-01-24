# Library SOAP Service (Legacy Architecture Training)

This project is a SOAP Web Service for library management, built using Spring Framework (Classic), JPA/Hibernate, and an H2 in-memory database.

## How to Run the Project

1. **Requirements:** Java 17, Maven 3.8+, Tomcat 10+
2. Run the following command:

```bash
mvn clean package
```

3. Deploy the generated `.war` file to your Tomcat server.
4. The application context path is `/library-soap`.

## Endpoints and WSDL

Once deployed, the service is available at:

* **WSDL (Contract):**
  [http://localhost:8080/library-soap/ws/libraryService.wsdl](http://localhost:8080/library-soap/ws/libraryService.wsdl)
  *(Use this URL to import the service into Postman or SOAP UI)*

* **SOAP Endpoint:**
  [http://localhost:8080/library-soap/ws](http://localhost:8080/library-soap/ws)

## Request Examples (SOAP)

Configure your HTTP client (Postman or SoapUI) to send a **POST** request to the SOAP endpoint with the following header:

```
Content-Type: text/xml
```

### 1. Create a Book (createBook)

**Description:** Registers a new book in the catalog.

```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:tns="http://api.library.com/soap">
    <soap:Body>
        <tns:createBook>
            <book>
                <isbn>978-3-456789-01-2</isbn> 
                <title>Microservices Architecture</title>
                <authorName>John Doe</authorName>
                <publicationYear>2023</publicationYear>
            </book>
        </tns:createBook>
    </soap:Body>
</soap:Envelope>
```

### 2. Get Book Details (getBook)

**Description:** Retrieves book details by ISBN.

```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:tns="http://api.library.com/soap">
    <soap:Body>
        <tns:getBook>
            <isbn>978-3-456789-01-2</isbn>
        </tns:getBook>
    </soap:Body>
</soap:Envelope>
```

### 3. Borrow a Book (borrowBook)

**Description:** Borrows a book for an existing student.

```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:tns="http://api.library.com/soap">
    <soap:Body>
        <tns:borrowBook>
            <isbn>978-3-456789-01-2</isbn>
            <studentId>1</studentId>
        </tns:borrowBook>
    </soap:Body>
</soap:Envelope>
```

### 4. Return a Book (returnBook)

**Description:** Return an already loaned book.

```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:tns="http://api.library.com/soap">
    <soap:Body>
        <tns:returnBook>
            <loanId>1</loanId>
        </tns:returnBook>
    </soap:Body>
</soap:Envelope>
```

## Database (H2)

The application uses an in-memory H2 database, which is reset on every deployment.

* **Initial data:** Automatically loaded from
  `src/main/resources/import.sql`
* **Test user:**

  * ID: `1`
  * Name: `Jorge Developer`

## Legacy Architecture Overview

This project follows a **classic layered architecture**, commonly found in legacy Java Enterprise applications developed before the widespread adoption of Spring Boot and RESTful APIs.

### Architectural Characteristics

- **SOAP-based communication** using WSDL-first contracts.
- **Monolithic deployment** packaged as a `.war` file.
- **Application Server–dependent** execution (Apache Tomcat).
- **XML-based configuration** combined with annotation-driven components.
- **Stateful persistence layer** using JPA/Hibernate.intended for:

### Purpose of This Architecture

This project is intended for:

- Understanding **legacy enterprise Java systems**
- Practicing **SOAP-based integration**
- Learning how **traditional layered architectures** were designed and maintained
- Preparing for **maintenance, migration, or modernization** scenarios
