package task;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    @Test
    void TasksMustEqualsById() {
        Task excepted = new Task("excepted", "excepted", 1, Status.NEW, Duration.ofMinutes(120)
                , LocalDateTime.parse("21.12.2025, 16:30", formatter));
        Task actual = new Task("actual", "actual", 1, Status.DONE, Duration.ofMinutes(90)
                , LocalDateTime.parse("27.11.2025, 12:30", formatter));
        assertEquals(excepted, actual);
    }

    @Test
    void EpicsMustEqualsById() {
        Epic excepted = new Epic(new Task("excepted", "excepted", 1, Status.NEW));
        Epic actual = new Epic(new Task("actual", "actual", 1, Status.IN_PROGRESS));
        assertEquals(excepted, actual);
    }

    @Test
    void SubTasksMustEqualsById() {
        SubTask excepted = new SubTask(new Task("excepted", "excepted", 1, Status.NEW
                , Duration.ofMinutes(45), LocalDateTime.parse("15.05.2025, 23:00", formatter)), 1);
        SubTask actual = new SubTask(new Task("actual", "actual", 1, Status.DONE
                , Duration.ofMinutes(15), LocalDateTime.parse("12.08.2025, 09:45", formatter)), 2);
        assertEquals(excepted, actual);
    }
}