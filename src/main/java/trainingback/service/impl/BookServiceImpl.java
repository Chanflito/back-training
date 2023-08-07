package trainingback.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import trainingback.dto.AuthorDTO;
import trainingback.dto.BookDTO;
import trainingback.exception.ModelAlreadyExistsException;
import trainingback.exception.ModelNotFoundException;
import trainingback.model.Author;
import trainingback.model.Book;
import trainingback.repository.AuthorRepository;
import trainingback.repository.BookRepository;
import trainingback.service.BookService;


import java.util.List;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    private Book findBook(BookDTO bookDTO) throws ModelNotFoundException {
        return bookRepository.findById(bookDTO.getId()).orElseThrow(()->new ModelNotFoundException("Book with ID "+ bookDTO.getId() +" not found."));
    }

    @Override
    public void create(BookDTO bookDTO) throws ModelAlreadyExistsException {
        boolean bookFound=bookRepository.existsBook(bookDTO.getAuthor().getId(), bookDTO.getTitle(), bookDTO.getYear(), bookDTO.getGenre());
        Optional<Author> authorFound=authorRepository.findById(bookDTO.getAuthor().getId());
        if (!bookFound && authorFound.isEmpty() ){
            Author author=Author.builder().id(bookDTO.getAuthor().getId()).name(bookDTO.getAuthor().getName()).build();
            Book book=Book.builder().title(bookDTO.getTitle()).publicationYear(bookDTO.getYear()).genre(bookDTO.getGenre()).author(author).build();
            bookRepository.save(book);
        }
        else if (!bookFound){
            Author existingAuthor = authorFound.get();
            Book book = Book.builder().title(bookDTO.getTitle()).publicationYear(bookDTO.getYear()).genre(bookDTO.getGenre()).author(existingAuthor).build();
            bookRepository.save(book);
        }
        else{
            throw new ModelAlreadyExistsException("Book " +bookDTO.getTitle()+ " already exists.");
        }
    }

    @Override
    public List<BookDTO> getAllBooks(boolean complete) {
        if (complete){
            return bookRepository.findAll().stream().map(book ->BookDTO.builder().id(book.getId()).year(book.getPublicationYear()).title(book.getTitle()).genre(book.getGenre()).
                            author(AuthorDTO.builder().id(book.getAuthor().getId()).name(book.getAuthor().getName()).build()).build()).collect(Collectors.toList());
        }
        else{
            return bookRepository.findAll().stream().map(book ->BookDTO.builder().id(book.getId()).
                            year(book.getPublicationYear()).title(book.getTitle()).genre(book.getGenre()).
                            author(AuthorDTO.builder().name(book.getAuthor().getName()).build()).build())
                            .collect(Collectors.toList());
        }
    }

    @Override
    public BookDTO getInfoByID(Long id) throws ModelNotFoundException {
        Optional<Book> book=bookRepository.findById(id);
        if (book.isPresent()){
            return BookDTO.builder().id(book.get().getId()).
                    year(book.get().getPublicationYear()).title(book.get().getTitle()).genre(book.get().getGenre()).
                    author(AuthorDTO.builder().name(book.get().getAuthor().getName()).id(book.get().getAuthor().getId()).build()).build();
        }else{
            throw new ModelNotFoundException("Book with ID "+ id +" not found.");
        }

    }

    @Override
    public void upgradeInfo(BookDTO bookDTO) throws ModelNotFoundException {
        Book book=findBook(bookDTO);
        if (Objects.equals(book.getAuthor().getId(), bookDTO.getAuthor().getId())
                && Objects.equals(book.getAuthor().getName(), bookDTO.getAuthor().getName())){
            book.setPublicationYear(bookDTO.getYear());
            book.setTitle(bookDTO.getTitle());
            book.setGenre(bookDTO.getGenre());
            bookRepository.save(book);
        }
        else{
            throw new IllegalArgumentException("Author's data must be equal.");
        }

    }

    @Override
    public void delete(Long bookID) throws ModelNotFoundException {
        Book book=bookRepository.findById(bookID).orElseThrow(()->new ModelNotFoundException("Book's ID must not be null"));
        bookRepository.delete(book);
    }
}
