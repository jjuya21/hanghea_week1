package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.entity.UserPoint;

import java.util.Optional;

public interface UserPointRepository {

    public Optional<UserPoint> selectById(long id);

    public Optional<UserPoint> create(long id, long point);

    public Optional<UserPoint> update(long id, long point);
}
