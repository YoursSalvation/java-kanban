package manager;

import manager.exception.ManagerTaskCrossingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

abstract class TaskManagerTest<T extends TaskManager> {
    protected static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
    protected T taskManager;

    protected TaskManagerTest(T taskManager) {
        this.taskManager = taskManager;
    }

    @BeforeEach
    protected void work_space_to_default() {
        taskManager.deleteAllSubTasks();
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        try {
            taskManager.create(new Task("Task1", "Task1", 0, Status.NEW, Duration.ofMinutes(10)
                    , LocalDateTime.parse("06.03.2025, 10:00", formatter)));
            taskManager.create(new Task("Task2", "Task2", 1, Status.DONE, Duration.ofMinutes(10)
                    , LocalDateTime.parse("07.03.2025, 10:00", formatter)));
            taskManager.create(new Task("Task3", "Task3", 2, Status.IN_PROGRESS
                    , Duration.ofMinutes(10), LocalDateTime.parse("08.03.2025, 10:00", formatter)));
            taskManager.create(new Epic(new Task("Epic1", "Epic1", 3, Status.DONE)));
            taskManager.create(new Epic(new Task("Epic2", "Epic2", 4, Status.NEW)));
            taskManager.create(new SubTask(new Task("SubTask1", "SubTask1", 5, Status.NEW
                    , Duration.ofMinutes(10), LocalDateTime.parse("09.03.2025, 10:00", formatter)), 3));
            taskManager.create(new SubTask(new Task("SubTask2", "SubTask2", 6, Status.NEW
                    , Duration.ofMinutes(10), LocalDateTime.parse("10.03.2025, 10:00", formatter)), 3));
            taskManager.create(new SubTask(new Task("SubTask3", "SubTask3", 7, Status.NEW
                    , Duration.ofMinutes(10), LocalDateTime.parse("11.03.2025, 10:00", formatter)), 4));
            taskManager.create(new SubTask(new Task("SubTask4", "SubTask4", 8, Status.NEW
                    , Duration.ofMinutes(10), LocalDateTime.parse("12.03.2025, 10:00", formatter)), 4));
            taskManager.create(new SubTask(new Task("SubTask5", "SubTask5", 9, Status.NEW
                    , Duration.ofMinutes(10), LocalDateTime.parse("13.03.2025, 10:00", formatter)), 4));
        } catch (ManagerTaskCrossingException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void delete_all_tasks() {
        taskManager.deleteAllTasks();
        ArrayList<Task> excepted = new ArrayList<>();
        assertEquals(excepted, taskManager.getTasks());
    }

    @Test
    void delete_all_epics() {
        taskManager.deleteAllEpics();
        ArrayList<Epic> exceptedEpics = new ArrayList<>();
        ArrayList<SubTask> exceptedSubTasks = new ArrayList<>();
        assertEquals(exceptedEpics, taskManager.getEpics());
        assertEquals(exceptedSubTasks, taskManager.getSubTasks());
    }

    @Test
    void delete_all_subTasks() {
        taskManager.deleteAllSubTasks();
        ArrayList<SubTask> excepted = new ArrayList<>();
        assertEquals(excepted, taskManager.getSubTasks());
    }

    @Test
    void get_all_tasks() {
        ArrayList<Task> excepted = new ArrayList<>();
        Task task = new Task("Task1", "Task1", 0, Status.NEW, Duration.ofMinutes(120)
                , LocalDateTime.parse("06.03.2025, 10:00", formatter));
        excepted.add(task);
        task = new Task("Task2", "Task2", 1, Status.DONE, Duration.ofMinutes(180)
                , LocalDateTime.parse("07.03.2025, 12:00", formatter));
        excepted.add(task);
        task = new Task("Task3", "Task3", 2, Status.IN_PROGRESS, Duration.ofMinutes(30)
                , LocalDateTime.parse("08.03.2025, 17:30", formatter));
        excepted.add(task);
        assertEquals(excepted, taskManager.getTasks());
    }

    @Test
    void get_all_epics() {
        ArrayList<Epic> excepted = new ArrayList<>();
        Epic epic = new Epic(new Task("Epic1", "Epic1", 3, Status.NEW));
        excepted.add(epic);
        epic = new Epic(new Task("Epic2", "Epic2", 4, Status.NEW));
        excepted.add(epic);
        assertEquals(excepted, taskManager.getEpics());
    }

    @Test
    void get_all_subTasks() {
        ArrayList<SubTask> excepted = new ArrayList<>();
        SubTask subTask = new SubTask(new Task("SubTask1", "SubTask1", 5, Status.NEW
                , Duration.ofMinutes(45), LocalDateTime.parse("09.03.2025, 12:00", formatter)), 3);
        excepted.add(subTask);
        subTask = new SubTask(new Task("SubTask2", "SubTask2", 6, Status.NEW
                , Duration.ofMinutes(55), LocalDateTime.parse("10.03.2025, 11:00", formatter)), 3);
        excepted.add(subTask);
        subTask = new SubTask(new Task("SubTask3", "SubTask3", 7, Status.NEW
                , Duration.ofMinutes(90), LocalDateTime.parse("11.03.2025, 15:30", formatter)), 4);
        excepted.add(subTask);
        subTask = new SubTask(new Task("SubTask4", "SubTask4", 8, Status.NEW
                , Duration.ofMinutes(60), LocalDateTime.parse("12.03.2025, 16:00", formatter)), 4);
        excepted.add(subTask);
        subTask = new SubTask(new Task("SubTask5", "SubTask5", 9, Status.NEW
                , Duration.ofMinutes(300), LocalDateTime.parse("13.03.2025, 10:00", formatter)), 4);
        excepted.add(subTask);
        assertEquals(excepted, taskManager.getSubTasks());
    }

    @Test
    void get_task() {
        SubTask subTask = new SubTask(new Task("SubTask1", "SubTask1", 5, Status.NEW
                , Duration.ofMinutes(45), LocalDateTime.parse("09.03.2025, 12:00", formatter)), 3);
        assertEquals(subTask, taskManager.getTask(5));
    }

    @Test
    void update() {
        Task excepted = new Task("777", "777", 0, Status.NEW, Duration.ofMinutes(120)
                , LocalDateTime.parse("11.11.1111, 11:11", formatter));
        taskManager.update(excepted);
        Task actual = taskManager.getTask(0);
        assertEquals(excepted.getTitle(), actual.getTitle());
        assertEquals(excepted.getDescription(), actual.getDescription());
        assertEquals(excepted.getId(), actual.getId());
        assertEquals(excepted.getStatus(), actual.getStatus());
        assertEquals(excepted, actual);
    }

    @Test
    void delete_task() {
        taskManager.deleteTask(1);
        assertNull(taskManager.getTask(1));
    }

    @Test
    void get_epic_subTasks() {
        ArrayList<SubTask> excepted = new ArrayList<>();
        excepted.add(new SubTask(new Task("SubTask1", "SubTask1", 5, Status.NEW
                , Duration.ofMinutes(45), LocalDateTime.parse("09.03.2025, 12:00", formatter)), 3));
        excepted.add(new SubTask(new Task("SubTask2", "SubTask2", 6, Status.NEW
                , Duration.ofMinutes(55), LocalDateTime.parse("10.03.2025, 11:00", formatter)), 3));
        assertEquals(excepted, taskManager.getEpicSubTasks(3));
    }

    @Test
    void check_invalid_subTask_in_epic() {
        Epic epic = (Epic) taskManager.getTask(4);
        assertEquals(3, epic.getSubTasks().size());
        taskManager.deleteTask(8);
        assertEquals(2, epic.getSubTasks().size());
    }

    @Test
    void should_not_create_subTask_without_epic() {
        try {
            taskManager.create(new SubTask(new Task("SubTask1", "SubTask1", 10, Status.NEW
                    , Duration.ofMinutes(45), LocalDateTime.parse("09.03.2025, 12:00", formatter)), 20));
        } catch (ManagerTaskCrossingException e) {
            System.out.println(e.getMessage());
        }
        assertNull(taskManager.getTask(10));
    }
}