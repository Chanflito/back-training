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
public class BookServiceTest {

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
    void createWhenNotExistsBookAndAuthorTest() throws ModelNotFoundException {
        AuthorDTO authorDTO=AuthorDTO.builder().id(5L).name("Marcus").build();
        BookDTO bookDTO=BookDTO.builder().title("Example Book").year(1969).genre("Fiction").author(authorDTO).build();
        assertDoesNotThrow(()->bookService.create(bookDTO));
        assertDoesNotThrow(()-> bookService.getInfoByID(8L));
        BookDTO bookWhenIsCreated=bookService.getInfoByID(8L);
        assertEquals(1969,bookWhenIsCreated.getYear());
        assertEquals("Example Book",bookWhenIsCreated.getTitle());
        assertEquals("Fiction",bookWhenIsCreated.getGenre());
        assertEquals(5L,bookWhenIsCreated.getAuthor().getId());
    }

    @Test
    void createWhenExistsAuthorAndNotExistsBookTest() throws ModelNotFoundException {
        AuthorDTO authorDTO=AuthorDTO.builder().id(102L).name("John Smith").build();
        BookDTO bookDTO=BookDTO.builder().title("Example Book").year(1969).genre("Fiction").author(authorDTO).build();
        assertDoesNotThrow(()->bookService.create(bookDTO));
        assertDoesNotThrow(()-> bookService.getInfoByID(8L));
        BookDTO bookWhenIsCreated=bookService.getInfoByID(8L);
        assertEquals(1969,bookWhenIsCreated.getYear());
        assertEquals("Example Book",bookWhenIsCreated.getTitle());
        assertEquals("Fiction",bookWhenIsCreated.getGenre());
        assertEquals(102L,bookWhenIsCreated.getAuthor().getId());
    }

    @Test
    void createWhenExistsAuthorAndBookTest(){
        AuthorDTO authorDTO=AuthorDTO.builder().id(104L).name("Michael Williams").build();
        BookDTO bookDTO=BookDTO.builder().title("To Kill a Mockingbird").year(1960).genre("Coming-of-age Novel").author(authorDTO).build();
        assertThrows(ModelAlreadyExistsException.class,()->bookService.create(bookDTO));
    }


    @Test
    void getAllBooksWhenCompleteIsTrueTest(){
        List<BookDTO> books = bookService.getAllBooks(true);
        assertFalse(books.isEmpty());
        for (BookDTO b: books) {
            assertNotNull(b.getAuthor().getId());
            assertNotNull(b.getAuthor().getName());
        }
    }
    @Test
    void getAllBooksWhenCompleteIsFalseTest(){
        List<BookDTO> books = bookService.getAllBooks(false);
        assertFalse(books.isEmpty());
        for (BookDTO b: books) {
            assertNull(b.getAuthor().getId());
            assertNotNull(b.getAuthor().getName());
        }
    }


    @Test
    void getBookInfoByIdWhenExistsTest() throws ModelNotFoundException {
        assertDoesNotThrow(()->bookService.getInfoByID(2L));
        BookDTO bookDTO=bookService.getInfoByID(2L);
        assertEquals("Fiction",bookDTO.getGenre());
        assertEquals("The Great Gatsby",bookDTO.getTitle());
        assertEquals(1925,bookDTO.getYear());
    }

    @Test
    void getBookInfoByIdWhenNotExistsTest()  {
        assertThrows(ModelNotFoundException.class,()->bookService.getInfoByID(500L));
    }

    @Test
    void upgradeBookInfoWhenExists() throws ModelNotFoundException {
        BookDTO bookDTO=BookDTO.builder().id(2L).title("New Title").year(1912).genre("Fiction").author(authors.get(1)).build();
        bookService.upgradeInfo(bookDTO);
        BookDTO actualBook=bookService.getInfoByID(2L);
        assertEquals(bookDTO.getGenre(),actualBook.getGenre());
        assertEquals(bookDTO.getTitle(),actualBook.getTitle());
        assertEquals(bookDTO.getYear(),actualBook.getYear());
    }

    @Test
    void upgradeBookInfoWhenNotExistsTest(){
        BookDTO bookDTO=BookDTO.builder().id(50L).title("New Title").year(1912).genre("Fiction").author(authors.get(1)).build();
        assertThrows(ModelNotFoundException.class,()->bookService.upgradeInfo(bookDTO));
    }

    @Test
    void deleteWhenBooksExistsTest() throws ModelNotFoundException {
        BookDTO actualBook = bookService.getInfoByID(1L);
        assertDoesNotThrow(()->bookService.delete(actualBook.getId()));
    }

    @Test
    void deleteWhenBooksNotExistsTest(){
        assertThrows(ModelNotFoundException.class,()->bookService.delete(20L));
    }
}
