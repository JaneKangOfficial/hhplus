package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;

import java.util.List;

public interface PointService {

    UserPoint getUserPoint(long id);

    List<PointHistory> selectAllByUserId(long userId);

    UserPoint chargeUserPoint(long id, long amount);

    UserPoint useUserPoint(long id, long amount);

}
