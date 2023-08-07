package trainingback.service;

import trainingback.dto.AuthorDTO;
import trainingback.exception.ModelNotFoundException;

import java.util.List;

public interface AuthorService {
    List<AuthorDTO> getAllAuthors();

    AuthorDTO getInfoByID(Long id) throws ModelNotFoundException;

    void upgradeInfo(AuthorDTO authorDTO) throws ModelNotFoundException;

    void delete(Long authorID) throws ModelNotFoundException;

}
