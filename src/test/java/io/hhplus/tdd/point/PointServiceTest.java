package io.hhplus.tdd.point;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PointServiceTest {

    @Autowired
    PointService pointService;

    @Test
    void getPoint_whenUserExists_returnsCorrectUserPoint() {
        // given
        Long userId = 1L;
        long userPoint = 100L;
        pointService.chargePoint(userId, userPoint);

        // when
        UserPoint result = pointService.getPoint(userId);

        // then
        assertNotNull(result);
        assertEquals(userPoint, result.point());
    }

    @Test
    void usePoint_whenPointsAreInsufficient_throwsException() {
        // given
        Long userId = 1L;
        long addPoint = 50L;
        pointService.chargePoint(userId, addPoint);

        // when
        long usePoint = 100L;
        UserPoint result = pointService.usePoint(userId, usePoint);

        // then
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(addPoint - usePoint, result.point());
    }

    @Test
    void addPoint_whenAddingValidPoints_updatesUserPointSuccessfully() {
        // given
        Long userId = 1L;
        long addPoint = 50L;
        pointService.chargePoint(userId, addPoint);

        // when
        long additionalPoint = 100L;
        UserPoint result = pointService.chargePoint(userId, additionalPoint);

        // then
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(addPoint + additionalPoint, result.point());
    }

    @Test
    void getPointHistory_whenUserHasHistory_returnsAllPointHistories() {
        // given
        Long userId = 1L;
        long addPoint = 500L;
        long usePoint = 100L;
        pointService.chargePoint(userId, addPoint);
        pointService.usePoint(userId, usePoint);

        // when
        List<PointHistory> result = pointService.getPointHistory(userId);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
