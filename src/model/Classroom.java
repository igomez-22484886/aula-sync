package model;

public class Classroom {
    private int id;
    private int classroomId;
    private int capacity;
    private ClassroomStatus status;

    public Classroom(int classroomId, int capacity, ClassroomStatus status) {
        this.classroomId = classroomId;
        this.capacity = capacity;
        this.status = status;
    }

    // Constructor adicional para incluir id si es necesario (ej. al insertar en DB)
    public Classroom(int id, int classroomId, int capacity, ClassroomStatus status) {
        this.id = id;
        this.classroomId = classroomId;
        this.capacity = capacity;
        this.status = status;
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

    public ClassroomStatus getStatus() {
        return status;
    }

    public void setStatus(ClassroomStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "id=" + id +
                ", classroomId=" + classroomId +
                ", capacity=" + capacity +
                ", status='" + status + '\'' +
                '}';
    }

    public enum ClassroomStatus {
        AVAILABLE("Available"),
        RESERVED("Reserved");

        private final String label;

        ClassroomStatus(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static ClassroomStatus fromLabel(String label) {
            for (ClassroomStatus status : values()) {
                if (status.label.equalsIgnoreCase(label)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown label: " + label);
        }
    }
}