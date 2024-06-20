package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPointEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PointRepositoryImpl implements PointRepository{

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    @Autowired
    public PointRepositoryImpl(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    @Override
//    public UserPoint selectById(long id) {
    public UserPointEntity selectById(long id) {
        return userPointTable.selectById(id);
    }

    @Override
    public List<PointHistory> selectAllByUserId(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

    @Override
//    public UserPoint chargeUserPoint(long id, long amount) {
    public UserPointEntity chargeUserPoint(long id, long amount) {
        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());   // 포인트 충전 내역 insert
        return userPointTable.insertOrUpdate(id, amount);   // 포인트 충전

    }
}
