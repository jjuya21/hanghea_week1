package io.hhplus.tdd.point.integration;

import io.hhplus.tdd.point.dto.PointHistoryDto;
import io.hhplus.tdd.point.dto.UserPointDto;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
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

        pointService.join(UserPointDto.builder().id(DEFAULT_USER_ID).point(INITIAL_POINT).build());
    }

    @DisplayName("포인트 조회 시 기본 point값을 반환해야한다.")
    @Test
    void getPointTest() {
        // given - setup()에서 기본적으로 설정된 값

        // when
        UserPoint result = pointService.getPoint(UserPointDto.builder().id(DEFAULT_USER_ID).build());

        // then
        assertThat(result.getPoint()).isEqualTo(INITIAL_POINT);
    }

    @DisplayName("포인트 충전 시 (기본 point값 + 충전 point값)을 반환해야한다.")
    @Test
    void chargePointTest() {
        // given - setup()에서 기본 값이 설정된 상태에서 추가적으로 충전

        // when
        pointService.chargePoint(UserPointDto.builder().id(DEFAULT_USER_ID).amount(CHARGE_AMOUNT).build());
        UserPoint result = pointService.getPoint(UserPointDto.builder().id(DEFAULT_USER_ID).build());

        // then
        assertThat(result.getPoint()).isEqualTo(INITIAL_POINT + CHARGE_AMOUNT);
    }

    @DisplayName("포인트 사용 성공 시 (기본 point값 - 사용 point값)을 반환해야한다.")
    @Test
    void usePointTest() {
        // given - setup()에서 기본 값이 설정된 상태에서 추가적으로 포인트 사용

        // when
        pointService.usePoint(UserPointDto.builder().id(DEFAULT_USER_ID).amount(USE_AMOUNT).build());
        UserPoint result = pointService.getPoint(UserPointDto.builder().id(DEFAULT_USER_ID).build());

        // then
        assertThat(result.getPoint()).isEqualTo(INITIAL_POINT - USE_AMOUNT);
    }

    @DisplayName("포인트 내역 조회 시 userId를 기준으로 충전/사용 내역들을 반환해야한다.")
    @Test
    void getPointHistoryTest() {
        // given - 새로운 사용자에 대해 포인트 충전 및 사용 기록
        long userId = 2L;
        pointService.join(UserPointDto.builder().id(userId).point(INITIAL_POINT).build());
        pointService.chargePoint(UserPointDto.builder().id(userId).amount(CHARGE_AMOUNT).build());
        pointService.usePoint(UserPointDto.builder().id(userId).amount(USE_AMOUNT).build());

        // when
        List<PointHistory> histories = pointService.getPointHistory(PointHistoryDto.builder().userId(userId).build());

        // then
        assertThat(histories).hasSize(2);
    }

    @DisplayName("포인트 충전 시 최대한도를 넘겨 충전하면 Exception이 발생한다.")
    @Test
    void chargePoint_whenExceedsMaxPointLimit_throwsException() {
        // given
        UserPoint userPoint = new UserPoint(1L, 990_000L, System.currentTimeMillis());

        // when & then
        assertThatThrownBy(() -> userPoint.chargePoint(14000L))
                .isInstanceOf(PointLimitExceededException.class)
                .hasMessage("최대 포인트 한도인 " + 1_000_000L + "포인트를 초과할 수 없습니다.");
    }

    @DisplayName("포인트 사용 시 잔액부족이 부족하면 Exception을 던진다.")
    @Test
    void usePoint_whenBelowMinimumPointLimit_throwsException() {
        // given
        UserPoint userPoint = new UserPoint(1L, 1000L, System.currentTimeMillis());

        // when & then
        assertThatThrownBy(() -> userPoint.usePoint(1500L))
                .isInstanceOf(InsufficientPointException.class)
                .hasMessage("포인트가 부족하여 사용이 불가능합니다.");
    }

    @DisplayName("한 id로 동시의 10개 포인트 충전요청처리 시 차례대로 처리해야한다.")
    @Test
    void testConcurrentChargePoints() throws InterruptedException {
        // given 기본 유저 초기화
        pointService.join(UserPointDto.builder().id(DEFAULT_USER_ID).point(INITIAL_POINT).build());

        // when
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> pointService.chargePoint(UserPointDto.builder().id(DEFAULT_USER_ID).amount(CHARGE_AMOUNT).build()));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // then
        UserPoint userPoint = pointService.getPoint(UserPointDto.builder().id(DEFAULT_USER_ID).build());
        assertThat(userPoint.getPoint()).isEqualTo(INITIAL_POINT + (CHARGE_AMOUNT * 10));
    }

    @DisplayName("한 id로 동시의 10개 포인트 사용요청처리 시 차례대로 처리해야한다.")
    @Test
    void testConcurrentUsePoints() throws InterruptedException {
        // given 기본 유저 초기화
        pointService.join(UserPointDto.builder().id(DEFAULT_USER_ID).point(INITIAL_POINT).build());

        // when
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> pointService.usePoint(UserPointDto.builder().id(DEFAULT_USER_ID).amount(USE_AMOUNT).build()));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // then
        UserPoint userPoint = pointService.getPoint(UserPointDto.builder().id(DEFAULT_USER_ID).build());
        assertThat(userPoint.getPoint()).isEqualTo(INITIAL_POINT - (USE_AMOUNT * 10));
    }
}
