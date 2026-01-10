Library SOAP Service (Legacy Architecture Training)

This project is a SOAP Web Service for library management, built using Spring Framework (Classic), JPA/Hibernate, and an H2 in-memory database.

🚀 How to Run the Project

Requirements: Java 17, Maven 3.8+, Tomcat 10+.

Run mvn clean package.

Deploy the generated .war file to your Tomcat server.

The application context path is /library-soap.

📡 Endpoints and WSDL

Once deployed, the service is available at:

WSDL (Contract):
http://localhost:8080/library-soap/ws/libraryService.wsdl
(Use this URL to import the service into Postman or SOAP UI)

SOAP Endpoint:
http://localhost:8080/library-soap/ws

🧪 Request Examples (Request Body)

Configure your HTTP client (Postman/Insomnia) to send a POST request to the SOAP Endpoint with the header:

Content-Type: text/xml

1. Borrow a Book (BorrowBook)

Operation: Borrows a book for an existing student.

<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:tns="http://api.library.com/ws">
    <soap:Body>
        <tns:borrowBookRequest>
            <tns:isbn>978-3-456789-01-2</tns:isbn>
            <tns:studentId>1</tns:studentId>
        </tns:borrowBookRequest>
    </soap:Body>
</soap:Envelope>

2. Create a Book (CreateBook)

Operation: Registers a new book in the catalog.

<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:tns="http://api.library.com/ws">
    <soap:Body>
        <tns:createBookRequest>
            <tns:book>
                <tns:isbn>978-3-456789-01-2</tns:isbn>
                <tns:title>Java Enterprise Architecture</tns:title>
                <tns:authorName>Jorge Dev</tns:authorName>
                <tns:publicationYear>2025</tns:publicationYear>
            </tns:book>
        </tns:createBookRequest>
    </soap:Body>
</soap:Envelope>

3. Get Book Details (GetBook)

Operation: Retrieves book details by ISBN.

<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:tns="http://api.library.com/ws">
    <soap:Body>
        <tns:getBookRequest>
            <tns:isbn>978-3-456789-01-2</tns:isbn>
        </tns:getBookRequest>
    </soap:Body>
</soap:Envelope>

🛠 Database (H2)

The database runs in memory and is reset on every deployment.

Initial data: Automatically loaded from src/main/resources/import.sql.

Test user:
ID: 1
Name: Jorge Developer
