package model;

public class Classroom {
    private int id;
    private int classroomId;
    private int capacity;

    public Classroom(int classroomId, int capacity) {
        this.classroomId = classroomId;
        this.capacity = capacity;
    }

    // Constructor adicional para incluir id si es necesario (ej. al insertar en DB)
    public Classroom(int id, int classroomId, int capacity) {
        this.id = id;
        this.classroomId = classroomId;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(int classroomId) {
        this.classroomId = classroomId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "id=" + id +
                ", classroomId=" + classroomId +
                ", capacity=" + capacity + '\'' +
                '}';
    }
}