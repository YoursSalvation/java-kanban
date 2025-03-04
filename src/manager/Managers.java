package manager;

import manager.exception.ManagerLoadException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Managers {

    public static TaskManager getDefault() {
        Path path = Paths.get("tasks.csv");
        try {
            return FileBackedTaskManager.loadFromFile(path.toFile());
        } catch (ManagerLoadException e) {
            System.out.println(e.getMessage());
            return new FileBackedTaskManager(path);
        }
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
