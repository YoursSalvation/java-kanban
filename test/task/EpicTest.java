package task;

import org.junit.jupiter.api.BeforeAll;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    static Epic epic = new Epic(new Task("Epic1", "description1", 1, Status.NEW));
    static HashMap<Integer, SubTask> subTasks = new HashMap<>();
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    @BeforeAll
    static void CreateSubTasks() {
        SubTask subTask1 = new SubTask(new Task("SubTask1", "description1", 2, Status.NEW
                , Duration.ofMinutes(120), LocalDateTime.parse("12.12.2025, 12:00", formatter)), 1);
        SubTask subTask2 = new SubTask(new Task("SubTask2", "description2", 3, Status.NEW
                , Duration.ofMinutes(12), LocalDateTime.parse("21.12.2025, 10:45")), 1);
        subTasks.put(subTask1.getId(), subTask1);
        subTasks.put(subTask2.getId(), subTask2);
        epic.setSubTasks(subTasks);
        assertEquals(subTasks, epic.getSubTasks());
    }
}