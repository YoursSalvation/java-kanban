package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager actual = Managers.getDefault();
        assertEquals(FileBackedTaskManager.class, actual.getClass());
    }

    @Test
    void getDefaultHistory() {
        InMemoryHistoryManager excepted = new InMemoryHistoryManager();
        HistoryManager actual = Managers.getDefaultHistory();
        assertEquals(actual.getClass(), excepted.getClass());
    }
}