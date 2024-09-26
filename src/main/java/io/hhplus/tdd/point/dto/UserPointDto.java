package io.hhplus.tdd.point.dto;

import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.exception.InsufficientPointException;
import io.hhplus.tdd.point.exception.PointLimitExceededException;
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

    private static final long MAX_POINT_LIMIT = 1_000_000L;

    public static UserPointDto from(UserPoint userPoint) {

        if (userPoint == null) {
            return null;
        }

        return UserPointDto.builder()
                .id(userPoint.id())
                .point(userPoint.point())
                .updateMillis(userPoint.updateMillis())
                .build();
    }

    public void usePoint(long amount) {

        if (point - amount < 0L) {
            throw new InsufficientPointException("포인트가 부족하여 사용이 불가능합니다.");
        }
        this.point -= amount;
    }

    public void chargePoint(long amount) {

        if (point + amount > 1_000_000L) {
            throw new PointLimitExceededException("최대 포인트 한도인 " + MAX_POINT_LIMIT + "포인트를 초과할 수 없습니다.");
        }
        this.point += amount;
    }

    private void setUpdateMillis() {

        this.updateMillis = System.currentTimeMillis();
    }
}
