package Task;

public class SubTask extends Task {
    private int epicId;

    public SubTask(Task task, int epicId) {
        super(task.getTitle(), task.getDescription(), task.getId(), task.getStatus());
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
                "}";
    }
}