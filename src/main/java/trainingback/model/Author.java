package trainingback.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Author {
    @Id
    private Long id;
    @Column
    private String name;
    @OneToMany(mappedBy = "author",cascade = CascadeType.PERSIST)
    private List<Book> bookList;

}
