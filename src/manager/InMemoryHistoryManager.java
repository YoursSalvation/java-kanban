package manager;

import task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history;
    public Node head;
    public Node tail;
    private int size;


    public InMemoryHistoryManager() {
        history = new HashMap<>();
        size = 0;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node node = head;
        while (node != null) {
            tasks.add(node.task);
            node = node.next;
        }
        return tasks;
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        if (history.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
        history.put(task.getId(), tail);
        size++;
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        if (node == null) return;
        removeNode(node);
        history.remove(id);
        size--;
    }

    public int getSize() {
        return size;
    }

    private void linkLast(Task task) {
        if (task == null) return;
        Node oldTail = tail;
        Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    private void removeNode(Node node) {
        if (node == null) return;
        if (node.task == head.task && node.task == tail.task) {
            head = null;
            tail = null;
        } else if (node.task == head.task) {
            head = head.next;
            head.prev = null;
        } else if (node.task == tail.task) {
            tail = tail.prev;
            tail.next = null;
        } else if (node.prev != null && node.next != null) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }
}