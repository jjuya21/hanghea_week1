package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.entity.UserPoint;

public interface UserPointRepository {

    public UserPoint selectById(long id);

    public UserPoint create(long id, long point);

    public UserPoint update(long id, long point);
}
