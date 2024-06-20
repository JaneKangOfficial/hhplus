package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.UserPointEntity;
import io.hhplus.tdd.point.repository.PointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointServiceImpl implements PointService{

    private final PointRepository pointRepository;

    @Autowired
    public PointServiceImpl(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    @Override
    public UserPoint getUserPoint(long id) {
        /*
        DTO로 작업했을 경우
            UserPoint userPoint = pointRepository.selectById(id);     // DTO
            if (userPoint == null) {    // 존재하지 않는 아이디일 경우
                throw new RuntimeException("사용자를 찾을 수 없습니다.");
            }
            return userPoint;
         */

        // DTO -> Entity -> DTO
        UserPointEntity userPoint = pointRepository.selectById(id); // Entity로 받기

        if (userPoint == null) {    // 존재하지 않는 아이디일 경우
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        return convertToDTO(userPoint);   // Entity -> DTO로 변환
    }

    @Override
    public List<PointHistory> selectAllByUserId(long id) {
        return pointRepository.selectAllByUserId(id);   // 포인트 충전/이용 내역을 조회
    }

    @Override
    public UserPoint chargeUserPoint(long id, long amount) {
        /*
        DTO로 작업했을 경우
            UserPoint userPoint = pointRepository.selectById(id);
            long originalPoint = userPoint.point();                 // 현재 point 가져오기
            return pointRepository.chargeUserPoint(id, originalPoint + amount); // 충전하기
        */

        if(amount <= 0) {
            throw new IllegalArgumentException("금액이 0보다 작습니다.");
        }

        // DTO -> Entity -> DTO
        UserPointEntity userPoint = pointRepository.selectById(id); // Entity로 받기
        long originalPoint = userPoint.getPoint();
        return convertToDTO(pointRepository.chargeUserPoint(id, originalPoint + amount)); // Entity -> DTO로 변환
    }

    @Override
    public UserPoint useUserPoint(long id, long amount) {
        /*
        DTO로 작업했을 경우
            UserPoint userPoint = pointRepository.selectById(id); // Entity로 받기
            long originalPoint = userPoint.point();                 // 현재 point 가져오기
            return pointRepository.chargeUserPoint(id, originalPoint - amount); // 사용하기
        */

        // DTO -> Entity -> DTO
        UserPointEntity userPoint = pointRepository.selectById(id); // Entity로 받기
        long originalPoint = userPoint.getPoint();

        if (originalPoint < amount) {
            throw new RuntimeException("잔액이 부족합니다.");
        }
        return convertToDTO(pointRepository.chargeUserPoint(id, originalPoint - amount)); // Entity -> DTO로 변환
    }

    // Entity -> DTO 로 변환
    private UserPoint convertToDTO(UserPointEntity userPoint) {
        return new UserPoint(userPoint.getId(), userPoint.getPoint(), userPoint.getUpdateMillis());
    }
}
