package com.westudy.chat.controller;

import com.westudy.chat.dto.ChatRoomResponseDTO;
import com.westudy.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/page/chat")
@RequiredArgsConstructor
public class ChatPageController {

    private final ChatService chatService;

    @GetMapping("")
    public String getChatLobby(Model model) {
        log.info("채팅방 로비 페이지 진입");
        List<ChatRoomResponseDTO> rooms = chatService.getMyChatRooms();
        model.addAttribute("rooms", rooms);
        return "layout/chat/lobby";
    }
}
