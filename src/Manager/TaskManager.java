package Manager;

import Task.*;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    void create(Task task);

    void create(Epic epic);

    void create(SubTask subTask);

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