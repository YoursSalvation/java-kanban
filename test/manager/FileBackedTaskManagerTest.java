package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    protected FileBackedTaskManagerTest() throws IOException {
        super(new FileBackedTaskManager(File.createTempFile("temp", ".csv").toPath()));
    }

    @BeforeEach
    protected void work_space_to_default() {
        super.work_space_to_default();
    }

    @Test
    void save_and_load() {
        HashMap<Integer, Task> excepted = new HashMap<>();
        try {
            File file = File.createTempFile("temp", ".csv");
            try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw)) {
                for (Task task : taskManager.getMap().values()) {
                    bw.write(toString(task));
                    bw.newLine();
                }
                excepted = taskManager.getMap();
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
                assertEquals(excepted, tasks);
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
                    + task.getDescription() + "," + task.getDuration().toMinutes() + "," + task.getStartTime() + ","
                    + subTask.getEpicId();
        } else if (task instanceof Epic) {
            return task.getId() + "," + TypeTask.EPIC + "," + task.getTitle() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + task.getDuration().toMinutes() + "," + task.getStartTime();
        }
        return task.getId() + "," + TypeTask.TASK + "," + task.getTitle() + "," + task.getStatus() + ","
                + task.getDescription() + "," + task.getDuration().toMinutes() + "," + task.getStartTime();
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
            Duration duration = Duration.ofMinutes(Long.parseLong(info[5]));
            LocalDateTime startTime = LocalDateTime.parse(info[6]);
            if (type.equals(TypeTask.SUBTASK)) {
                return new SubTask(new Task(title, description, id, status, duration, startTime), Integer.parseInt(info[7]));
            } else if (type.equals(TypeTask.EPIC)) {
                return new Epic(new Task(title, description, id, status));
            }
            return new Task(title, description, id, status, duration, startTime);
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