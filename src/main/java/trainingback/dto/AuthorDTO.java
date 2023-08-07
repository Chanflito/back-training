package trainingback.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private String name;
}
