package Manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManagersTest {

    @Test
    void GetDefault() {
        InMemoryTaskManager excepted = new InMemoryTaskManager();
        TaskManager actual = Managers.getDefault();
        assertEquals(excepted.getClass(), actual.getClass());
    }

    @Test
    void GetDefaultHistory() {
        InMemoryHistoryManager excepted = new InMemoryHistoryManager();
        HistoryManager actual = Managers.getDefaultHistory();
        assertEquals(actual.getClass(), excepted.getClass());
    }
}