package io.hhplus.tdd.point.dto;

import io.hhplus.tdd.point.entity.UserPoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPointDto {

    private long id;
    private long point;
    private long updateMillis;
    private long amount;

    public static UserPointDto from(UserPoint userPoint) {

        if (userPoint == null) {
            return null;
        }

        return UserPointDto.builder()
                .id(userPoint.getId())
                .point(userPoint.getPoint())
                .updateMillis(userPoint.getUpdateMillis())
                .build();
    }
}
