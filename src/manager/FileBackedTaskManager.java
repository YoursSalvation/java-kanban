package manager;

import manager.exception.ManagerLoadException;
import manager.exception.ManagerSaveException;
import manager.exception.ManagerTaskCrossingException;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.*;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path path;

    public FileBackedTaskManager(Path path) {
        super();
        this.path = path;
    }

    public FileBackedTaskManager(Path path, HashMap<Integer, Task> tasks) {
        super(tasks);
        this.path = path;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerLoadException {
        HashMap<Integer, Task> tasks = new HashMap<>();
        ArrayList<SubTask> subTasks = new ArrayList<>();
        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
            String line = br.readLine(); // Для пропуска первой строки
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
            return new FileBackedTaskManager(file.toPath(), tasks);
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка при восстановлении данных из файла.");
        }
    }

    @Override
    public void create(Task task) throws ManagerTaskCrossingException {
        super.create(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void create(Epic epic) {
        super.create(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void create(SubTask subTask) throws ManagerTaskCrossingException {
        super.create(subTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Task task) {
        super.update(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Epic epic) {
        super.update(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(SubTask subTask) {
        super.update(subTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    private void save() throws ManagerSaveException {
        try (FileWriter fw = new FileWriter(path.toFile()); BufferedWriter bw = new BufferedWriter(fw)) {
            HashMap<Integer, Task> tasks = super.getMap();
            bw.write("id,type,title,status,description,duration,startTime,epic");
            bw.newLine();
            for (Task task : tasks.values()) {
                bw.write(toString(task));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ManagerSaveException("Возникла ошибка при попытке сохранения файла.");
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
}