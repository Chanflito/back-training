package trainingback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trainingback.model.Book;


public interface BookRepository extends JpaRepository<Book,Long> {


    @Query ("SELECT count(b)>0 FROM Book b WHERE b.title=:title AND b.publicationYear=:publicationYear AND b.genre=:genre OR b.author.id=:authorID")
    boolean existsBook(@Param("authorID")Long authorID,@Param("title") String title, @Param("publicationYear")int publicationYear,@Param("genre") String genre);
}
