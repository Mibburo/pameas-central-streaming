package gr.uaegean.pameas.central.streaming.controller;

import gr.uaegean.pameas.central.streaming.service.WebSocketClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class WebSocketController {

    @Autowired
    WebSocketClientService webSocketClientService;

    @PostMapping("/starWebSocketClient")
    public void startWebSocketClient(){
        webSocketClientService.connectToStreamingApi();
    }

}
