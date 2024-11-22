import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    protected int taskId;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;

    public TaskManager() {
        taskId = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subTasks = new HashMap<>();
    }

    public void createTask(Task task) {
        task.setId(idIncrement());
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(idIncrement());
        epics.put(epic.getId(), epic);
    }

    public void createSubTask(SubTask subTask) {
        subTask.setId(idIncrement());
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        HashMap<Integer, SubTask> updateSubTasks = epic.getSubTasks();
        updateSubTasks.put(subTask.getId(), subTask);
        epic.setSubTasks(updateSubTasks);
        controlEpicStatus(epic);
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic value : epics.values()) {
            value.getSubTasks().clear();
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
        if (!tasks.containsKey(task.getId())) return;
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) return;
        controlEpicStatus(epic); // т.к. метод controlEpicStatus, ложит проверенный эпик в hashmap я решил, что
        // этого действия будет достаточно т.к он выполнит и проверку и обновление
    }

    public void updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId())) return;
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        HashMap<Integer, SubTask> updateSubTasks = epic.getSubTasks();
        updateSubTasks.put(subTask.getId(), subTask);
        controlEpicStatus(epic);
    }

    public void deleteTask(int id) {
        if (!tasks.containsKey(id)) return;
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        if (!epics.containsKey(id)) return;
        Epic epic = getEpic(id);

        if (epic.getSubTasks() == null) {
            epics.remove(id);
            return;
        }
        HashMap<Integer, SubTask> subTasksToDel = getEpic(id).getSubTasks();

        for (Integer key : subTasksToDel.keySet()) {
            subTasks.remove(key);
        }
        epics.remove(id);
    }

    public void deleteSubTask(int id) {
        if (!subTasks.containsKey(id)) return;
        SubTask subTask = getSubTask(id);
        Epic epic = getEpic(subTask.getEpicId());
        HashMap<Integer, SubTask> newSubTasks = epic.getSubTasks();

        newSubTasks.remove(id);
        epic.setSubTasks(newSubTasks);
        controlEpicStatus(epic);
        subTasks.remove(id);
    }

    public ArrayList<SubTask> getEpicSubTasks(int id) {
        if (getEpic(id) == null) return null;
        Epic epic = getEpic(id);
        HashMap<Integer, SubTask> epicSubTasks = epic.getSubTasks();

        return new ArrayList<>(epicSubTasks.values());
    }

    private int idIncrement() {
        taskId += 1;
        return taskId;
    }

    private void controlEpicStatus(Epic epic) {
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
}