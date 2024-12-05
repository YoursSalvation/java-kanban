package Manager;

import Task.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    static InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    @Test
    void AddAndGetHistory() {
        Task excepted = new Task("1", "1", 1, Status.NEW);
        inMemoryHistoryManager.add(new Task("1", "1", 1, Status.NEW));
        assertEquals(1, inMemoryHistoryManager.getHistory().size());
        Task actual = inMemoryHistoryManager.getHistory().getFirst();
        assertEquals(excepted, actual);
    }
}