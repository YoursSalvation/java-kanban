package task;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    static Epic epic = new Epic(new Task("Epic1", "description1", 1, Status.NEW));
    static HashMap<Integer, SubTask> subTasks = new HashMap<>();

    @BeforeAll
    static void CreateSubTasks() {
        SubTask subTask1 = new SubTask(new Task("SubTask1", "description1", 2, Status.NEW) ,1);
        SubTask subTask2 = new SubTask(new Task("SubTask2", "description2", 3, Status.NEW) ,1);
        subTasks.put(subTask1.getId(), subTask1);
        subTasks.put(subTask2.getId(), subTask2);
        epic.setSubTasks(subTasks);
    }

    @Test
    void GetterAndSetter() {
        HashMap<Integer, SubTask> excepted = epic.getSubTasks();
        assertEquals(excepted, epic.getSubTasks());
        excepted.clear();
        epic.setSubTasks(excepted);
        assertEquals(excepted, epic.getSubTasks());
    }
}