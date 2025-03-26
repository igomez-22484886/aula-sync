package model;

public class Classroom {
    private int id;
    private int capacity;
    private String status;

    public Classroom(int id, int capacity, String status) {
        this.id = id;
        this.capacity = capacity;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Classroom{" +
                "id=" + id +
                ", capacity=" + capacity +
                ", status='" + status + '\'' +
                '}';
    }
}
