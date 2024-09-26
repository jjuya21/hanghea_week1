package io.hhplus.tdd.point.unit;

import io.hhplus.tdd.point.dto.PointHistoryDto;
import io.hhplus.tdd.point.dto.UserPointDto;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.TransactionType;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.exception.InsufficientPointException;
import io.hhplus.tdd.point.exception.NegativeValueException;
import io.hhplus.tdd.point.exception.PointLimitExceededException;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.service.PointServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class PointServiceImplTest {

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Mock
    private UserPointRepository userPointRepository;

    @InjectMocks
    private PointServiceImpl pointService;

    private final static long userId = 1L;
    private final static long initialPoint = 1000L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("포인트 조회 시 같은 point값을 반환해야한다.")
    @Test
    void getPoint() {
        // given
        UserPointDto defaultUser = UserPointDto.builder().id(userId).point(initialPoint).build();
        UserPoint userPoint = new UserPoint(userId, initialPoint, System.currentTimeMillis());

        given(userPointRepository.selectById(anyLong())).willReturn(userPoint);

        // when
        UserPoint result = pointService.getPoint(defaultUser);

        // then
        assertThat(result.getPoint()).isEqualTo(userPoint.getPoint());
        then(userPointRepository).should().selectById(anyLong());
    }

    @DisplayName("포인트 사용/충전 내역조회 시 userId를 기준으로 충전/사용 내역들을 반환해야한다.")
    @Test
    void getPointHistory() {
        // given
        PointHistoryDto defaultUser = PointHistoryDto.builder().id(userId).build();
        PointHistory pointHistory_1 = new PointHistory(1, userId, 2000L, TransactionType.USE, System.currentTimeMillis());
        PointHistory pointHistory_2 = new PointHistory(2, userId, 10000L, TransactionType.CHARGE, System.currentTimeMillis());

        given(pointHistoryRepository.selectAllByUserId(anyLong())).willReturn(Arrays.asList(pointHistory_1, pointHistory_2));

        // when
        List<PointHistory> pointHistories = pointService.getPointHistory(defaultUser);

        // then
        assertThat(pointHistories).hasSize(2);
        then(pointHistoryRepository).should().selectAllByUserId(anyLong());
    }

    @DisplayName("포인트 사용 시 (기본 point값 - 사용 point값)을 반환해야한다.")
    @Test
    void usePointTest() {
        // given
        long useAmount = 200L;
        UserPointDto defaultUser = UserPointDto.builder().id(userId).amount(useAmount).build();

        UserPoint userPoint = new UserPoint(userId, initialPoint, System.currentTimeMillis());

        given(userPointRepository.selectById(anyLong())).willReturn(userPoint);
        given(userPointRepository.update(anyLong(), anyLong()))
                .willReturn(new UserPoint(userId, initialPoint - useAmount, anyLong()));

        // when
        UserPoint updatedUserPoint = pointService.usePoint(defaultUser);

        // then
        assertThat(updatedUserPoint.getPoint()).isEqualTo(initialPoint - useAmount);
        then(userPointRepository).should().selectById(userId);
        then(userPointRepository).should().update(userId, initialPoint - useAmount);
        then(pointHistoryRepository).should().create(anyLong(), anyLong(), eq(TransactionType.USE), anyLong());
    }

    @DisplayName("포인트 충전 시 (기본 point값 + 충전 point값)을 반환해야한다.")
    @Test
    void chargePointTest() {
        // given
        long chargeAmount = 200L;
        UserPointDto defaultUser = UserPointDto.builder().id(userId).amount(chargeAmount).build();

        UserPoint userPoint = new UserPoint(userId, initialPoint, System.currentTimeMillis());

        given(userPointRepository.selectById(anyLong())).willReturn(userPoint);
        given(userPointRepository.update(anyLong(), anyLong()))
                .willReturn(new UserPoint(userId, initialPoint + chargeAmount, anyLong()));

        // when
        UserPoint updatedUserPoint = pointService.chargePoint(defaultUser);

        // then
        assertThat(updatedUserPoint.getPoint()).isEqualTo(initialPoint + chargeAmount);
        then(userPointRepository).should().selectById(userId);
        then(userPointRepository).should().update(userId, initialPoint + chargeAmount);
        then(pointHistoryRepository).should().create(anyLong(), anyLong(), eq(TransactionType.CHARGE), anyLong());
    }

    @DisplayName("포인트 사용 시 잔액이 부족하면 InsufficientPointException이 발생해야 한다.")
    @Test
    void usePoint_whenInsufficientPoints_throwsException() {
        // given
        long useAmount = 6000L;
        UserPointDto defaultUser = UserPointDto.builder().id(userId).amount(useAmount).build();

        UserPoint userPoint = new UserPoint(userId, initialPoint, System.currentTimeMillis());

        given(userPointRepository.selectById(anyLong())).willReturn(userPoint);

        // when & then
        assertThatThrownBy(() -> pointService.usePoint(defaultUser))
                .isInstanceOf(InsufficientPointException.class);

        then(userPointRepository).should().selectById(userId);
        then(userPointRepository).should(never()).update(anyLong(), anyLong());
        then(pointHistoryRepository).should(never()).create(anyLong(), anyLong(), eq(TransactionType.USE), anyLong());
    }

    @DisplayName("포인트 충전 시 한도를 초과하면 PointLimitExceededException이 발생해야 한다.")
    @Test
    void chargePoint_whenExceedsMaxPointLimit_throwsException() {
        // given
        long chargeAmount = 999_900L;
        UserPointDto defaultUser = UserPointDto.builder().id(userId).amount(chargeAmount).build();

        UserPoint userPoint = new UserPoint(userId, initialPoint, System.currentTimeMillis());

        given(userPointRepository.selectById(anyLong())).willReturn(userPoint);

        // when & then
        assertThatThrownBy(() -> pointService.chargePoint(defaultUser))
                .isInstanceOf(PointLimitExceededException.class);

        then(userPointRepository).should().selectById(userId);
        then(userPointRepository).should(never()).update(anyLong(), anyLong());
        then(pointHistoryRepository).should(never()).create(anyLong(), anyLong(), eq(TransactionType.CHARGE), anyLong());
    }

    @DisplayName("포인트 충전 시 음수 값이 주어지면 NegativeValueException이 발생해야 한다.")
    @Test
    void chargePoint_whenAmountIsNegative_throwsException() {
        // given
        long negativeAmount = -200L;
        UserPointDto defaultUser = UserPointDto.builder().id(userId).amount(negativeAmount).build();

        // when & then
        assertThatThrownBy(() -> pointService.chargePoint(defaultUser))
                .isInstanceOf(NegativeValueException.class);

        then(userPointRepository).should(never()).selectById(anyLong());
        then(userPointRepository).should(never()).update(anyLong(), anyLong());
        then(pointHistoryRepository).should(never()).create(anyLong(), anyLong(), eq(TransactionType.CHARGE), anyLong());
    }
}
