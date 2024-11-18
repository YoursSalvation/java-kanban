import java.util.HashMap;

public class Epic extends Task {
    protected HashMap<Integer, SubTask> subTasks;

    public Epic(Task task) {
        super(task.title, task.description, task.id, task.status);
        subTasks = new HashMap<>();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public void controlEpicStatus() {
        int doneCount = 0;
        int newCount = 0;
        int inProgressCount = 0;

        for (SubTask value : subTasks.values()) {
            if (value.status.equals(Status.DONE)) doneCount += 1;
            else if (value.status.equals(Status.NEW)) newCount += 1;
            else if (value.status.equals(Status.IN_PROGRESS)) inProgressCount += 1;
        }
        if (newCount == 0 && inProgressCount == 0 && doneCount > 0) status = Status.DONE;
        else if (doneCount == 0 && inProgressCount == 0) status = Status.NEW;
        else status = Status.IN_PROGRESS;
    }
}