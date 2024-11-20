import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, SubTask> subTasks;

    public Epic(Task task) {
        super(task.getTitle(), task.getDescription(), task.getId(), task.getStatus());
        subTasks = new HashMap<>();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                '}';
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(HashMap<Integer, SubTask> subTasks) {
        this.subTasks = subTasks;
    }
}