public class SubTask extends Task {
    protected int epicId;

    public SubTask(Task task, int epicId) {
        super(task.title, task.description, task.id, task.status);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}