package kr.co.sunnyvale.chat.socket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.sunnyvale.chat.security.CustomUserDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketHandler extends TextWebSocketHandler {
    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Long> sessionIdloginIdMap = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {

        System.out.println(message.getPayload());
        SocketManager socketManager = SocketManager.getInstance();

        CustomUserDetails customUserDetails = socketManager.sessionIdCustomUserDetailsMap.get(session.getId());

        TypeReference<HashMap<String,Object>> typeRef
                = new TypeReference<HashMap<String,Object>>() {};
        HashMap<String, String> map = objectMapper.readValue(message.getPayload(), typeRef);
        map.put("name", customUserDetails.getName()); // login 정보를 어떻게 읽어와야할지 고민하자.

        String msg = objectMapper.writeValueAsString(map);
        System.out.println(msg);
        TextMessage textMessage = new TextMessage(msg);

        socketManager.sendMessage(session.getId(), textMessage);

    }

    // 클라이언트가 접속을 하면 클라이언트와 통신할 수 있는 WebSocketSession이 전달된다.
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        AbstractAuthenticationToken principal = (AbstractAuthenticationToken)session.getPrincipal();
        CustomUserDetails customUserDetails = (CustomUserDetails)principal.getPrincipal();
        System.out.println(customUserDetails.getNickName());
        System.out.println(customUserDetails.getId());
        System.out.println(customUserDetails.getEmail());
        System.out.println(customUserDetails.getName());

        SocketManager socketManager = SocketManager.getInstance();
        socketManager.connect(session.getId(), customUserDetails.getId(), customUserDetails, session);
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

        SocketManager socketManager = SocketManager.getInstance();
        socketManager.close(session.getId());

        System.out.println("session remove");
        sessions.remove(session);

    }
}