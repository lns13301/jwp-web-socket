package com.websocket.lns13301.domain;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(mappedBy = "user")
    private Session session;

    public User(final String name) {
        this.name = name;
    }

    public void exit(final Session session) {
        if (isLinkedSession(session)) {
            this.session = null;
            session.delete();
        }
    }

    private boolean isLinkedSession(final Session session) {
        return Objects.nonNull(this.session) && this.session.equals(session);
    }

    public void setSession(final Session session) {
        this.session = session;
    }
}
