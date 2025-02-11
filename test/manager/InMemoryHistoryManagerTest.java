package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    static InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    @BeforeEach
    void to_default() {
        inMemoryHistoryManager.clear();
    }

    @Test
    void add_and_get_history() {
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
    void remove_node() {
        inMemoryHistoryManager.add(new Task("1", "1", 1, Status.NEW));
        inMemoryHistoryManager.add(new Task("1", "1", 2, Status.NEW));
        inMemoryHistoryManager.add(new Task("1", "1", 3, Status.NEW));
        inMemoryHistoryManager.remove(1);
        assertEquals(inMemoryHistoryManager.head.task.getId(), 2);
        inMemoryHistoryManager.add(new Task("1", "1", 4, Status.NEW));
        inMemoryHistoryManager.remove(4);
        assertEquals(inMemoryHistoryManager.tail.task.getId(), 3);
    }

    @Test
    void empty_history() {
        assertEquals(0, inMemoryHistoryManager.getSize());
    }

    @Test
    void duplication() {
        inMemoryHistoryManager.add(new Task("1", "1", 0, Status.NEW));
        assertEquals(1, inMemoryHistoryManager.getSize());
        inMemoryHistoryManager.add(new Task("1", "1", 0, Status.NEW));
        inMemoryHistoryManager.add(new Task("1", "1", 0, Status.NEW));
        inMemoryHistoryManager.add(new Task("1", "1", 0, Status.NEW));
        assertEquals(1, inMemoryHistoryManager.getSize());
    }

    @Test
    void remove_first_mid_last() {
        inMemoryHistoryManager.add(new Task("1", "1", 0, Status.NEW));
        inMemoryHistoryManager.add(new Task("2", "2", 1, Status.NEW));
        inMemoryHistoryManager.add(new Task("3", "3", 2, Status.NEW));
        inMemoryHistoryManager.add(new Task("4", "4", 3, Status.NEW));
        inMemoryHistoryManager.add(new Task("5", "5", 4, Status.NEW));
        inMemoryHistoryManager.add(new Task("6", "6", 5, Status.NEW));
        inMemoryHistoryManager.remove(0);
        assertEquals(1, inMemoryHistoryManager.head.task.getId());
        inMemoryHistoryManager.remove(5);
        assertEquals(4, inMemoryHistoryManager.tail.task.getId());
        inMemoryHistoryManager.remove(3);
        assertEquals(2, inMemoryHistoryManager.tail.prev.task.getId());
    }
}