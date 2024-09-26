package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.dto.PointHistoryDto;
import io.hhplus.tdd.point.dto.UserPointDto;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.TransactionType;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.exception.NegativeValueException;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final UserPointRepository userPointRepository;

    private final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    @Override
    public UserPoint join(UserPointDto userPointDto) {

        long id = userPointDto.getId();
        long point = userPointDto.getPoint();
        log.info("User with ID: {} is joining with initial point: {}", id, point);

        return userPointRepository.create(id, point);
    }

    @Override
    public UserPoint getPoint(UserPointDto userPointDto) {

        long id = userPointDto.getId();
        log.info("Fetching points for user with ID: {}", id);
        return userPointRepository.selectById(id);
    }

    @Override
    public List<PointHistory> getPointHistory(PointHistoryDto pointHistoryDto) {

        long userId = pointHistoryDto.getUserId();
        log.info("Retrieving point history for user with ID: {}", userId);

        List<PointHistory> histories = pointHistoryRepository.selectAllByUserId(userId);
        log.info("Found {} point history records for user with ID: {}", histories.size(), userId);

        return histories;
    }

    @Override
    public UserPoint chargePoint(UserPointDto userPointDto) {

        long id = userPointDto.getId();
        long amount = userPointDto.getAmount();
        log.info("Charging user with ID: {} by amount: {}", id, amount);

        validatePositiveAmount(amount);

        ReentrantLock lock = lockMap.computeIfAbsent(id, k -> new ReentrantLock(true));
        lock.lock();
        try {
            UserPoint userPoint = getPoint(userPointDto);
            userPoint.chargePoint(amount);

            pointHistoryRepository.create(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
            log.info("Charged user with ID: {} successfully, new balance: {}", id, userPoint.getPoint());

            return userPointRepository.update(userPoint.getId(), userPoint.getPoint());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public UserPoint usePoint(UserPointDto userPointDto) {

        long id = userPointDto.getId();
        long amount = userPointDto.getAmount();
        log.info("Using points for user with ID: {} by amount: {}", id, amount);

        validatePositiveAmount(amount);

        ReentrantLock lock = lockMap.computeIfAbsent(id, k -> new ReentrantLock(true));
        lock.lock();
        try {
            UserPoint userPoint = getPoint(userPointDto);
            userPoint.usePoint(amount);

            pointHistoryRepository.create(id, amount, TransactionType.USE, System.currentTimeMillis());
            log.info("Used points for user with ID: {} successfully, new balance: {}", id, userPoint.getPoint());

            return userPointRepository.update(userPoint.getId(), userPoint.getPoint());
        } finally {
            lock.unlock();
        }
    }

    private void validatePositiveAmount(long amount) {

        if (amount < 0) {

            log.error("Invalid amount: {}. Amount must be positive.", amount);
            throw new NegativeValueException("Amount는 음수일 수 없습니다.");
        }
    }
}
