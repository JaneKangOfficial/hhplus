package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPointEntity;

import java.util.List;

public interface PointRepository {

    UserPointEntity selectById(long id);

    List<PointHistory> selectAllByUserId(long id);

    UserPointEntity chargeUserPoint(long id, long l);
}
