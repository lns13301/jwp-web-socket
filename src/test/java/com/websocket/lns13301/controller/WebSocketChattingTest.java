package com.websocket.lns13301.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.websocket.lns13301.domain.Room;
import com.websocket.lns13301.domain.User;
import com.websocket.lns13301.message.MessageRequest;
import com.websocket.lns13301.message.MessageResponse;
import com.websocket.lns13301.message.SessionRequest;
import com.websocket.lns13301.message.SessionResponse;
import com.websocket.lns13301.message.UserResponse;
import com.websocket.lns13301.repository.RoomRepository;
import com.websocket.lns13301.repository.UserRepository;
import io.restassured.RestAssured;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebSocketChattingTest {

    @LocalServerPort
    private int port;
    private BlockingQueue<SessionResponse> users;
    private BlockingQueue<MessageResponse> messages;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        users = new LinkedBlockingDeque<>();
        messages = new LinkedBlockingDeque<>();
        유저_삽입();
        방_생성();
    }

    private void 유저_삽입() {
        userRepository.save(new User("와일더"));
        userRepository.save(new User("마이클"));
        userRepository.save(new User("제이슨"));
        userRepository.save(new User("오스카"));
    }

    private void 방_생성() {
        roomRepository.save(new Room(2));
        roomRepository.save(new Room(4));
        roomRepository.save(new Room(5));
    }

    @DisplayName("유저가 입장하고 메시지를 보내면 해당 방에 메시지가 브로드 캐스팅된다.")
    @Test
    void enterUserAndBroadCastMessage() throws InterruptedException, ExecutionException, TimeoutException {
        Room room = roomRepository.findAll().get(0);
        User user = userRepository.findAll().get(0);
        UserResponse expectedUser = UserResponse.from(user);
        MessageResponse expectedMessage = new MessageResponse(user.getId(), "채팅을 보내 봅니다.");

        // Settings
        WebSocketStompClient webSocketStompClient = 웹_소켓_STOMP_CLIENT();
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        // Connection
        ListenableFuture<StompSession> connect = webSocketStompClient
            .connect("ws://localhost:" + port + "/ws-connection", new StompSessionHandlerAdapter() {
            });
        StompSession stompSession = connect.get(60, TimeUnit.SECONDS);

        stompSession.subscribe(String.format("/sub/rooms/%s", room.getId()), new StompFrameHandlerImpl(new SessionResponse(), users));
        stompSession.send(String.format("/pub/rooms/%s", room.getId()), new SessionRequest(user.getId(), "1A2B3C4D"));

        stompSession.subscribe(String.format("/sub/rooms/%s/chat", room.getId()), new StompFrameHandlerImpl(new MessageResponse(), messages));
        stompSession.send(String.format("/sub/rooms/%s/chat", room.getId()), new MessageRequest(user.getId(), "채팅을 보내 봅니다."));

        SessionResponse sessionResponse = users.poll(5, TimeUnit.SECONDS);
        MessageResponse messageResponse = messages.poll(5, TimeUnit.SECONDS);

        // Then
        assertThat(sessionResponse.getUserResponses().get(0)).usingRecursiveComparison().isEqualTo(expectedUser);
        assertThat(messageResponse).usingRecursiveComparison().isEqualTo(expectedMessage);
    }

    private WebSocketStompClient 웹_소켓_STOMP_CLIENT() {
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        WebSocketTransport webSocketTransport = new WebSocketTransport(standardWebSocketClient);
        List<Transport> transports = Collections.singletonList(webSocketTransport);
        SockJsClient sockJsClient = new SockJsClient(transports);

        return new WebSocketStompClient(sockJsClient);
    }
}
