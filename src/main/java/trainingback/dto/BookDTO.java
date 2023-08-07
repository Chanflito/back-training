package trainingback.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private String title;

    private Integer year;

    private String genre;

    private AuthorDTO author;
}
