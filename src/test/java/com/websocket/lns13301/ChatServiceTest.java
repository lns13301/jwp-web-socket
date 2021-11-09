package com.websocket.lns13301;

import static org.assertj.core.api.Assertions.assertThat;

import com.websocket.lns13301.domain.Room;
import com.websocket.lns13301.domain.Session;
import com.websocket.lns13301.domain.User;
import com.websocket.lns13301.message.SessionRequest;
import com.websocket.lns13301.message.SessionResponse;
import com.websocket.lns13301.repository.RoomRepository;
import com.websocket.lns13301.repository.SessionRepository;
import com.websocket.lns13301.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ChatServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ChatService chatService;

    @DisplayName("세션을 생성(방에 유저 입장)한다.")
    @Test
    void createSession() {
        // given
        User user = userRepository.save(new User("와일더"));
        Room room = roomRepository.save(new Room(5));

        SessionRequest request = new SessionRequest(user.getId(), "1A2B3C4D");

        // when
        SessionResponse response = chatService.enter(room.getId(), request);

        // then
        assertThat(response.getUserResponses().get(0)).usingRecursiveComparison()
            .isEqualTo(user);
    }

    @DisplayName("세션을 삭제(방에 유저 퇴장)한다.")
    @Test
    void deleteSession() {
        // given
        Session session = 세션_생성();

        // when
        chatService.exit(session.getSessionId());

        // then
        assertThat(sessionRepository.findBySessionId(session.getSessionId())).isNotPresent();
    }

    private Session 세션_생성() {
        User user = userRepository.save(new User("테스터"));
        Room room = roomRepository.save(new Room(5));

        Session session = new Session("1A2B3C4D", user, room);

        return sessionRepository.save(session);
    }

    
}
