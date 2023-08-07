package trainingback.util;
import trainingback.dto.AuthorDTO;
import trainingback.dto.BookDTO;

import java.util.ArrayList;
import java.util.List;

public class BookGenerator {

    private static long authorIdCounter = 101L;

    private static long generateAuthorId() {
        return authorIdCounter++;
    }

    public static List<AuthorDTO> generateAuthors() {
        List<AuthorDTO> authors = new ArrayList<>();

        authors.add(AuthorDTO.builder().id(generateAuthorId()).name("John Smith").build());
        authors.add(AuthorDTO.builder().id(generateAuthorId()).name("Emily Johnson").build());
        authors.add(AuthorDTO.builder().id(generateAuthorId()).name("Michael Williams").build());
        authors.add(AuthorDTO.builder().id(generateAuthorId()).name("Sophia Brown").build());
        authors.add(AuthorDTO.builder().id(generateAuthorId()).name("Daniel Davis").build());
        authors.add(AuthorDTO.builder().id(generateAuthorId()).name("Olivia Miller").build());
        authors.add(AuthorDTO.builder().id(generateAuthorId()).name("James Wilson").build());

        return authors;
    }

    public static List<BookDTO> generateBooks(List<AuthorDTO> authors) {
        List<BookDTO> books = new ArrayList<>();

        books.add(BookDTO.builder().title("The Secret Garden").year(1909).genre("Children's Classic").author(authors.get(0)).build());
        books.add(BookDTO.builder().title("The Great Gatsby").year(1925).genre("Fiction").author(authors.get(1)).build());
        books.add(BookDTO.builder().title("To Kill a Mockingbird").year(1960).genre("Coming-of-age Novel").author(authors.get(2)).build());
        books.add(BookDTO.builder().title("Brave New World").year(1932).genre("Dystopian Fiction").author(authors.get(3)).build());
        books.add(BookDTO.builder().title("Pride and Prejudice").year(1813).genre("Romance Novel").author(authors.get(4)).build());
        books.add(BookDTO.builder().title("Lord of the Rings").year(1954).genre("Fantasy").author(authors.get(5)).build());
        books.add(BookDTO.builder().title("1984").year(1949).genre("Dystopian Fiction").author(authors.get(6)).build());

        return books;
    }

}
