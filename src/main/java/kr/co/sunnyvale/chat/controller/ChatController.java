package kr.co.sunnyvale.chat.controller;

import kr.co.sunnyvale.chat.security.AuthUser;
import kr.co.sunnyvale.chat.security.CustomUserDetails;
import kr.co.sunnyvale.chat.socket.SocketManager;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/chatrooms")
public class ChatController {

    @GetMapping(path = "/{id}")
    public String chatroom(@AuthUser CustomUserDetails customUserDetails, @PathVariable(name = "id") Long roomId, ModelMap modelMap){

        SocketManager socketManager = SocketManager.getInstance();
        socketManager.loginIdroomIdMap.put(customUserDetails.getId(), roomId);
        socketManager.joinRoom(roomId, customUserDetails.getId());
        modelMap.addAttribute("chatRoomId", roomId);

        return "chat/chatroom";
    }
}