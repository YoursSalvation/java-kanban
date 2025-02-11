package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private String title;
    private String description;
    private int id;
    private Status status;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    public Task(String title, String description, int id, Status status, Duration duration, LocalDateTime startTime) {
        this.title = title;
        this.id = id;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String title, String description, int id, Status status) { // Конструктор для эпиков
        this.title = title;
        this.id = id;
        this.description = description;
        this.status = status;
        this.duration = Duration.ofMinutes(0);
        this.startTime = LocalDateTime.now();
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return "\nTask {" +
                "title = '" + title + '\'' +
                ", description = '" + description + '\'' +
                ", id = " + id +
                ", status = " + status +
                ", startTime = " + startTime.format(formatter) +
                ", duration = " + duration.toMinutes() +
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

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
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

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }
}