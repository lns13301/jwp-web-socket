package com.websocket.lns13301.message;

import com.websocket.lns13301.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String sessionId;

    public static UserResponse from(final User user) {
        return new UserResponse(user.getId(), user.getName(), "Empty");
    }
}
