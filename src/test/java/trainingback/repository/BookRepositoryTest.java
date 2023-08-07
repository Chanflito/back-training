package trainingback.repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import trainingback.model.Author;
import trainingback.model.Book;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureWebClient
@DataJpaTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;
    @Autowired
    AuthorRepository authorRepository;
    @Test
    void simpleBookRepositoryTest(){
        Author author=Author.builder().id(12L).name("Pedro").bookList(new ArrayList<>()).build();
        Book book=Book.builder().title("El Principito").genre("Fiction").publicationYear(1992).author(author).build();
        bookRepository.save(book);
        assertFalse(bookRepository.findAll().isEmpty());
    }

    @Test
    void existsBookBookRepositoryTest(){
        Author author=Author.builder().id(12L).name("Pedro").bookList(new ArrayList<>()).build();
        Book book=Book.builder().title("El Principito").genre("Fiction").publicationYear(1992).author(author).build();
        bookRepository.save(book);
        assertTrue(bookRepository.existsBook(author.getId(), book.getTitle(), book.getPublicationYear(), book.getGenre()));
    }
}
