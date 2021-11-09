package com.websocket.lns13301.domain;

import com.websocket.lns13301.message.UserResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Session> sessions;

    private int maxHeadCount;

    public Room(final int maxHeadCount) {
        this.maxHeadCount = maxHeadCount;
        this.sessions = new ArrayList<>();
    }

    public List<User> users() {
        return sessions.stream()
            .map(Session::getUser)
            .collect(Collectors.toList());
    }

    public void exit(final Session session) {
        if (sessions.contains(session)) {
            sessions.remove(session);
            session.delete();
        }
    }

    public void addSession(final Session session) {
        sessions.add(session);
    }
}
