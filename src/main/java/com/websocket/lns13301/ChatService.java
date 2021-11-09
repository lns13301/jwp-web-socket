package com.websocket.lns13301;

import com.websocket.lns13301.domain.Room;
import com.websocket.lns13301.domain.Session;
import com.websocket.lns13301.domain.User;
import com.websocket.lns13301.message.DummyResponse;
import com.websocket.lns13301.message.ExitResponse;
import com.websocket.lns13301.message.MessageRequest;
import com.websocket.lns13301.message.MessageResponse;
import com.websocket.lns13301.message.SessionRequest;
import com.websocket.lns13301.message.SessionResponse;
import com.websocket.lns13301.repository.RoomRepository;
import com.websocket.lns13301.repository.SessionRepository;
import com.websocket.lns13301.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class ChatService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final SessionRepository sessionRepository;

    public ChatService(final UserRepository userRepository, final RoomRepository roomRepository, final SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.sessionRepository = sessionRepository;
    }

    public MessageResponse sendMessage(final MessageRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(IllegalArgumentException::new);
        return new MessageResponse(user.getId(), request.getContent());
    }

    @Transactional
    public SessionResponse enter(final Long roomId, final SessionRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(IllegalArgumentException::new);
        Room room = roomRepository.findById(roomId).orElseThrow(IllegalArgumentException::new);
        Session session = new Session(request.getSessionId(), user, room);

        sessionRepository.save(session);

        return SessionResponse.from(room.users());
    }

    @Transactional
    public ExitResponse exit(final String sessionId) {
        Session session = sessionRepository.findBySessionId(sessionId).orElseThrow(IllegalArgumentException::new);
        Room room = session.getRoom();

        session.delete();
        sessionRepository.delete(session);

        return new ExitResponse(room.getId(), SessionResponse.from(room.users()));
    }

    public DummyResponse createDummy() {
        List<User> users = new ArrayList<>();
        List<Room> rooms = new ArrayList<>();

        users.add(userRepository.save(new User("와일더")));
        users.add(userRepository.save(new User("마이클")));
        users.add(userRepository.save(new User("제이슨")));
        users.add(userRepository.save(new User("오스카")));

        rooms.add(roomRepository.save(new Room(2)));
        rooms.add(roomRepository.save(new Room(4)));
        rooms.add(roomRepository.save(new Room(5)));

        return DummyResponse.of(users, rooms);
    }
}
