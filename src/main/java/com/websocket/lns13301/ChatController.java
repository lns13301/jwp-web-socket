package com.websocket.lns13301;

import com.websocket.lns13301.message.DummyResponse;
import com.websocket.lns13301.message.ExitResponse;
import com.websocket.lns13301.message.MessageRequest;
import com.websocket.lns13301.message.SessionRequest;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
public class ChatController {

    private final SimpMessagingTemplate template;
    private final ChatService chatService;

    public ChatController(final SimpMessagingTemplate template, final ChatService chatService) {
        this.template = template;
        this.chatService = chatService;
    }

    @MessageMapping("/rooms/{roomId}/chat")
    public void chat(@DestinationVariable final Long roomId, final MessageRequest request) {
        template.convertAndSend("/topic/rooms/" + roomId + "/chat", chatService.sendMessage(request));
    }

    @MessageMapping("/rooms/{roomId}")
    public void enter(@DestinationVariable final Long roomId, final SessionRequest request) {
        template.convertAndSend("/sub/rooms/" + roomId, chatService.enter(roomId, request));
    }

    @EventListener
    public void exit(final SessionDisconnectEvent event) {
        ExitResponse response = chatService.exit(event.getSessionId());
        template.convertAndSend("/sub/rooms/" + response.getRoomId(), response.getSessionResponse());
    }

    @PostMapping("/dummy")
    public ResponseEntity<DummyResponse> createDummyRoomAndUser() {
        DummyResponse response = chatService.createDummy();
        return ResponseEntity.ok(response);
    }
}
