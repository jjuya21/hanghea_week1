package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.dto.PointHistoryDto;
import io.hhplus.tdd.point.dto.UserPointDto;
import io.hhplus.tdd.point.entity.TransactionType;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final UserPointRepository userPointRepository;

    private final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    @Override
    public UserPointDto join(long id, long point) {

        log.info("User with ID: {} is joining with initial point: {}", id, point);

        return userPointRepository.create(id, point)
                .map(UserPointDto::from)
                .orElse(null);
    }

    @Override
    public UserPointDto getPoint(long id) {

        log.info("Fetching points for user with ID: {}", id);

        return userPointRepository.selectById(id)
                .map(UserPointDto::from)
                .orElse(null);
    }

    @Override
    public List<PointHistoryDto> getPointHistory(long userId) {
        log.info("Retrieving point history for user with ID: {}", userId);

        List<PointHistoryDto> histories = pointHistoryRepository.selectAllByUserId(userId).stream()
                .map(PointHistoryDto::from)
                .collect(Collectors.toList());

        log.info("Found {} point history records for user with ID: {}", histories.size(), userId);

        return histories;
    }


    @Override
    public UserPointDto chargePoint(long id, long amount) {

        log.info("Charging user with ID: {} by amount: {}", id, amount);

        validatePositiveAmount(amount);

        ReentrantLock lock = lockMap.computeIfAbsent(id, k -> new ReentrantLock());
        lock.lock();
        try {
            UserPointDto userPoint = getPoint(id);
            userPoint.chargePoint(amount);

            pointHistoryRepository.create(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
            log.info("Charged user with ID: {} successfully, new balance: {}", id, userPoint.getPoint());

            return userPointRepository.update(userPoint.getId(), userPoint.getPoint())
                    .map(UserPointDto::from)
                    .orElse(null);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public UserPointDto usePoint(long id, long amount) {

        log.info("Using points for user with ID: {} by amount: {}", id, amount);

        validatePositiveAmount(amount);

        ReentrantLock lock = lockMap.computeIfAbsent(id, k -> new ReentrantLock());
        lock.lock();
        try {
            UserPointDto userPoint = getPoint(id);
            userPoint.usePoint(amount);

            pointHistoryRepository.create(id, amount, TransactionType.USE, System.currentTimeMillis());
            log.info("Used points for user with ID: {} successfully, new balance: {}", id, userPoint.getPoint());

            return userPointRepository.update(userPoint.getId(), userPoint.getPoint())
                    .map(UserPointDto::from)
                    .orElse(null);
        } finally {
            lock.unlock();
        }
    }

    private void validatePositiveAmount(long amount) {

        if (amount < 0) {

            log.error("Invalid amount: {}. Amount must be positive.", amount);
            throw new NegativeArraySizeException("Amount는 음수일 수 없습니다.");
        }
    }
}
