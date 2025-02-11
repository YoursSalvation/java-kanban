package task;

public class SubTask extends Task {
    private int epicId;

    public SubTask(Task task, int epicId) {
        super(task.getTitle(), task.getDescription(), task.getId(), task.getStatus(), task.getDuration()
                , task.getStartTime());
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public boolean setEpicId(int epicId) {
        if (this.getId() == epicId) return false;
        this.epicId = epicId;
        return true;
    }

    @Override
    public String toString() {
        return "\nSubTask {" +
                "epicId = " + epicId +
                ", title = '" + getTitle() + '\'' +
                ", description = '" + getDescription() + '\'' +
                ", id = " + getId() +
                ", status = " + getStatus() +
                ", startTime = " + startTime.format(formatter) +
                ", duration = " + duration.toMinutes() +
                "}";
    }
}