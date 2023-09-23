package trainingback.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class OutputMessage {
    private final String from;
    private final String text;

    private final String date;

    public OutputMessage(String from, String text,String localDateTime) {
        this.from = from;
        this.text = text;
        this.date=localDateTime;
    }
}
