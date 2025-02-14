package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManagersTest {

    @Test
    void get_default() {
        InMemoryTaskManager excepted = new InMemoryTaskManager();
        TaskManager actual = Managers.getDefault();
        assertEquals(excepted.getClass(), actual.getClass());
    }

    @Test
    void get_default_history() {
        InMemoryHistoryManager excepted = new InMemoryHistoryManager();
        HistoryManager actual = Managers.getDefaultHistory();
        assertEquals(actual.getClass(), excepted.getClass());
    }
}