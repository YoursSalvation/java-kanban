package Manager;
import Task.*;

public class Node {
    public final Task task;
    public Node prev;
    public Node next;

    public Node(Node prev, Task task, Node next) {
        this.prev = prev;
        this.task = task;
        this.next = next;
    }

    public Task getTask() {
        return task;
    }
}