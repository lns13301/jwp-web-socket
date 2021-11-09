package com.websocket.lns13301.message;

import com.websocket.lns13301.domain.Room;
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
public class DummyResponse {

    private List<UserResponse> users;
    private List<RoomResponse> rooms;

    public static DummyResponse of(final List<User> users, final List<Room> rooms) {
        List<UserResponse> userResponses = users.stream()
            .map(UserResponse::from)
            .collect(Collectors.toList());
        List<RoomResponse> roomResponses = rooms.stream()
            .map(RoomResponse::from)
            .collect(Collectors.toList());

        return new DummyResponse(userResponses, roomResponses);
    }
}
