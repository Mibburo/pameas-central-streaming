package gr.uaegean.pameas.central.streaming.service;

import CentralLocation.Location;
import StreamMessage.Streaming;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import javax.websocket.OnMessage;
import java.net.URI;
import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class WebSocketClientService {

    public void connectToStreamingApi() {
        //String url = "ws://localhost:7040/name";
        String url = "wss://app-eucentral3.central.arubanetworks.com/streaming/api";
        WebSocketClient client = new StandardWebSocketClient();

        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cGUiOiJqd3QifQ.eyJjdXN0b21lcl9pZCI6IjM2N2Y3OTAyMDM1NTExZWQ4N2Y1OTZkOTQ3ZTI2OWE2IiwiY3JlYXRpb25fZGF0ZSI6MTY1NzgwNjM5OX0.BP7Rqrke3n1RTI9docQkd_9qRtlurEIAWO-7NvtxxsY");
        headers.add("Topic", "location");
//         headers.add("Topic", "monitoring");
        //headers.add("Topic", "presence");

        HashMap<String, String> locations = new HashMap<>();

        try {
            WebSocketSession webSocketSession = client.doHandshake(new WebSocketHandler() {

                @Override
                public void afterConnectionEstablished(WebSocketSession session) {
                    log.info("established connection :{}", session);
                }

                @Override
                public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//                    log.info("received message :{}", message);
                    ByteBuffer buf = (ByteBuffer) message.getPayload();

                    byte[] arr = new byte[buf.remaining()];
                    buf.get(arr);

                    Streaming.MsgProto parseCentralMsg = parseCentralMsg(arr);

                    Location.stream_location loc = parseLocationMsg(parseCentralMsg.getData().toByteArray());
                    String parsedMac = transformMacAddress(bytesToHex(loc.getStaEthMac().toByteArray()));



                    List<String> knownMacs = Arrays.asList(
                            "5C:D0:6E:F7:45:DC",
                            "50:3E:AA:25:43:D9",
                            "D8:32:E3:B5:79:1B",
                            "20:34:FB:93:28:43",
                            "B4:B5:B6:87:DC:99",
                            "34:42:62:96:9E:D8",
                            "A4:D9:31:26:AE:08",
                            "00:C0:CA:84:FC:8B",
                            "88:9F:6F:14:3C:9A",
                            "4C:02:20:FC:AC:72",
                            "68:5D:43:EA:00:5B",
                            "08:2E:5F:F1:00:E2",
                            "80:35:C1:39:99:B4",
                            "10:02:B5:80:90:85",
                            "7C:D6:61:94:3A:3B",
                            "9C:AD:97:35:DE:14",
                            "90:9A:4A:9C:CA:8E",
                            "00:95:69:F2:F8:09",
                            "30:CD:A7:20:A4:8D",
                            "F8:B9:5A:C4:DB:34",
                            "FC:87:43:1B:26:F0",
                            "10:0E:7E:0E:68:40",
                            "B8:EE:65:88:37:AE",
                            "E8:6F:38:7C:C4:85",
                            "00:21:B7:4B:5E:48",
                            "E8:9E:B4:0F:D3:29",
                            "70:5F:A3:01:21:10"
                            ,"2C:AE:2B:B9:5D:EB",
                            "58:D9:C3:9D:BD:25",
                            "B8:EE:65:88:37:AE",
                            "00:C0:CA:84:FC:9B",
                            "18:01:F1:35:32:17",
                            "7C:67:A2:B4:7E:5E"
                    );
                    if (knownMacs.contains(parsedMac)) {
                        log.info("known device");
                    } else {
                        log.info("location received for mac {}, x: {}, y: {} ", parsedMac, loc.getStaLocationX(), loc.getStaLocationY());
                        if (locations.get(parsedMac) == null) {
                            locations.put(parsedMac, loc.getStaLocationX() + "-" + loc.getStaLocationY());
                        } else {
                            if (!locations.get(parsedMac).equals(loc.getStaLocationX() + "-" + loc.getStaLocationY())) {
                                log.info("!!!!!!new location found!!!!!");
                                locations.put(parsedMac, loc.getStaLocationX() + "-" + loc.getStaLocationY());
                                log.info(locations.get(parsedMac));
                            } else {
                                log.info("same loc found again!");
                            }

                        }
                    }


                    //                    log.info("new location x {}", loc.getStaLocationX());
//                    log.info("new location y {}", loc.getStaLocationY());
//                    log.info("new location mac {}", );


//                    String mac64 = new String(Base64.getDecoder().decode( loc.getHashedStaEthMacBytes().toByteArray()));
//                    log.info("new location mac64 {}", mac64);


//                    log.info("new location mac64_2 {}", loc.getStaEthMac().getAddr().toStringUtf8());

                    //
//                    log.info(new String(Base64.decodeBase64(loc.getStaEthMac().toByteArray())));
//                    log.info(loc.getStaEthMac().getAddr().toStringUtf8());
//
//                    log.info(loc.getStaEthMacOrBuilder().getAddr().toStringUtf8());
//                    log.info(new String(Base64.decodeBase64(   )));


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
                    return true;
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

    private Location.stream_location parseLocationMsg(byte[] data) {
        try {
            return Location.stream_location.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private Streaming.MsgProto parseCentralMsg(byte[] data) {
        try {
            return Streaming.MsgProto.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage());
        }
        return null;
    }


    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String transformMacAddress(String macAddress) {
        //remove buffer chars if any
        String mcAddr = macAddress.length() < 16 ? macAddress : macAddress.substring(4, macAddress.length());
        String val = "2";

        //add : every val = 2 characters and remove the last one
        return mcAddr.replaceAll("(.{" + val + "})", "$0:").replaceAll(".$", "");
    }

}
