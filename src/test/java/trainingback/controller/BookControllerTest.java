package trainingback.controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import trainingback.dto.AuthorDTO;
import trainingback.dto.BookDTO;
import trainingback.exception.ModelAlreadyExistsException;
import trainingback.service.BookService;
import trainingback.util.BookGenerator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class BookControllerTest{
    @Autowired
    private BookService bookService;

    private List<BookDTO> books;
    private List<AuthorDTO> authors;

    @Autowired
    private TestRestTemplate restTemplate;

    private final String baseUrl="/book";
    @BeforeEach
    public void setUp() {
        authors = BookGenerator.generateAuthors();
        books = BookGenerator.generateBooks(authors);
        books.forEach(book -> {
            try {
                bookService.create(book);
            } catch (ModelAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private BookDTO createValidBook() {
        AuthorDTO author = new AuthorDTO();
        author.setId(1L);
        author.setName("John Doe");

        BookDTO book = new BookDTO();
        book.setAuthor(author);
        book.setYear(2020);
        book.setGenre("Fiction");
        book.setTitle("Sample Book");

        return book;
    }

    private HttpEntity<BookDTO> createHttpEntity(BookDTO bookDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(bookDTO, headers);
    }

    @Test
    void createBookWithValidParametersTest(){
        BookDTO bookDTO=createValidBook();
        ResponseEntity<?> response=restTemplate.postForEntity(baseUrl,bookDTO,Void.class);
        assertEquals(HttpStatus.CREATED,response.getStatusCode());
    }

    @Test
    void createBookWithNoValidParametersTest(){
        BookDTO bookDTO=createValidBook();
        bookDTO.setTitle(null);
        bookDTO.setYear(null);
        ResponseEntity<?> response=restTemplate.postForEntity(baseUrl,bookDTO,Void.class);
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    void createBookWithAnExistsBookTest(){
        BookDTO bookDTO=books.get(1);
        ResponseEntity<?> response=restTemplate.postForEntity(baseUrl,bookDTO,Void.class);
        assertEquals(HttpStatus.CONFLICT,response.getStatusCode());
    }

    @Test
    void createBookWithIllegalParametersTest(){
        BookDTO bookDTO=createValidBook();
        bookDTO.getAuthor().setId(-1L);
        ResponseEntity<?> response=restTemplate.postForEntity(baseUrl,bookDTO,Void.class);
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    void getAllBooksWithCompletedTrueTest(){
        ResponseEntity<?> response=restTemplate.getForEntity(baseUrl+ "?completed=true",Void.class);
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    void getAllBooksWithCompletedFalseTest(){
        ResponseEntity<?> response=restTemplate.getForEntity(baseUrl+ "?completed=false",Void.class);
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    void getAllBooksWithInvalidParamTest(){
        ResponseEntity<?> response=restTemplate.getForEntity(baseUrl+ "?invalid=false",Void.class);
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }


    @Test
    void getBookInfoWithExistingIDTest(){
        ResponseEntity<?> response=restTemplate.getForEntity(baseUrl+ "/" +1L,BookDTO.class);
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    void getBookInfoWithNoExistingIDTest(){
        ResponseEntity<?> response=restTemplate.getForEntity(baseUrl+ "/" +50L,String.class);
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    void getBookInfoWithInvalidPathVariableTest(){
        ResponseEntity<?> response=restTemplate.getForEntity(baseUrl+ "/invalidID" ,Void.class);
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    void updateExistingBookTest(){
        BookDTO bookDTO=books.get(0);
        bookDTO.setId(1L);
        bookDTO.setYear(1800);
        bookDTO.setTitle("Example title");
        bookDTO.setGenre("Pacific");
        ResponseEntity<?> response=restTemplate.exchange(baseUrl,HttpMethod.PUT,createHttpEntity(bookDTO), Void.class);
        assertEquals(HttpStatus.ACCEPTED,response.getStatusCode());
    }

    @Test
    void updateNullFieldsTest(){
        BookDTO bookDTO=new BookDTO();
        ResponseEntity<?> response=restTemplate.exchange(baseUrl,HttpMethod.PUT,createHttpEntity(bookDTO), Void.class);
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    void updateNoExistingBookTest(){
        BookDTO bookDTO=createValidBook();
        bookDTO.setId(50L);
        ResponseEntity<?> response=restTemplate.exchange(baseUrl,HttpMethod.PUT,createHttpEntity(bookDTO), Void.class);
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    void deleteExistingBookTest(){
        ResponseEntity<?> response=restTemplate.exchange(baseUrl+"/"+2L,HttpMethod.DELETE,null, Void.class);
        assertEquals(HttpStatus.ACCEPTED,response.getStatusCode());
    }

    @Test
    void deleteNoExistingBookTest(){
        ResponseEntity<?> response=restTemplate.exchange(baseUrl+"/"+50L,HttpMethod.DELETE,null, Void.class);
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }
}
