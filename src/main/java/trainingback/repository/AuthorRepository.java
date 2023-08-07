package trainingback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trainingback.model.Author;

public interface AuthorRepository extends JpaRepository<Author,Long> {
}
