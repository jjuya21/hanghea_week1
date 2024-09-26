package io.hhplus.tdd.point.integration;

import io.hhplus.tdd.point.dto.PointHistoryDto;
import io.hhplus.tdd.point.dto.UserPointDto;
import io.hhplus.tdd.point.exception.InsufficientPointException;
import io.hhplus.tdd.point.exception.PointLimitExceededException;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
class PointServiceTest {

    @Autowired
    PointService pointService;

    private static final long DEFAULT_USER_ID = 1L;
    private static final long INITIAL_POINT = 500L;
    private static final long CHARGE_AMOUNT = 100L;
    private static final long USE_AMOUNT = 50L;

    @BeforeEach
    void setUp() {

        pointService.join(DEFAULT_USER_ID, INITIAL_POINT);
    }

    @DisplayName("포인트 조회 성공")
    @Test
    void getPointTest() {
        // given - setup()에서 기본적으로 설정된 값

        // when
        UserPointDto result = pointService.getPoint(DEFAULT_USER_ID);

        // then
        assertThat(result.getPoint()).isEqualTo(INITIAL_POINT);
    }

    @DisplayName("포인트 충전 성공")
    @Test
    void chargePointTest() {
        // given - setup()에서 기본 값이 설정된 상태에서 추가적으로 충전

        // when
        pointService.chargePoint(DEFAULT_USER_ID, CHARGE_AMOUNT);
        UserPointDto result = pointService.getPoint(DEFAULT_USER_ID);

        // then
        assertThat(result.getPoint()).isEqualTo(INITIAL_POINT + CHARGE_AMOUNT);
    }

    @DisplayName("포인트 사용 성공")
    @Test
    void usePointTest() {
        // given - setup()에서 기본 값이 설정된 상태에서 추가적으로 포인트 사용

        // when
        pointService.usePoint(DEFAULT_USER_ID, USE_AMOUNT);
        UserPointDto result = pointService.getPoint(DEFAULT_USER_ID);

        // then
        assertThat(result.getPoint()).isEqualTo(INITIAL_POINT - USE_AMOUNT);
    }

    @DisplayName("포인트 내역 조회 성공")
    @Test
    void getPointHistoryTest() {
        // given - 새로운 사용자에 대해 포인트 충전 및 사용 기록
        long userId = 2L;
        pointService.join(userId, INITIAL_POINT);
        pointService.chargePoint(userId, CHARGE_AMOUNT);
        pointService.usePoint(userId, USE_AMOUNT);

        // when
        List<PointHistoryDto> histories = pointService.getPointHistory(userId);

        // then
        assertThat(histories).hasSize(2);
    }

    @DisplayName("포인트 충전 최대한도로 인한 실패")
    @Test
    void chargePoint_whenExceedsMaxPointLimit_throwsException() {
        // given
        UserPointDto userPoint = new UserPointDto(1L, 990_000L, System.currentTimeMillis());

        // when & then
        assertThatThrownBy(() -> userPoint.chargePoint(14000L))
                .isInstanceOf(PointLimitExceededException.class)
                .hasMessage("최대 포인트 한도인 " + 1_000_000L + "포인트를 초과할 수 없습니다.");
    }

    @DisplayName("포인트 사용 잔액부족으로 실패")
    @Test
    void usePoint_whenBelowMinimumPointLimit_throwsException() {
        // given
        UserPointDto userPoint = new UserPointDto(1L, 1000L, System.currentTimeMillis());

        // when & then
        assertThatThrownBy(() -> userPoint.usePoint(1500L))
                .isInstanceOf(InsufficientPointException.class)
                .hasMessage("포인트가 부족하여 사용이 불가능합니다.");
    }

    @DisplayName("한 id로 동시의 10개 포인트 충전요청처리 성공")
    @Test
    void testConcurrentChargePoints() throws InterruptedException {
        // given 기본 유저 초기화
        pointService.join(DEFAULT_USER_ID, INITIAL_POINT);

        // when
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> pointService.chargePoint(DEFAULT_USER_ID, CHARGE_AMOUNT));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // then
        UserPointDto userPoint = pointService.getPoint(DEFAULT_USER_ID);
        assertThat(userPoint.getPoint()).isEqualTo(INITIAL_POINT + (CHARGE_AMOUNT * 10));
    }

    @DisplayName("한 id로 동시의 10개 포인트 사용요청처리 성공")
    @Test
    void testConcurrentUsePoints() throws InterruptedException {
        // given 기본 유저 초기화
        pointService.join(DEFAULT_USER_ID, INITIAL_POINT);

        // when
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> pointService.usePoint(DEFAULT_USER_ID, USE_AMOUNT));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // then
        UserPointDto userPoint = pointService.getPoint(DEFAULT_USER_ID);
        assertThat(userPoint.getPoint()).isEqualTo(INITIAL_POINT - (USE_AMOUNT * 10));
    }
}

