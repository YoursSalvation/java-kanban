package Manager;

import Task.*;
import java.util.List;
import java.util.Stack;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history;
    private final int size;

    public InMemoryHistoryManager() {
        size = 10;
        history = new Stack<>();
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        if (history.size() == size) {
            history.removeFirst();
            history.addLast(task);
        } else {
            history.addLast(task);
        }
    }
}