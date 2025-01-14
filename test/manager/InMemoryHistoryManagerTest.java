package manager;

import task.*;
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
        assertEquals(inMemoryHistoryManager.head.task.getId(), 1);
        assertEquals(inMemoryHistoryManager.tail.task.getId(), 1);
        inMemoryHistoryManager.add(new Task("1", "1", 2, Status.NEW));
        assertEquals(inMemoryHistoryManager.tail.task.getId(), 2);
        assertEquals(2, inMemoryHistoryManager.getSize());
        assertEquals(2, inMemoryHistoryManager.getHistory().size());
    }

    @Test
    void removeNode() {
        inMemoryHistoryManager.add(new Task("1", "1", 1, Status.NEW));
        inMemoryHistoryManager.add(new Task("1", "1", 2, Status.NEW));
        inMemoryHistoryManager.add(new Task("1", "1", 3, Status.NEW));
        inMemoryHistoryManager.remove(1);
        assertEquals(inMemoryHistoryManager.head.task.getId(), 2);
        inMemoryHistoryManager.add(new Task("1", "1", 4, Status.NEW));
        inMemoryHistoryManager.remove(4);
        assertEquals(inMemoryHistoryManager.tail.task.getId(), 3);
    }
}