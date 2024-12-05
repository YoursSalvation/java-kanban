package Manager;

import Task.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected int taskId;
    private final HashMap<Integer, Task> tasks;
    private final HistoryManager inMemoryHistoryManager;

    public InMemoryTaskManager() {
        inMemoryHistoryManager = Managers.getDefaultHistory();
        taskId = 0;
        tasks = new HashMap<>();
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    @Override
    public void create(Task task) {
        task.setId(idIncrement());
        tasks.put(task.getId(), task);
    }

    @Override
    public void create(Epic epic) {
        epic.setId(idIncrement());
        controlEpicStatus(epic);
    }

    @Override
    public void create(SubTask subTask) {
        if (!(tasks.get(subTask.getEpicId()) instanceof Epic)) return;
        subTask.setId(idIncrement());
        tasks.put(subTask.getId(), subTask);
        Epic epic = (Epic) tasks.get(subTask.getEpicId());
        HashMap<Integer, SubTask> updateSubTasks = epic.getSubTasks();
        updateSubTasks.put(subTask.getId(), subTask);
        epic.setSubTasks(updateSubTasks);
        controlEpicStatus(epic);
    }

    @Override
    public void deleteAllTasks() {
        ArrayList<Integer> idToRemove = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (!(task instanceof Epic) && !(task instanceof SubTask)) {
                idToRemove.add(task.getId());
            }
        }
        for (Integer i : idToRemove) {
            tasks.remove(i);
        }
    }

    @Override
    public void deleteAllEpics() {
        ArrayList<Integer> idToRemove = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Epic) {
                idToRemove.add(task.getId());
            }
        }
        for (Task task : tasks.values()) {
            if (task instanceof SubTask) {
                idToRemove.add(task.getId());
            }
        }
        for (Integer i : idToRemove) {
            tasks.remove(i);
        }
    }

    @Override
    public void deleteAllSubTasks() {
        ArrayList<Integer> idToRemove = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof SubTask) {
                idToRemove.add(task.getId());
            }
        }
        for (Task task : tasks.values()) {
            if (task instanceof Epic) {
                Epic epic = (Epic) task;
                HashMap<Integer, SubTask> emptySubTasks = new HashMap<>();
                epic.setSubTasks(emptySubTasks);
                controlEpicStatus(epic);
            }
        }
        for (Integer i : idToRemove) {
            tasks.remove(i);
        }
    }

    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (!(task instanceof Epic) && !(task instanceof SubTask)) {
                tasksList.add(task);
            }
        }
        return tasksList;
    }

    @Override
    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicsList = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Epic) {
                epicsList.add((Epic) task);
            }
        }
        return epicsList;
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        ArrayList<SubTask> subTasksList = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof SubTask) {
                subTasksList.add((SubTask) task);
            }
        }
        return subTasksList;
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
        tasks.put(task.getId(), task);
    }

    @Override
    public void update(Epic epic) {
        if (!tasks.containsKey(epic.getId())) return;
        if (!(tasks.get(epic.getId()) instanceof Epic)) return;
        Epic tempEpic = (Epic) tasks.get(epic.getId());
        epic.setSubTasks(tempEpic.getSubTasks());
        controlEpicStatus(epic);
    }

    @Override
    public void update(SubTask subTask) {
        if (!tasks.containsKey(subTask.getId())) return;
        if (!(tasks.get(subTask.getId()) instanceof SubTask)) return;
        SubTask oldSubTask = (SubTask) tasks.get(subTask.getId());
        int epicId = oldSubTask.getEpicId();
        subTask.setEpicId(epicId);
        tasks.put(subTask.getId(), subTask);
        Epic epic = (Epic) tasks.get(epicId);
        HashMap<Integer, SubTask> updateSubTasks = epic.getSubTasks();
        updateSubTasks.put(subTask.getId(), subTask);
        controlEpicStatus(epic);
    }

    @Override
    public void deleteTask(int id) {
        if (!tasks.containsKey(id)) return;
        if (tasks.get(id) instanceof Epic) {
            Epic epic = (Epic) tasks.get(id);
            HashMap<Integer, SubTask> subTasksToDel = epic.getSubTasks();
            for (Integer idSubTask : subTasksToDel.keySet()) {
                tasks.remove(idSubTask);
            }
        } else if (tasks.get(id) instanceof SubTask) {
            SubTask subTask = (SubTask) tasks.get(id);
            Epic epic = (Epic) tasks.get(subTask.getEpicId());
            HashMap<Integer, SubTask> updateSubTasks = epic.getSubTasks();
            updateSubTasks.remove(id);
            epic.setSubTasks(updateSubTasks);
            controlEpicStatus(epic);
        }
        tasks.remove(id);
    }

    @Override
    public ArrayList<SubTask> getEpicSubTasks(int id) {
        if (!(tasks.get(id) instanceof Epic)) return null;
        Epic epic = (Epic) tasks.get(id);
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
        tasks.put(epic.getId(), epic);
    }
}