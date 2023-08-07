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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuthorControllerTest {
    @Autowired
    private BookService bookService;

    private List<AuthorDTO> authors;

    @Autowired
    private TestRestTemplate restTemplate;

    private final String baseUrl="/author";
    @BeforeEach
    public void setUp() {
        authors = BookGenerator.generateAuthors();
        List<BookDTO> books = BookGenerator.generateBooks(authors);
        books.forEach(book -> {
            try {
                bookService.create(book);
            } catch (ModelAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private HttpEntity<AuthorDTO> createHttpEntity(AuthorDTO authorDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(authorDTO, headers);
    }
    @Test
    void getAllAuthorsTest(){
        ResponseEntity<?> response=restTemplate.getForEntity(baseUrl,Void.class);
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }



    @Test
    void getAuthorsInfoWithNoExistingIDTest(){
        ResponseEntity<?> response=restTemplate.getForEntity(baseUrl+ "/" +2L,String.class);
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    void getAuthorsInfoWithInvalidPathVariableTest(){
        ResponseEntity<?> response=restTemplate.getForEntity(baseUrl+ "/invalidID" ,String.class);
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    void updateExistingAuthorTest(){
        AuthorDTO authorDTO=authors.get(0);
        authorDTO.setName("Example Name");
        ResponseEntity<?> response=restTemplate.exchange(baseUrl,HttpMethod.PUT,createHttpEntity(authorDTO), Void.class);
        assertEquals(HttpStatus.ACCEPTED,response.getStatusCode());
    }


    @Test
    void updateNoExistingAuthorTest(){
        AuthorDTO authorDTO=authors.get(0);
        authorDTO.setName("Example Name");
        authorDTO.setId(50L);
        ResponseEntity<?> response=restTemplate.exchange(baseUrl,HttpMethod.PUT,createHttpEntity(authorDTO), Void.class);
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    void updateAuthorWithNullParameterTest(){
        AuthorDTO authorDTO=authors.get(0);
        authorDTO.setName(null);
        authorDTO.setId(50L);
        ResponseEntity<?> response=restTemplate.exchange(baseUrl,HttpMethod.PUT,createHttpEntity(authorDTO), Void.class);
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }


    @Test
    void deleteNoExistingAuthor(){
        ResponseEntity<?> response=restTemplate.exchange(baseUrl+"/"+5L,HttpMethod.DELETE,null, Void.class);
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    void getAuthorsInfoWithExistingIDTest() throws ModelAlreadyExistsException {
        AuthorDTO authorDTO=AuthorDTO.builder().name("Roberto").id(209L).build();
        BookDTO bookDTO=BookDTO.builder().title("Example").year(1200).genre("A kind of Fiction").author(authorDTO).build();
        bookService.create(bookDTO);
        ResponseEntity<?> response=restTemplate.getForEntity(baseUrl+ "/" +209L,String.class);
        assertEquals(HttpStatus.OK,response.getStatusCode());
    }
    @Test
    void deleteExistingAuthor() throws ModelAlreadyExistsException {
        AuthorDTO authorDTO=AuthorDTO.builder().name("Roberto").id(209L).build();
        BookDTO bookDTO=BookDTO.builder().title("Example").year(1200).genre("A kind of Fiction").author(authorDTO).build();
        bookService.create(bookDTO);
        ResponseEntity<?> response=restTemplate.exchange(baseUrl+"/"+209L,HttpMethod.DELETE,null, Void.class);
        assertEquals(HttpStatus.ACCEPTED,response.getStatusCode());
    }
}
