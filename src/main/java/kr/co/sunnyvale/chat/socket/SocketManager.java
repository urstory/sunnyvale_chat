package kr.co.sunnyvale.chat.socket;

import kr.co.sunnyvale.chat.security.CustomUserDetails;
import org.springframework.web.server.WebSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

public class SocketManager {
    private static SocketManager instance = new SocketManager();
    private SocketManager(){
    }
    public static SocketManager getInstance(){
        return instance;
    }

    public Map<Long, Long> loginIdroomIdMap = Collections.synchronizedMap(new HashMap<>());
    public Map<Long, List<Long>> roomIduserIdsMap = Collections.synchronizedMap(new HashMap<>());
    public Map<String, Long> sessionIdloginIdMap = Collections.synchronizedMap(new HashMap<>());
    public Map<String, CustomUserDetails> sessionIdCustomUserDetailsMap = Collections.synchronizedMap(new HashMap<>());
    public Map<Long, WebSocketSession> loginIdSessionMap = Collections.synchronizedMap(new HashMap<>());

    public void joinRoom(Long roomId, Long userId){
        List<Long> userIds = roomIduserIdsMap.get(roomId);
        if (userIds == null) {
            roomIduserIdsMap.put(roomId, Collections.synchronizedList(new ArrayList<Long>()));
            userIds = roomIduserIdsMap.get(roomId);
        }
        userIds.add(userId);
    }

    public void exitRoom(Long roomId, Long userId){
        List<Long> userIds = roomIduserIdsMap.get(roomId);
        if (userIds != null) {
            while(true) {
                boolean flag = userIds.remove(userId);
                if(!flag) break;
            }

        }
    }

    public synchronized void sendMessage(String sessionId, TextMessage textMessage){
        Long loginId = sessionIdloginIdMap.get(sessionId);
        Long roomId = loginIdroomIdMap.get(loginId);
        List<Long> userIds = roomIduserIdsMap.get(roomId);
        userIds.stream().forEach(userId -> {
            try {
                WebSocketSession chatSession = loginIdSessionMap.get(userId);
                chatSession.sendMessage(textMessage);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        });
    }

    public synchronized void connect(String sessionId, Long loginId, CustomUserDetails customUserDetails, WebSocketSession session){
        sessionIdloginIdMap.put(sessionId, loginId);
        sessionIdCustomUserDetailsMap.put(sessionId, customUserDetails);
        loginIdSessionMap.put(customUserDetails.getId(), session);
    }

    public synchronized void close(String sessionId){
        Long loginId = sessionIdloginIdMap.get(sessionId);
        Long roomId = loginIdroomIdMap.get(loginId);
        exitRoom(roomId, loginId);
        sessionIdloginIdMap.remove(sessionId);
        loginIdroomIdMap.remove(loginId);
        sessionIdCustomUserDetailsMap.remove(sessionId);
        loginIdSessionMap.remove(loginId);
    }
}
