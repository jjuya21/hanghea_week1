package io.hhplus.tdd.point.unit;

import io.hhplus.tdd.point.dto.PointHistoryDto;
import io.hhplus.tdd.point.dto.UserPointDto;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.TransactionType;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.service.PointServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class PointServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(PointServiceImplTest.class);
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

    @DisplayName("포인트 조회")
    @Test
    void getPoint() {
        // given
        UserPoint userPoint = new UserPoint(userId, initialPoint, System.currentTimeMillis());

        given(userPointRepository.selectById(anyLong())).willReturn(Optional.of(userPoint));

        // when
        UserPointDto userPointDto = pointService.getPoint(userId);

        // then
        assertThat(userPointDto.getPoint()).isEqualTo(userPointDto.getPoint());
        then(userPointRepository).should().selectById(anyLong());
    }

    @DisplayName("포인트 사용/충전 내역조회 성공")
    @Test
    void getPointHistory() {
        // given
        PointHistory pointHistory_1 = new PointHistory(1, userId, 2000L, TransactionType.USE, System.currentTimeMillis());
        PointHistory pointHistory_2 = new PointHistory(2, userId, 10000L, TransactionType.CHARGE, System.currentTimeMillis());

        given(pointHistoryRepository.selectAllByUserId(anyLong())).willReturn(Arrays.asList(pointHistory_1, pointHistory_2));


        // when
        List<PointHistoryDto> pointHistories = pointService.getPointHistory(userId);

        // then
        assertThat(pointHistories).hasSize(2);
        then(pointHistoryRepository).should().selectAllByUserId(anyLong());
    }

    @DisplayName("포인트 사용 성공")
    @Test
    void usePointTest() {
        // given
        long useAmount = 200L;

        UserPoint userPoint = new UserPoint(userId, initialPoint, System.currentTimeMillis());

        given(userPointRepository.selectById(anyLong())).willReturn(Optional.of(userPoint));
        given(userPointRepository.update(anyLong(), anyLong()))
                .willReturn(Optional.of(new UserPoint(userId, initialPoint - useAmount, System.currentTimeMillis())));

        // when
        UserPointDto updatedUserPoint = pointService.usePoint(userId, useAmount);

        // then
        assertThat(updatedUserPoint.getPoint()).isEqualTo(initialPoint - useAmount);
        then(userPointRepository).should().selectById(userId);
        then(userPointRepository).should().update(userId, initialPoint - useAmount);
        then(pointHistoryRepository).should().create(anyLong(), anyLong(), eq(TransactionType.USE), anyLong());
    }

    @DisplayName("포인트 충전 성공")
    @Test
    void chargePointTest() {
        // given
        long chargeAmount = 200L;
        UserPoint userPoint = new UserPoint(userId, initialPoint, System.currentTimeMillis());

        given(userPointRepository.selectById(anyLong())).willReturn(Optional.of(userPoint));
        given(userPointRepository.update(anyLong(), anyLong()))
                .willReturn(Optional.of(new UserPoint(userId, initialPoint + chargeAmount, System.currentTimeMillis())));

        // when
        UserPointDto updatedUserPoint = pointService.chargePoint(userId, chargeAmount);

        // then
        assertThat(updatedUserPoint.getPoint()).isEqualTo(initialPoint + chargeAmount);
        then(userPointRepository).should().selectById(userId);
        then(userPointRepository).should().update(userId, initialPoint + chargeAmount);
        then(pointHistoryRepository).should().create(anyLong(), anyLong(), eq(TransactionType.CHARGE), anyLong());
    }
}
