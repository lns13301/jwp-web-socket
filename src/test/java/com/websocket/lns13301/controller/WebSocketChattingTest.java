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
        ??????_??????();
        ???_??????();
    }

    private void ??????_??????() {
        userRepository.save(new User("?????????"));
        userRepository.save(new User("?????????"));
        userRepository.save(new User("?????????"));
        userRepository.save(new User("?????????"));
    }

    private void ???_??????() {
        roomRepository.save(new Room(2));
        roomRepository.save(new Room(4));
        roomRepository.save(new Room(5));
    }

    @DisplayName("????????? ???????????? ???????????? ????????? ?????? ?????? ???????????? ????????? ???????????????.")
    @Test
    void enterUserAndBroadCastMessage() throws InterruptedException, ExecutionException, TimeoutException {
        Room room = roomRepository.findAll().get(0);
        User user = userRepository.findAll().get(0);
        UserResponse expectedUser = UserResponse.from(user);
        MessageResponse expectedMessage = new MessageResponse(user.getId(), "????????? ?????? ?????????.");

        // Settings
        WebSocketStompClient webSocketStompClient = ???_??????_STOMP_CLIENT();
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        // Connection
        ListenableFuture<StompSession> connect = webSocketStompClient
            .connect("ws://localhost:" + port + "/ws-connection", new StompSessionHandlerAdapter() {
            });
        StompSession stompSession = connect.get(60, TimeUnit.SECONDS);

        stompSession.subscribe(String.format("/sub/rooms/%s", room.getId()), new StompFrameHandlerImpl(new SessionResponse(), users));
        stompSession.send(String.format("/pub/rooms/%s", room.getId()), new SessionRequest(user.getId(), "1A2B3C4D"));

        stompSession.subscribe(String.format("/sub/rooms/%s/chat", room.getId()), new StompFrameHandlerImpl(new MessageResponse(), messages));
        stompSession.send(String.format("/sub/rooms/%s/chat", room.getId()), new MessageRequest(user.getId(), "????????? ?????? ?????????."));

        SessionResponse sessionResponse = users.poll(5, TimeUnit.SECONDS);
        MessageResponse messageResponse = messages.poll(5, TimeUnit.SECONDS);

        // Then
        assertThat(sessionResponse.getUserResponses().get(0)).usingRecursiveComparison().isEqualTo(expectedUser);
        assertThat(messageResponse).usingRecursiveComparison().isEqualTo(expectedMessage);
    }

    private WebSocketStompClient ???_??????_STOMP_CLIENT() {
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        WebSocketTransport webSocketTransport = new WebSocketTransport(standardWebSocketClient);
        List<Transport> transports = Collections.singletonList(webSocketTransport);
        SockJsClient sockJsClient = new SockJsClient(transports);

        return new WebSocketStompClient(sockJsClient);
    }
}
