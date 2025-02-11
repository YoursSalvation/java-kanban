package manager;

import manager.exception.ManagerTaskCrossingException;
import task.Epic;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();

    void create(Task task) throws ManagerTaskCrossingException;

    void create(Epic epic);

    void create(SubTask subTask) throws ManagerTaskCrossingException;

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubTasks();

    Task getTask(int id);

    void update(Task task);

    void update(Epic epic);

    void update(SubTask subTask);

    void deleteTask(int id);

    ArrayList<SubTask> getEpicSubTasks(int id);
}