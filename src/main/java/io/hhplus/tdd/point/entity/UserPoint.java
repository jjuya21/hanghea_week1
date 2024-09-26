package io.hhplus.tdd.point.entity;

import io.hhplus.tdd.point.exception.InsufficientPointException;
import io.hhplus.tdd.point.exception.PointLimitExceededException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPoint {
    long id;
    long point;
    long updateMillis;

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    private static final long MAX_POINT_LIMIT = 1_000_000L;

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