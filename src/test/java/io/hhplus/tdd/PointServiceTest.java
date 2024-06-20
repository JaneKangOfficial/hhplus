package io.hhplus.tdd;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.UserPointEntity;
import io.hhplus.tdd.point.repository.PointRepository;
import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.service.PointServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static io.hhplus.tdd.point.TransactionType.CHARGE;
import static io.hhplus.tdd.point.TransactionType.USE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@WebMvcTest(PointService.class)
public class PointServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointRepository pointRepository;

    @Autowired
    private PointServiceImpl pointServiceImpl;

    /**
     given : 특정 조건 설정
     when : 실제 동작 수행
     then : 결과 검증
     **/

    @Test
    @DisplayName("특정 유저의 포인트를 조회하는 테스트")
    public void getUserPointTest() {

        // given
        long userId = 1L;
        long point = 1000L;

        UserPointEntity userPoint = new UserPointEntity(userId, point, System.currentTimeMillis());
        given(pointRepository.selectById(userId)).willReturn(userPoint);                     // return 값으로 userPoint를 주입

        // when
        UserPoint getUserPoint = pointServiceImpl.getUserPoint(userId);                     // 실행되어도 willReturn(userPoint) 객체를 반환

        // then
        verify(pointRepository, times(1)).selectById(userId);   // selectById 한 번 호출되었는지 확인
        assertNotNull(getUserPoint);                                                        // notNull 검증
        assertEquals(userId, getUserPoint.id());
        assertEquals(point, getUserPoint.point());

    }

    @Test
    @DisplayName("존재하지 않는 아이디 테스트")
    public void getUserPointNotFoundTest() {

        // given
        long userId = 999L;     // 존재하지 않는 아이디

        given(pointRepository.selectById(userId)).willReturn(null);   // return 값으로 null 주입

        // when
        UserPoint getUserPoint = pointServiceImpl.getUserPoint(userId);      // 실행되어도 willReturn(null) 객체를 반환

        // then
        verify(pointRepository, times(1)).selectById(userId);
        assertNull(getUserPoint);

        /*
        // when & then
        // pointServiceImpl.getUserPoint 구현 후에 다시 테스트코드를 실행시키니 오류 발생...?
        // pointServiceImpl에서 추가한 RuntimeException을 여기에도 적용해야 한다
        assertThrows(RuntimeException.class, () -> {
            pointServiceImpl.getUserPoint(userId);
        });
        */

        verify(pointRepository, times(1)).selectById(userId);

    }

    @Test
    @DisplayName("특정 유저의 포인트 충전/이용 내역을 조회하는 테스트")
    public void getUserHistoryTest() {

        // given
        long id1 = 100L;
        long id2 = 101L;
        long userId = 1L;
        long amount1 = 1000L;
        long amount2 = 500L;

        PointHistory pointHistory1 = new PointHistory(id1, userId, amount1, CHARGE, System.currentTimeMillis());
        PointHistory pointHistory2 = new PointHistory(id2, userId, amount2, USE, System.currentTimeMillis());
        given(pointRepository.selectAllByUserId(userId)).willReturn(Arrays.asList(pointHistory1, pointHistory2));

        // when
        List<PointHistory> getPointHistory = pointServiceImpl.selectAllByUserId(userId);   // 실행되어도 willReturn(Arrays.asList(pointHistory1, pointHistory2)) 객체를 반환

        // then
        assertEquals(2, getPointHistory.size());
        assertEquals(pointHistory1, getPointHistory.get(0));
        assertEquals(pointHistory2, getPointHistory.get(1));

    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 테스트")
    public void chargeUserPointTest() {

        // given
        long userId = 1L;
        long originalPoint = 1000L;
        long amount = 100L;

        UserPointEntity originalUserPoint = new UserPointEntity(userId, originalPoint, System.currentTimeMillis());     // 현재 포인트 설정
        given(pointRepository.selectById(userId)).willReturn(originalUserPoint);

        UserPointEntity updateUserPoint = new UserPointEntity(userId, originalPoint + amount, System.currentTimeMillis());  // 현재 포인트 + 충전 포인트 설정
        given(pointRepository.chargeUserPoint(userId, originalPoint + amount)).willReturn(updateUserPoint);

        // when
        UserPoint chargedUserPoint = pointServiceImpl.chargeUserPoint(userId, amount);  // chargeUserPoint() 실행

        // then
        verify(pointRepository).selectById(userId);
        verify(pointRepository).chargeUserPoint(userId, originalPoint + amount);    // 호출 되었는지 확인

        assertEquals(userId, chargedUserPoint.id());                                    // 사용 아이디가 일치하는지 검증
        assertEquals(originalPoint + amount, chargedUserPoint.point());     // 금액이 일치하는지 검증

    }

    @Test
    @DisplayName("포인트 충전시 음수인 경우 테스트")
    public void chargeUserPointExceptionTest() {

        // given
        long userId = 1L;
        long amount = -100L;

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> pointServiceImpl.chargeUserPoint(userId, amount));  // 에러 발생

        // then
        verify(pointRepository, times(0)).chargeUserPoint(userId, amount);  // 호출되지 않아야 한다.
//        assertEquals("금액이 0보다 작습니다.", exception.getMessage());    // 텍스트 일치하는지 확인

    }

    @Test
    @DisplayName("특정 유저의 포인트를 사용하는 테스트")
    public void useUserPointTest() {

        // given
        long userId = 1L;
        long originalPoint = 1000L;
        long amount = 100L;

        UserPointEntity originalUserPoint = new UserPointEntity(userId, originalPoint, System.currentTimeMillis());     // 현재 포인트 설정
        given(pointRepository.selectById(userId)).willReturn(originalUserPoint);

        UserPointEntity updateUserPoint = new UserPointEntity(userId, originalPoint - amount, System.currentTimeMillis());  // 현재 포인트 - 사용 포인트 설정
        given(pointRepository.chargeUserPoint(userId, originalPoint - amount)).willReturn(updateUserPoint);

        // when
        UserPoint usedUserPoint = pointServiceImpl.useUserPoint(userId, amount);    // useUserPoint() 실행

        // then
        verify(pointRepository).selectById(userId);
        verify(pointRepository).chargeUserPoint(userId, originalPoint - amount);    // 호출되었는지 확인

        assertEquals(userId, usedUserPoint.id());                                   // 사용 아이디가 일치하는지 검증
        assertEquals(originalPoint - amount, usedUserPoint.point());    // 금액이 일치하는지 검증

    }

    @Test
    @DisplayName("포인트를 사용시 잔액 부족 테스트")
    public void useUserPointExceptionTest() {

        // given
        long userId = 1L;
        long originalPoint = 900L;
        long amount = 1000L;

        UserPointEntity originalUserPoint = new UserPointEntity(userId, originalPoint, System.currentTimeMillis());     // 현재 포인트 설정
        given(pointRepository.selectById(userId)).willReturn(originalUserPoint);

        // when
        RuntimeException exception = assertThrows(RuntimeException.class, () -> pointServiceImpl.useUserPoint(userId, amount)); // 에러 발생

        // then
//        verify(pointRepository, times(0)).chargeUserPoint(userId, amount);  // 호출되지 않아야 한다.
        assertEquals("잔액이 부족합니다.", exception.getMessage());     // 텍스트 일치하는지 확인

    }

}
