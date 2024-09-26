package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.entity.UserPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {

    private final UserPointTable userPointTable;

    @Override
    public UserPoint selectById(long id) {
        log.info("Fetching UserPoint for userId: {}", id);

        UserPoint userPoint = userPointTable.selectById(id);
        log.info("UserPoint found with userId: {}, point: {}", userPoint.getId(), userPoint.getPoint());

        return userPoint;
    }

    @Override
    public UserPoint create(long id, long point) {
        log.info("Creating UserPoint for userId: {}, initial point: {}", id, point);

        UserPoint result = userPointTable.insertOrUpdate(id, point);
        log.info("UserPoint created successfully with userId: {}, point: {}", result.getId(), result.getPoint());

        return result;
    }

    @Override
    public UserPoint update(long id, long point) {
        log.info("Updating UserPoint for userId: {}, new point: {}", id, point);

        UserPoint result = userPointTable.insertOrUpdate(id, point);
        log.info("UserPoint updated successfully with userId: {}, point: {}", result.getId(), result.getPoint());

        return result;
    }
}
