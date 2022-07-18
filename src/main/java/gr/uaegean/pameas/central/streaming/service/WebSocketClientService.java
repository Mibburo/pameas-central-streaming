package gr.uaegean.pameas.central.streaming.service;

import CentralLocation.Location;
import StreamMessage.Streaming;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import javax.websocket.OnMessage;
import java.net.URI;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class WebSocketClientService {

    public void connectToStreamingApi(){
        //String url = "ws://localhost:7040/name";
        String url =  "wss://app-eucentral3.central.arubanetworks.com/streaming/api";
        WebSocketClient client = new StandardWebSocketClient();

        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cGUiOiJqd3QifQ.eyJjdXN0b21lcl9pZCI6IjM2N2Y3OTAyMDM1NTExZWQ4N2Y1OTZkOTQ3ZTI2OWE2IiwiY3JlYXRpb25fZGF0ZSI6MTY1NzgwNjM5OX0.BP7Rqrke3n1RTI9docQkd_9qRtlurEIAWO-7NvtxxsY");
        headers.add("Topic", "location");
        // headers.add("Topic", "monitoring");
        //headers.add("Topic", "presence");
        try {
            WebSocketSession webSocketSession = client.doHandshake(new WebSocketHandler() {

                @Override
                public void afterConnectionEstablished(WebSocketSession session) {
                    log.info("established connection :{}", session);
                }

                @Override
                public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                    log.info("received message :{}",  message);
                }

                @Override
                public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                    log.info("transport error :{}", exception.getMessage());
                }

                @Override
                public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                    log.info("connection closed");
                }

                @Override
                public boolean supportsPartialMessages() {
                    return false;
                }

            }, headers, URI.create(url)).get();

            log.info("websocket session is open ? :{}", webSocketSession.isOpen());


        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("Received msg: " + message);
    }

    private Location.stream_location parseLocationMsg(byte[] data){
        try {
            return Location.stream_location.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private Streaming.MsgProto parseCentralMsg(byte[] data){
        try {
            return Streaming.MsgProto.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
