package io.hhplus.tdd.point;

public class UserPointEntity {

    private long id;
    private long point;
    private long updateMillis;

    // 기본 생성자
    public UserPointEntity() {

    }

    // 생성자
    public UserPointEntity(long id, long point, long updateMillis) {
        this.id = id;
        this.point = point;
        this.updateMillis = updateMillis;
    }

    // Getter 및 Setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
    }

    public long getUpdateMillis() {
        return updateMillis;
    }

    public void setUpdateMillis(long updateMillis) {
        this.updateMillis = updateMillis;
    }

    public static UserPointEntity empty(long id) {
        return new UserPointEntity(id, 0, System.currentTimeMillis());
    }

}
