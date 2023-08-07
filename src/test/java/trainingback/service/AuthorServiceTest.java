package trainingback.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import trainingback.dto.AuthorDTO;
import trainingback.dto.BookDTO;
import trainingback.exception.ModelAlreadyExistsException;
import trainingback.exception.ModelNotFoundException;
import trainingback.util.BookGenerator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuthorServiceTest {
    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    private List<BookDTO> books;
    private List<AuthorDTO> authors;

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

    @Test
    void getAllAuthorsTest(){
        assertFalse(authorService.getAllAuthors().isEmpty());
    }
    @Test
    void getInfoByIDWhenExistsAuthorTest() throws ModelNotFoundException {
        AuthorDTO expectedAuthor = authors.get(0);
        AuthorDTO actualAuthor = authorService.getInfoByID(expectedAuthor.getId());
        assertEquals(expectedAuthor.getName(), actualAuthor.getName());
        assertEquals(expectedAuthor.getId(), actualAuthor.getId());
    }

    @Test
    void getInfoByIDWhenNotExistsAuthorTest(){
        assertThrows(ModelNotFoundException.class,()->{
            authorService.getInfoByID(1L);
        });
    }

    @Test
    void upgradeInfoWhenExistsAuthorTest() throws ModelNotFoundException {
        AuthorDTO actualAuthor = authors.get(0);
        AuthorDTO newAuthor=AuthorDTO.builder().id(actualAuthor.getId()).name("Mauricio").build();
        authorService.upgradeInfo(newAuthor);
        assertDoesNotThrow(()->authorService.upgradeInfo(newAuthor));
        AuthorDTO updatedAuthor= authorService.getInfoByID(actualAuthor.getId());
        assertEquals(newAuthor.getName(),updatedAuthor.getName());
    }

    @Test
    void upgradeInfoWhenNotExistsAuthorTest(){
        AuthorDTO newAuthor=AuthorDTO.builder().id(1L).name("Mauricio").build();
        assertThrows(ModelNotFoundException.class,()->{
            authorService.upgradeInfo(newAuthor);
        });
    }

    @Test
    void deleteWhenExistsAuthorTest(){
        AuthorDTO actualAuthor = authors.get(0);
        assertDoesNotThrow(()->authorService.delete(actualAuthor.getId()));
        assertThrows(ModelNotFoundException.class,()->authorService.getInfoByID(actualAuthor.getId()));
    }

    @Test
    void deleteWhenNotExistsAuthorTest(){
        assertThrows(ModelNotFoundException.class,()->authorService.delete(1L));
    }
}
