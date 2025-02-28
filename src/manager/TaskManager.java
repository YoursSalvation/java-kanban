package manager;

import manager.exception.ManagerTaskCrossingException;
import manager.exception.NotFoundException;
import task.Epic;
import task.SubTask;
import task.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();

    void create(Task task) throws ManagerTaskCrossingException;

    void create(Epic epic);

    void create(SubTask subTask) throws ManagerTaskCrossingException;

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    List<Task> getTasks();

    List<Epic> getEpics();

    List<SubTask> getSubTasks();

    Task getTask(int id) throws NotFoundException;

    void update(Task task) throws NotFoundException;

    void update(Epic epic) throws NotFoundException;

    void update(SubTask subTask) throws NotFoundException;

    void deleteTask(int id) throws NotFoundException;

    List<SubTask> getEpicSubTasks(int id) throws NotFoundException;
}