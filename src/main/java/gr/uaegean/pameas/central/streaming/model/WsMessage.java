package gr.uaegean.pameas.central.streaming.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WsMessage {

    private String type;
    private String group;
    private String timestamp;
    private String sender;
    private String recipient;
    private String textMsg;
    private byte[] imageBytes;
    private byte[] videoBytes;

    public WsMessage(String textMsg, String sender) {
        this.textMsg = textMsg;
        this.sender = sender;
    }

}
