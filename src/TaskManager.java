import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    protected int taskId;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;

    public TaskManager() {
        taskId = 1;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    private void idIncrement() {
        taskId += 1;
    }

    public void createTask(Task task) {
        tasks.put(taskId, task);
        idIncrement();
    }

    public void createEpic(Epic epic) {
        epics.put(taskId, epic);
        idIncrement();
    }

    public void createSubTask(SubTask subTask) {
        subTasks.put(taskId, subTask);
        idIncrement();
        Epic epic = epics.get(subTask.getEpicId());
        HashMap<Integer, SubTask> updateSubTasks = epic.getSubTasks();
        updateSubTasks.put(subTask.getId(), subTask);
        epic.setSubTasks(updateSubTasks);
        controlEpicStatus(epic);
    }

    public void controlEpicStatus(Epic epic) {
        int doneCount = 0;
        int newCount = 0;
        int inProgressCount = 0;

        for (SubTask value : epic.getSubTasks().values()) {
            if (value.getStatus().equals(Status.DONE)) doneCount += 1;
            else if (value.getStatus().equals(Status.NEW)) newCount += 1;
            else if (value.getStatus().equals(Status.IN_PROGRESS)) inProgressCount += 1;
        }
        if (newCount == 0 && inProgressCount == 0 && doneCount > 0) epic.setStatus(Status.DONE);
        else if (doneCount == 0 && inProgressCount == 0) epic.setStatus(Status.NEW);
        else epic.setStatus(Status.IN_PROGRESS);
        epics.put(epic.getId(), epic);
    }

    public void deleteAllTasks() {
        taskId -= tasks.size();
        tasks.clear();
    }

    public void deleteAllEpics() {
        taskId -= epics.size() + subTasks.size();
        epics.clear();
        subTasks.clear();
    }

    public void deleteAllSubTasks() {
        taskId -= subTasks.size();
        subTasks.clear();
        HashMap<Integer, SubTask> newSubTasks = new HashMap<>();
        for (Epic value : epics.values()) {
            value.setSubTasks(newSubTasks);
            controlEpicStatus(value);
            epics.put(value.getId(), value);
        }
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public Task getTask(int id) {
        if (!tasks.containsKey(id)) return null;
        return tasks.get(id);
    }

    public SubTask getSubTask(int id) {
        if (!subTasks.containsKey(id)) return null;
        return subTasks.get(id);
    }

    public Epic getEpic(int id) {
        if (!epics.containsKey(id)) return null;
        return epics.get(id);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        controlEpicStatus(epic); // т.к. метод controlEpicStatus, ложит проверенный эпик в hashmap я решил, что
        // этого действия будет достаточно т.к он выполнит и проверку и обновление
    }

    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        HashMap<Integer, SubTask> updateSubTasks = epic.getSubTasks();
        updateSubTasks.put(subTask.getId(), subTask);
        controlEpicStatus(epic);
    }

    public void deleteTask(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Задачи с id" + id + " не существует");
            return;
        }
        taskId -= 1;
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Эпика с id " + id + " не существует");
            return;
        }
        Epic epic = getEpic(id);

        if (epic.getSubTasks() == null) {
            taskId -= 1;
            epics.remove(id);
            return;
        }
        HashMap<Integer, SubTask> subTasksToDel = getEpic(id).getSubTasks();

        for (Integer key : subTasksToDel.keySet()) {
            taskId -= 1;
            subTasks.remove(key);
        }
        taskId -= 1;
        epics.remove(id);
    }

    public void deleteSubTask(int id) {
        if (!subTasks.containsKey(id)) {
            System.out.println("Подзадачи с id " + id + " не существует");
            return;
        }
        SubTask subTask = getSubTask(id);
        Epic epic = getEpic(subTask.getEpicId());
        HashMap<Integer, SubTask> newSubTasks = epic.getSubTasks();

        newSubTasks.remove(id);
        epic.setSubTasks(newSubTasks);
        controlEpicStatus(epic);
        taskId -= 1;
        subTasks.remove(id);
    }

    public ArrayList<SubTask> getEpicSubTasks(int id) {
        Epic epic = getEpic(id);
        HashMap<Integer, SubTask> epicSubTasks = epic.getSubTasks();

        return new ArrayList<>(epicSubTasks.values());
    }
}