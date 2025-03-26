package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation {
    private int id;
    private int userId;
    private int classroomId;
    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;

    public Reservation(int id, int userId, int classroomId, LocalDate reservationDate, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.userId = userId;
        this.classroomId = classroomId;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(int classroomId) {
        this.classroomId = classroomId;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", userId=" + userId +
                ", classroomId=" + classroomId +
                ", reservationDate=" + reservationDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
