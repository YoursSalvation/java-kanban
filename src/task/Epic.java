package task;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

public class Epic extends Task {
    private HashMap<Integer, SubTask> subTasks;

    public Epic(Task task) {
        super(task.getTitle(), task.getDescription(), task.getId(), task.getStatus());
        subTasks = new HashMap<>();
    }

    @Override
    public String toString() {
        return "\nEpic {" +
                "title = '" + getTitle() + '\'' +
                ", description = '" + getDescription() + '\'' +
                ", id = " + getId() +
                ", status = " + getStatus() +
                ", startTime = " + startTime.format(formatter) +
                ", duration = " + duration.toMinutes() +
                ", subTasksSize = " + subTasks.size() +
                "}";
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(HashMap<Integer, SubTask> subTasks) {
        this.subTasks = subTasks;

        Optional<SubTask> minStartTime = this.subTasks.values().stream()
                .peek(subTask -> duration = duration.plus(subTask.duration))
                .min(Comparator.comparing(subTask -> subTask.startTime));

        minStartTime.ifPresent(subTask -> startTime = subTask.startTime);
    }
}