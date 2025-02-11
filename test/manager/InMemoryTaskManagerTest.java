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
import java.util.Comparator;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    protected InMemoryTaskManagerTest() {
        super(new InMemoryTaskManager());
    }

    @BeforeEach
    protected void work_space_to_default() {
        super.work_space_to_default();
    }

    @Test
    void check_epic_status_correct_work() {
        taskManager.create(new Epic(new Task("Epic3", "Epic3", 10, Status.DONE)));
        assertEquals(Status.NEW, taskManager.getTask(10).getStatus());
        try {
            taskManager.create(new SubTask(new Task("SubTask6", "SubTask6", 11, Status.DONE
                    , Duration.ofMinutes(10), LocalDateTime.parse("01.01.2000, 11:00", formatter)), 10));
            taskManager.create(new SubTask(new Task("SubTask7", "SubTask7", 12, Status.NEW
                    , Duration.ofMinutes(10), LocalDateTime.parse("01.01.2000, 12:00", formatter)), 10));
        } catch (ManagerTaskCrossingException e) {
            System.out.println(e.getMessage());
        }
        assertEquals(Status.IN_PROGRESS, taskManager.getTask(10).getStatus());
        SubTask updateSubTask = (SubTask) taskManager.getTask(12);
        updateSubTask.setStatus(Status.DONE);
        taskManager.update(updateSubTask);
        assertEquals(Status.DONE, taskManager.getTask(10).getStatus());
        taskManager.deleteTask(12);
        assertEquals(Status.DONE, taskManager.getTask(10).getStatus());
        taskManager.deleteAllSubTasks();
        System.out.println(taskManager.getSubTasks());
        assertEquals(Status.NEW, taskManager.getTask(10).getStatus());
    }

    @Test
    void check_prioritized_tasks_treeSet() {
        TreeSet<Task> excepted = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        excepted.add(new Task("Task1", "Task1", 0, Status.NEW, Duration.ofMinutes(10)
                , LocalDateTime.parse("06.03.2025, 10:00", formatter)));
        excepted.add(new Task("Task2", "Task2", 1, Status.DONE, Duration.ofMinutes(10)
                , LocalDateTime.parse("07.03.2025, 10:00", formatter)));
        excepted.add(new Task("Task3", "Task3", 2, Status.IN_PROGRESS
                , Duration.ofMinutes(10), LocalDateTime.parse("08.03.2025, 10:00", formatter)));
        excepted.add(new SubTask(new Task("SubTask1", "SubTask1", 5, Status.NEW
                , Duration.ofMinutes(10), LocalDateTime.parse("09.03.2025, 10:00", formatter)), 3));
        excepted.add(new SubTask(new Task("SubTask2", "SubTask2", 6, Status.NEW
                , Duration.ofMinutes(10), LocalDateTime.parse("10.03.2025, 10:00", formatter)), 3));
        excepted.add(new SubTask(new Task("SubTask3", "SubTask3", 7, Status.NEW
                , Duration.ofMinutes(10), LocalDateTime.parse("11.03.2025, 10:00", formatter)), 4));
        excepted.add(new SubTask(new Task("SubTask4", "SubTask4", 8, Status.NEW
                , Duration.ofMinutes(10), LocalDateTime.parse("12.03.2025, 10:00", formatter)), 4));
        excepted.add(new SubTask(new Task("SubTask5", "SubTask5", 9, Status.NEW
                , Duration.ofMinutes(10), LocalDateTime.parse("13.03.2025, 10:00", formatter)), 4));
        assertEquals(excepted, taskManager.getPrioritizedTasks());
    }

    @Test
    void check_isCrossing_correct_work() {
        assertThrows(ManagerTaskCrossingException.class, () -> {
            taskManager.create(new Task("Task1", "Task1", 0, Status.NEW, Duration.ofMinutes(10)
                    , LocalDateTime.parse("06.03.2025, 10:00", formatter)));
        });
    }
}