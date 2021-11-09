package com.websocket.lns13301.repository;

import com.websocket.lns13301.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
