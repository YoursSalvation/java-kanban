package manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
class FileBackedTaskManagerTest {
    static FileBackedTaskManager inMemoryTaskManager;

    @Test
    void saveAndLoad() {
        try {
            inMemoryTaskManager = new FileBackedTaskManager(File.createTempFile("temp", ".csv").toPath());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        inMemoryTaskManager.create(new Task("Task1", "Task1", 0, Status.NEW));
        inMemoryTaskManager.create(new Task("Task2", "Task2", 1, Status.DONE));
        inMemoryTaskManager.create(new Task("Task3", "Task3", 2, Status.IN_PROGRESS));
        inMemoryTaskManager.create(new Epic(new Task("Epic1", "Epic1", 3, Status.DONE)));
        inMemoryTaskManager.create(new Epic(new Task("Epic2", "Epic2", 4, Status.NEW)));
        inMemoryTaskManager.create(new SubTask(new Task("SubTask1", "SubTask1", 5, Status.NEW), 3));
        inMemoryTaskManager.create(new SubTask(new Task("SubTask2", "SubTask2", 6, Status.NEW), 3));
        inMemoryTaskManager.create(new SubTask(new Task("SubTask3", "SubTask3", 7, Status.NEW), 4));
        inMemoryTaskManager.create(new SubTask(new Task("SubTask4", "SubTask4", 8, Status.NEW), 4));
        inMemoryTaskManager.create(new SubTask(new Task("SubTask5", "SubTask5", 9, Status.NEW), 4));
        try {
            File file = File.createTempFile("temp", ".csv");
            try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)) {
                for (Task task : inMemoryTaskManager.getMap().values()) {
                    bw.write(toString(task));
                    System.out.println(toString(task));
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            HashMap<Integer, Task> tasks = new HashMap<>();
            ArrayList<SubTask> subTasks = new ArrayList<>();
            try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
                String line;
                while ((line = br.readLine()) != null) {
                    Task task = fromString(line);
                    if (task != null) {
                        if (task instanceof SubTask subTask) {
                            subTasks.add(subTask);
                        } else if (task instanceof Epic epic) {
                            tasks.put(epic.getId(), epic);
                        } else {
                            tasks.put(task.getId(), task);
                        }
                    }
                }
                tasks = loadSubTasks(subTasks, tasks);
                System.out.println(tasks);
            } catch (FileNotFoundException e) {
                System.out.println("ошибка");
            } catch (IOException e) {
                System.out.println("ошибка");
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String toString(Task task) {
        if (task instanceof SubTask subTask) {
            return task.getId() + "," + TypeTask.SUBTASK + "," + task.getTitle() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + subTask.getEpicId();
        } else if (task instanceof Epic) {
            return task.getId() + "," + TypeTask.EPIC + "," + task.getTitle() + "," + task.getStatus() + ","
                    + task.getDescription();
        }
        return task.getId() + "," + TypeTask.TASK + "," + task.getTitle() + "," + task.getStatus() + ","
                + task.getDescription();
    }

    private static Task fromString(String str) {
        if (str.isBlank()) return null;
        try {
            String[] info = str.split(",");
            String title = info[2];
            String description = info[4];
            int id = Integer.parseInt(info[0]);
            Status status = Status.valueOf(info[3]);
            TypeTask type = TypeTask.valueOf(info[1]);
            if (type.equals(TypeTask.SUBTASK)) {
                return new SubTask(new Task(title, description, id, status), Integer.parseInt(info[5]));
            } else if (type.equals(TypeTask.EPIC)) {
                return new Epic(new Task(title, description, id, status));
            }
            return new Task(title, description, id, status);
        } catch (Exception e) {
            System.out.println("Произошла ошибка во время преобразования строки в Task.");
            return null;
        }
    }

    private static HashMap<Integer, Task> loadSubTasks(ArrayList<SubTask> subTasks, HashMap<Integer, Task> tasks) {
        for (SubTask subTask : subTasks) {
            tasks.put(subTask.getId(), subTask);
            Epic epic = (Epic) tasks.get(subTask.getEpicId());
            HashMap<Integer, SubTask> updateSubTasks = epic.getSubTasks();
            updateSubTasks.put(subTask.getId(), subTask);
            epic.setSubTasks(updateSubTasks);
            tasks.put(epic.getId(), epic);
        }
        return tasks;
    }
}