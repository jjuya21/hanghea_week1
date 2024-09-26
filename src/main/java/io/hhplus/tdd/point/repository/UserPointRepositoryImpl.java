package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.entity.UserPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {

    private final UserPointTable userPointTable;

    @Override
    public Optional<UserPoint> selectById(long id) {
        log.info("Fetching UserPoint for userId: {}", id);

        Optional<UserPoint> userPoint = Optional.ofNullable(userPointTable.selectById(id));

        userPoint.ifPresent(point -> log.info("UserPoint found with userId: {}, point: {}", point.id(), point.point()));
        return userPoint;
    }

    @Override
    public Optional<UserPoint> create(long id, long point) {
        log.info("Creating UserPoint for userId: {}, initial point: {}", id, point);

        Optional<UserPoint> result = Optional.ofNullable(userPointTable.insertOrUpdate(id, point));

        result.ifPresent(userPoint -> log.info("UserPoint created successfully with userId: {}, point: {}", userPoint.id(), userPoint.point()));
        return result;
    }

    @Override
    public Optional<UserPoint> update(long id, long point) {
        log.info("Updating UserPoint for userId: {}, new point: {}", id, point);

        Optional<UserPoint> result = Optional.ofNullable(userPointTable.insertOrUpdate(id, point));

        result.ifPresent(userPoint -> log.info("UserPoint updated successfully with userId: {}, point: {}", userPoint.id(), userPoint.point()));
        return result;
    }
}
