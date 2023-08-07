package trainingback.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import trainingback.dto.AuthorDTO;
import trainingback.exception.ModelAlreadyExistsException;
import trainingback.exception.ModelNotFoundException;
import trainingback.model.Author;
import trainingback.repository.AuthorRepository;
import trainingback.repository.BookRepository;
import trainingback.service.AuthorService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository,BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository=bookRepository;
    }

    private Author findAuthor(AuthorDTO authorDTO) throws ModelNotFoundException {
        return authorRepository.findById(authorDTO.getId()).orElseThrow(()->new ModelNotFoundException("Author not found"));
    }


    @Override
    public List<AuthorDTO> getAllAuthors() {
        return authorRepository.findAll().stream().map(author -> AuthorDTO.builder().name(author.getName()).id(author.getId()).build()).collect(Collectors.toList());
    }

    @Override
    public AuthorDTO getInfoByID(Long id) throws ModelNotFoundException {
        Optional<Author> author=authorRepository.findById(id);
        if (author.isPresent()){
            return AuthorDTO.builder().name(author.get().getName()).id(author.get().getId()).build();
        }
        else{
            throw new ModelNotFoundException("Author with "+ id+ " not found");
        }
    }

    @Override
    public void upgradeInfo(AuthorDTO authorDTO) throws ModelNotFoundException {
        Author author=findAuthor(authorDTO);
        author.setName(authorDTO.getName());
        authorRepository.save(author);
    }

    @Override
    @Transactional
    public void delete(Long id) throws ModelNotFoundException {
        Author author=authorRepository.findById(id).orElseThrow(()->new ModelNotFoundException("AuthorID must not be null."));
        if (author.getBookList()!=null && !author.getBookList().isEmpty()){
            bookRepository.deleteAll(author.getBookList());
        }
        authorRepository.delete(author);
    }
}
