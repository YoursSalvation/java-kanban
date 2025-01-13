package Task;

import java.util.Objects;

public class Task {
    private String title;
    private String description;
    private int id;
    private Status status;

    public Task(String title, String description, int id, Status status) {
        this.title = title;
        this.id = id;
        this.description = description;
        this.status = status;
    }

    @Override
    public String toString() {
        return "\nTask {" +
                "title = '" + title + '\'' +
                ", description = '" + description + '\'' +
                ", id = " + id +
                ", status = " + status +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }
}