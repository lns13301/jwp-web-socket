package com.websocket.lns13301.message;

import com.websocket.lns13301.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {

    private Long id;
    private Integer maxHeadCount;

    public static RoomResponse from(final Room room) {
        return new RoomResponse(room.getId(), room.getMaxHeadCount());
    }
}
