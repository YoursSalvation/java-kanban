public class SubTask extends Task {
    private int epicId;

    public SubTask(Task task, int epicId) {
        super(task.getTitle(), task.getDescription(), task.getId(), task.getStatus());
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                '}';
    }
}