package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManagersTest {

    @Test
    void get_default() {
        TaskManager actual = Managers.getDefault();
        assertEquals(FileBackedTaskManager.class, actual.getClass());
    }

    @Test
    void get_default_history() {
        InMemoryHistoryManager excepted = new InMemoryHistoryManager();
        HistoryManager actual = Managers.getDefaultHistory();
        assertEquals(actual.getClass(), excepted.getClass());
    }
}