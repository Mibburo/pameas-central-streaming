package gr.uaegean.pameas.central.streaming;

import gr.uaegean.pameas.central.streaming.service.WebSocketClientService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class streamingApiTest {

    @Autowired
    WebSocketClientService clientService;

    @Test
    public void testStreamingApi(){
        clientService.connectToStreamingApi();

    }
}
