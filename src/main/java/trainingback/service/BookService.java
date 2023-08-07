package trainingback.service;

import trainingback.dto.BookDTO;
import trainingback.exception.ModelAlreadyExistsException;
import trainingback.exception.ModelNotFoundException;

import java.util.List;

public interface BookService {
    void create(BookDTO bookDTO) throws ModelAlreadyExistsException;

    List<BookDTO> getAllBooks(boolean complete);
    BookDTO getInfoByID(Long id) throws ModelNotFoundException;
    void upgradeInfo(BookDTO bookDTO) throws ModelNotFoundException;
    void delete(Long bookID) throws ModelNotFoundException;
}
