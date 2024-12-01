package chat.data;

import java.time.Instant;
import java.util.Date;

public class Message {

    private String sender;
    private Instant date;
    private String content;

    public Message() {
    }

    public Message(String sender, String content,Instant date) {
        this.sender = sender;
        this.date = date;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
