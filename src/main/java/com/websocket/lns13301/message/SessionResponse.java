package com.websocket.lns13301.message;

import com.websocket.lns13301.domain.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponse {

    private List<UserResponse> userResponses;

    public static SessionResponse of(final List<User> users) {
        return new SessionResponse(users.stream()
            .map(user -> new UserResponse(user.getId(), user.getName()))
            .collect(Collectors.toList()));
    }
}
