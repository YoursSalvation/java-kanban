package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InMemoryTaskManagerTest {
    static InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    void WorkSpaceToDefault() {
        inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.create(new Task("Task1", "Task1", 1, Status.NEW));
        inMemoryTaskManager.create(new Task("Task2", "Task2", 2, Status.DONE));
        inMemoryTaskManager.create(new Task("Task3", "Task3", 3, Status.IN_PROGRESS));
        inMemoryTaskManager.create(new Epic(new Task("Epic1", "Epic1", 4, Status.DONE)));
        inMemoryTaskManager.create(new Epic(new Task("Epic2", "Epic2", 5, Status.NEW)));
        inMemoryTaskManager.create(new SubTask(new Task("SubTask1", "SubTask1", 6, Status.NEW), 4));
        inMemoryTaskManager.create(new SubTask(new Task("SubTask2", "SubTask2", 7, Status.NEW), 4));
        inMemoryTaskManager.create(new SubTask(new Task("SubTask3", "SubTask3", 8, Status.NEW), 5));
        inMemoryTaskManager.create(new SubTask(new Task("SubTask4", "SubTask4", 9, Status.NEW), 5));
        inMemoryTaskManager.create(new SubTask(new Task("SubTask5", "SubTask5", 10, Status.NEW), 5));
    }

    @Test
    void CheckEpicStatusCorrectWork() {
        inMemoryTaskManager.create(new Epic(new Task("Epic3", "Epic3", 11, Status.DONE)));
        Epic epic = (Epic) inMemoryTaskManager.getTask(11);
        assertEquals(epic.getStatus(), Status.NEW);
        inMemoryTaskManager.create(new SubTask(new Task("SubTask6", "SubTask6", 12, Status.DONE), 11));
        assertEquals(epic.getStatus(), Status.DONE);
        inMemoryTaskManager.create(new SubTask(new Task("SubTask7", "SubTask7", 13, Status.NEW), 11));
        assertEquals(epic.getStatus(), Status.IN_PROGRESS);
        SubTask updateSubTask = (SubTask) inMemoryTaskManager.getTask(13);
        updateSubTask.setStatus(Status.DONE);
        inMemoryTaskManager.update(updateSubTask);
        assertEquals(epic.getStatus(), Status.DONE);
        inMemoryTaskManager.deleteTask(13);
        assertEquals(epic.getStatus(), Status.DONE);
        inMemoryTaskManager.deleteAllSubTasks();
        assertEquals(epic.getStatus(), Status.NEW);
    }

    @Test
    void DeleteAllTasks() {
        inMemoryTaskManager.deleteAllTasks();
        ArrayList<Task> excepted = new ArrayList<>();
        assertEquals(excepted, inMemoryTaskManager.getTasks());
    }

    @Test
    void DeleteAllEpics() {
        inMemoryTaskManager.deleteAllEpics();
        ArrayList<Epic> exceptedEpics = new ArrayList<>();
        ArrayList<SubTask> exceptedSubTasks = new ArrayList<>();
        assertEquals(exceptedEpics, inMemoryTaskManager.getEpics());
        assertEquals(exceptedSubTasks, inMemoryTaskManager.getSubTasks());
    }

    @Test
    void DeleteAllSubTasks() {
        inMemoryTaskManager.deleteAllSubTasks();
        ArrayList<SubTask> excepted = new ArrayList<>();
        assertEquals(excepted, inMemoryTaskManager.getSubTasks());
    }

    @Test
    void GetAllTasks() {
        ArrayList<Task> excepted = new ArrayList<>();
        Task task = new Task("Task1", "Task1", 1, Status.NEW);
        excepted.add(task);
        task = new Task("Task2", "Task2", 2, Status.DONE);
        excepted.add(task);
        task = new Task("Task3", "Task3", 3, Status.IN_PROGRESS);
        excepted.add(task);
        assertEquals(excepted, inMemoryTaskManager.getTasks());
    }

    @Test
    void GetAllEpics() {
        ArrayList<Epic> excepted = new ArrayList<>();
        Epic epic = new Epic(new Task("Epic1", "Epic1", 4, Status.NEW));
        excepted.add(epic);
        epic = new Epic(new Task("Epic2", "Epic2", 5, Status.NEW));
        excepted.add(epic);
        assertEquals(excepted, inMemoryTaskManager.getEpics());
    }

    @Test
    void GetAllSubTasks() {
        ArrayList<SubTask> excepted = new ArrayList<>();
        SubTask subTask = new SubTask(new Task("SubTask1", "SubTask1", 6, Status.NEW), 4);
        excepted.add(subTask);
        subTask = new SubTask(new Task("SubTask2", "SubTask2", 7, Status.NEW), 4);
        excepted.add(subTask);
        subTask = new SubTask(new Task("SubTask3", "SubTask3", 8, Status.NEW), 5);
        excepted.add(subTask);
        subTask = new SubTask(new Task("SubTask4", "SubTask4", 9, Status.NEW), 5);
        excepted.add(subTask);
        subTask = new SubTask(new Task("SubTask5", "SubTask5", 10, Status.NEW), 5);
        excepted.add(subTask);
        assertEquals(excepted, inMemoryTaskManager.getSubTasks());
    }

    @Test
    void GetTask() {
        SubTask subTask = new SubTask(new Task("SubTask1", "SubTask1", 6, Status.NEW), 4);
        assertEquals(subTask, inMemoryTaskManager.getTask(6));
    }

    @Test
    void Update() {
        Task excepted = new Task("777", "777", 2, Status.NEW);
        inMemoryTaskManager.update(excepted);
        Task actual = inMemoryTaskManager.getTask(2);
        assertEquals(excepted.getTitle(), actual.getTitle());
        assertEquals(excepted.getDescription(), actual.getDescription());
        assertEquals(excepted.getId(), actual.getId());
        assertEquals(excepted.getStatus(), actual.getStatus());
        assertEquals(excepted, actual);
    }

    @Test
    void DeleteTask() {
        inMemoryTaskManager.deleteTask(1);
        assertNull(inMemoryTaskManager.getTask(1));
    }

    @Test
    void GetEpicSubTasks() {
        ArrayList<SubTask> excepted = new ArrayList<>();
        excepted.add(new SubTask(new Task("SubTask1", "SubTask1", 6, Status.NEW), 4));
        excepted.add(new SubTask(new Task("SubTask2", "SubTask2", 7, Status.NEW), 4));
        assertEquals(excepted, inMemoryTaskManager.getEpicSubTasks(4));
    }

    @Test
    void CheckInvalidSubTaskInEpic() {
        Epic epic = (Epic) inMemoryTaskManager.getTask(5);
        assertEquals(3, epic.getSubTasks().size());
        inMemoryTaskManager.deleteTask(9);
        assertEquals(2, epic.getSubTasks().size());
    }
}