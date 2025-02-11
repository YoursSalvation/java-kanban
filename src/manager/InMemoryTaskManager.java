package manager;

import manager.exception.ManagerTaskCrossingException;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int taskId;
    private final HashMap<Integer, Task> tasks;
    private final HistoryManager inMemoryHistoryManager;
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public InMemoryTaskManager() {
        inMemoryHistoryManager = Managers.getDefaultHistory();
        taskId = 0;
        tasks = new HashMap<>();
    }

    public InMemoryTaskManager(HashMap<Integer, Task> tasks) {
        inMemoryHistoryManager = Managers.getDefaultHistory();
        this.tasks = tasks;
        prioritizedTasks.addAll(tasks.values());
        taskId = idIncrement();
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    @Override
    public void create(Task task) throws ManagerTaskCrossingException {
        if (isCrossing(task)) throw new ManagerTaskCrossingException("Задача пересекает по времени выполнения " +
                "одну или несколько задач");
        task.setId(idIncrement());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void create(Epic epic) {
        epic.setId(idIncrement());
        controlEpicStatus(epic);
    }

    @Override
    public void create(SubTask subTask) throws ManagerTaskCrossingException {
        if (!(tasks.get(subTask.getEpicId()) instanceof Epic epic)) return;
        if (isCrossing(subTask)) throw new ManagerTaskCrossingException("Задача пересекает по времени выполнения " +
                "одну или несколько задач");
        subTask.setId(idIncrement());
        tasks.put(subTask.getId(), subTask);
        prioritizedTasks.add(subTask);
        HashMap<Integer, SubTask> updateSubTasks = epic.getSubTasks();
        updateSubTasks.put(subTask.getId(), subTask);
        epic.setSubTasks(updateSubTasks);
        controlEpicStatus(epic);
    }

    @Override
    public void deleteAllTasks() {
        ArrayList<Integer> idToRemove = tasks.values().stream()
                .filter(task -> !(task instanceof Epic) && !(task instanceof SubTask))
                .map(Task::getId)
                .collect(Collectors.toCollection(ArrayList::new));
        for (Integer i : idToRemove) {
            prioritizedTasks.remove(tasks.get(i));
            tasks.remove(i);
            inMemoryHistoryManager.remove(i);
        }
    }

    @Override
    public void deleteAllEpics() {
        ArrayList<Integer> idToRemove = tasks.values().stream()
                .filter(task -> (task instanceof Epic) || (task instanceof SubTask))
                .map(Task::getId)
                .collect(Collectors.toCollection(ArrayList::new));
        for (Integer i : idToRemove) {
            prioritizedTasks.remove(tasks.get(i));
            tasks.remove(i);
            inMemoryHistoryManager.remove(i);
        }
    }

    @Override
    public void deleteAllSubTasks() {
        ArrayList<Integer> idToRemove = tasks.values().stream()
                .filter(task -> task instanceof SubTask)
                .map(Task::getId)
                .collect(Collectors.toCollection(ArrayList::new));
        for (Task task : tasks.values()) {
            if (task instanceof Epic epic) {
                HashMap<Integer, SubTask> emptySubTasks = new HashMap<>();
                epic.setSubTasks(emptySubTasks);
                controlEpicStatus(epic);
            }
        }
        for (Integer i : idToRemove) {
            prioritizedTasks.remove(tasks.get(i));
            tasks.remove(i);
            inMemoryHistoryManager.remove(i);
        }
    }

    @Override
    public ArrayList<Task> getTasks() {
        return tasks.values().stream()
                .filter(task -> !(task instanceof Epic) && !(task instanceof SubTask))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return tasks.values().stream()
                .filter(task -> task instanceof Epic)
                .map(task -> (Epic) task)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return tasks.values().stream()
                .filter(task -> task instanceof SubTask)
                .map(task -> (SubTask) task)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Task getTask(int id) {
        if (!tasks.containsKey(id)) return null;
        inMemoryHistoryManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void update(Task task) {
        if (!tasks.containsKey(task.getId())) return;
        if (tasks.get(task.getId()) instanceof Epic || tasks.get(task.getId()) instanceof SubTask) return;
        Task oldTask = tasks.get(task.getId());
        task.setDuration(oldTask.getDuration());
        task.setStartTime(oldTask.getStartTime());
        tasks.put(task.getId(), task);
        prioritizedTasks.remove(oldTask);
        prioritizedTasks.add(task);
    }

    @Override
    public void update(Epic epic) {
        if (!tasks.containsKey(epic.getId())) return;
        if (!(tasks.get(epic.getId()) instanceof Epic oldEpic)) return;
        epic.setSubTasks(oldEpic.getSubTasks());
        controlEpicStatus(epic);
    }

    @Override
    public void update(SubTask subTask) {
        if (!tasks.containsKey(subTask.getId())) return;
        if (!(tasks.get(subTask.getId()) instanceof SubTask oldSubTask)) return;
        int epicId = oldSubTask.getEpicId();
        subTask.setEpicId(epicId);
        subTask.setDuration(oldSubTask.getDuration());
        subTask.setStartTime(oldSubTask.getStartTime());
        tasks.put(subTask.getId(), subTask);
        prioritizedTasks.remove(oldSubTask);
        prioritizedTasks.add(subTask);
        Epic epic = (Epic) tasks.get(epicId);
        HashMap<Integer, SubTask> updateSubTasks = epic.getSubTasks();
        updateSubTasks.put(subTask.getId(), subTask);
        epic.setSubTasks(updateSubTasks);
        controlEpicStatus(epic);
    }

    @Override
    public void deleteTask(int id) {
        if (!tasks.containsKey(id)) return;
        if (tasks.get(id) instanceof Epic epic) {
            HashMap<Integer, SubTask> subTasksToDel = epic.getSubTasks();
            for (Integer idSubTask : subTasksToDel.keySet()) {
                prioritizedTasks.remove(tasks.get(idSubTask));
                tasks.remove(idSubTask);
                inMemoryHistoryManager.remove(idSubTask);
            }
        } else if (tasks.get(id) instanceof SubTask subTask) {
            Epic epic = (Epic) tasks.get(subTask.getEpicId());
            HashMap<Integer, SubTask> updateSubTasks = epic.getSubTasks();
            updateSubTasks.remove(id);
            epic.setSubTasks(updateSubTasks);
            controlEpicStatus(epic);
        }
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        inMemoryHistoryManager.remove(id);
    }

    @Override
    public ArrayList<SubTask> getEpicSubTasks(int id) {
        if (!(tasks.get(id) instanceof Epic epic)) return null;
        HashMap<Integer, SubTask> epicSubTasks = epic.getSubTasks();
        return new ArrayList<>(epicSubTasks.values());
    }

    protected HashMap<Integer, Task> getMap() {
        return tasks;
    }

    private boolean isCrossing(Task task) {
        return tasks.values().stream()
                .anyMatch(checkingTask ->
                        (task.getStartTime().isBefore(checkingTask.getStartTime()) &&
                                task.getEndTime().isAfter(checkingTask.getStartTime())) ||
                                (task.getStartTime().isAfter(checkingTask.getStartTime()) &&
                                        task.getStartTime().isBefore(checkingTask.getEndTime())) ||
                                (task.getStartTime().isEqual(checkingTask.getStartTime()) ||
                                        task.getEndTime().isEqual(checkingTask.getEndTime())));
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
        tasks.put(epic.getId(), epic);
    }

    private int idIncrement() {
        taskId = 0;
        while (tasks.containsKey(taskId)) taskId++;
        return taskId;
    }
}