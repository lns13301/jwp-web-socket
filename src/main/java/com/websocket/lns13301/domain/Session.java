package com.websocket.lns13301.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    public Session(final String sessionId, final User user, final Room room) {
        this(null, sessionId, user, room);
    }

    public Session(final Long id, final String sessionId, final User user, final Room room) {
        this.id = id;
        this.sessionId = sessionId;
        this.user = user;
        this.room = room;

        user.setSession(this);
        room.addSession(this);
    }

    public void delete() {
        user.exit(this);
        room.exit(this);
    }
}
