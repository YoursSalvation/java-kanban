package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void TasksMustEqualsById() {
        Task excepted = new Task("excepted", "excepted", 1, Status.NEW);
        Task actual = new Task("actual", "actual", 1, Status.DONE);
        assertEquals(excepted, actual);
    }

    @Test
    void EpicsMustEqualsById() {
        Epic excepted = new Epic( new Task("excepted", "excepted", 1, Status.NEW));
        Epic actual = new Epic( new Task("actual", "actual", 1, Status.IN_PROGRESS));
        assertEquals(excepted, actual);
    }

    @Test
    void SubTasksMustEqualsById() {
        SubTask excepted = new SubTask(new Task("excepted", "excepted", 1, Status.NEW), 1);
        SubTask actual = new SubTask(new Task("actual", "actual", 1, Status.DONE), 2);
        assertEquals(excepted, actual);
    }
}