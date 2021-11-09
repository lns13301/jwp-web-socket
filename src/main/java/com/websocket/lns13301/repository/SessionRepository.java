package com.websocket.lns13301.repository;

import com.websocket.lns13301.domain.Session;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findBySessionId(final String sessionId);
}
