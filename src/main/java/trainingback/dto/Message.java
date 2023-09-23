package trainingback.dto;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Message {
    private String from;
    private String text;

    public Message(String from, String text) {
        this.from = from;
        this.text = text;
    }
}
