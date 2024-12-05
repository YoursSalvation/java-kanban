package Task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    static SubTask subTask = new SubTask(new Task("excepted", "excepted", 1, Status.NEW), 2);

    @Test
    void SetSameIdForCurrIdAndEpicIdMustBeFalse() {
        assertFalse(subTask.setEpicId(1));
    }
}