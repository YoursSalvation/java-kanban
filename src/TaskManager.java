import java.util.HashMap;

public class TaskManager {
    protected int taskId;
    HashMap<Integer, Object> task;
    HashMap<String, HashMap<Integer, Object>> tasks;

    public TaskManager() {
        taskId = 1;
        task = new HashMap<>();
        tasks = new HashMap<>();
    }

    public void createTask(Object object) {
        String type = object.getClass().getName();
        switch (type) {
            case "Epic":
                Epic epic = (Epic) object;
                if (epic.title == null) return;
                epic.id = taskId;
                taskId += 1;
                if (tasks.containsKey(type)) {
                    task = tasks.get(type);
                    task.put(epic.id, epic);
                    tasks.put(type, task);
                } else {
                    task = new HashMap<>();
                    task.put(epic.id, epic);
                    tasks.put(type, task);
                }
                System.out.println("Epic задача создана");
                break;
            case "Task":
                Task newTask = (Task) object;
                newTask.id = taskId;
                if (newTask.title == null) return;
                taskId += 1;
                if (tasks.containsKey(type)) {
                    task = tasks.get(type);
                    task.put(newTask.id, newTask);
                    tasks.put(type, task);
                } else {
                    task = new HashMap<>();
                    task.put(newTask.id, newTask);
                    tasks.put(type, task);
                }
                System.out.println("Task задача создана");
                break;
            case "SubTask":
                SubTask subTask = (SubTask) object;
                if (subTask.title == null) return;
                subTask.id = taskId;
                taskId += 1;
                if (tasks.containsKey(type)) {
                    task = tasks.get(type);
                    task.put(subTask.id, subTask);
                    tasks.put(type, task);
                    Epic updatingEpic = (Epic) tasks.get("Epic").get(subTask.epicId);
                    updatingEpic.subTasks.put(subTask.id, subTask);
                    updatingEpic.controlEpicStatus();
                    task = tasks.get("Epic");
                    task.put(updatingEpic.id, updatingEpic);
                    tasks.put("Epic", task);
                } else {
                    task = new HashMap<>();
                    task.put(subTask.id, subTask);
                    tasks.put(type, task);
                    Epic updatingEpic = (Epic) tasks.get("Epic").get(subTask.epicId);
                    updatingEpic.subTasks.put(subTask.id, subTask);
                    updatingEpic.controlEpicStatus();
                    task = tasks.get("Epic");
                    task.put(updatingEpic.id, updatingEpic);
                    tasks.put("Epic", task);
                }
                System.out.println("SubTask задача создана");
                break;
        }
    }

    public Status getStatus(int id) {
        Object object = getTask(id);
        Status status = null;
        String type = object.getClass().getName();
        switch (type) {
            case "Epic":
                Epic epic = (Epic) object;
                status = epic.status;
                break;
            case "Task":
                Task task = (Task) object;
                status = task.status;
                break;
            case "SubTask":
                SubTask subTask = (SubTask) object;
                status = subTask.status;
                break;
        }
        return status;
    }

    public void setStatus(Status status, Object object) {
        String type = object.getClass().getName();
        switch (type) {
            case "Epic":
                System.out.println("Статус Epic задачи поменять нлеьзя");
                break;
            case "Task":
                Task newTask = (Task) object;
                newTask.status = status;
                task = tasks.get("Task");
                task.put(newTask.id, newTask);
                tasks.put("Task", task);
                break;
            case "SubTask":
                SubTask subTask = (SubTask) object;
                subTask.status = status;
                task = tasks.get("SubTask");
                task.put(subTask.id, subTask);
                tasks.put("SubTask", task);
                Epic updatingEpic = (Epic) tasks.get("Epic").get(subTask.epicId);
                updatingEpic.controlEpicStatus();
                task = tasks.get("Epic");
                task.put(updatingEpic.id, updatingEpic);
                tasks.put("Epic", task);
                break;
        }
    }

    public HashMap<Integer, Object> getCurrentTasks(String type) {
        if (!tasks.containsKey(type)) {
            return null;
        }
        return tasks.get(type);
    }

    public void deleteAllCurrentTasks(String type) {
        if (type == null) {
            System.out.println("Вы не указали тип задач, которые необходимо удалить");
            return;
        }
        if (!tasks.containsKey(type)) {
            System.out.println("Указаный тип задач не существует");
            return;
        }

        switch (type) {
            case "Task":
                taskId -= tasks.get(type).size();
                tasks.remove(type);
                System.out.println("Задачи типа " + type + " удалены");
                break;
            case "Epic":
                taskId -= tasks.get(type).size() + tasks.get("SubTask").size();
                tasks.remove(type);
                tasks.remove("SubTask");
                System.out.println("Задачи типа " + type + " и все их подзадачи удалены");
                break;
            case "SubTask":
                taskId -= tasks.get(type).size();
                tasks.remove(type);
                HashMap<Integer, Object> tempEpic = tasks.get("Epic");
                for (Object value : tempEpic.values()) {
                    Epic val = (Epic) value;
                    val.subTasks.clear();
                    val.controlEpicStatus();
                    tempEpic.put(val.id, val);
                }
                tasks.put("Epic", tempEpic);
                System.out.println("Задачи типа " + type + " удалены");
                break;
            default:
                System.out.println("Указан неверный тип задач");
                break;
        }
    }

    public Object getTask(int id) {
        for (HashMap<Integer, Object> value : tasks.values()) {
            if (value.containsKey(id)) return value.get(id);
        }
        return null;
    }

    public boolean chekEpic(int id) {
        if (!tasks.containsKey("Epic")) return false;
        return tasks.get("Epic").containsKey(id);
    }

    public void updateTask(Object object, int id) {

        for (String type : tasks.keySet()) {
            if (tasks.get(type).containsKey(id)) {
                switch (type) {
                    case "Epic":
                        Epic newEpic = (Epic) object;
                        Epic updatingEpic = (Epic) tasks.get(type).get(id);
                        if (!newEpic.title.isEmpty()) updatingEpic.title = newEpic.title;
                        if (!newEpic.description.isEmpty()) updatingEpic.description = newEpic.description;
                        task = tasks.get("Epic");
                        task.put(id, updatingEpic);
                        tasks.put("Epic", task);
                        System.out.println("Новые данные задачи:");
                        System.out.println(tasks.get(type).get(id));
                        break;
                    case "Task":
                        Task newTask = (Task) object;
                        Task updatingTask = (Task) tasks.get(type).get(id);
                        if (!newTask.title.isEmpty()) updatingTask.title = newTask.title;
                        if (!newTask.description.isEmpty()) updatingTask.description = newTask.description;
                        if (newTask.status != null) updatingTask.status = newTask.status;
                        task = tasks.get("Task");
                        task.put(id, updatingTask);
                        tasks.put("Task", task);
                        System.out.println("Новые данные задачи:");
                        System.out.println(tasks.get(type).get(id));
                        break;
                    case "SubTask":
                        SubTask newSubTask = (SubTask) object;
                        SubTask updatingSubTask = (SubTask) tasks.get(type).get(id);
                        if (!newSubTask.title.isEmpty()) updatingSubTask.title = newSubTask.title;
                        if (!newSubTask.description.isEmpty()) updatingSubTask.description = newSubTask.description;
                        if (newSubTask.status != null) {
                            updatingSubTask.status = newSubTask.status;
                            Epic epic = (Epic) tasks.get("Epic").get(updatingSubTask.epicId);
                            epic.subTasks.put(updatingSubTask.id, updatingSubTask);
                            epic.controlEpicStatus();
                            task = tasks.get("Epic");
                            task.put(epic.id, epic);
                            tasks.put("Epic", task);
                        }
                        task = tasks.get("SubTask");
                        task.put(id, updatingSubTask);
                        tasks.put("SubTask", task);
                        System.out.println("Новые данные задачи:");
                        System.out.println(tasks.get(type).get(id));
                        break;
                    case null, default:
                        System.out.println("Что-то пошло не так");
                        break;
                }
            }
        }
    }

    public void deleteTask(int id) {
        if (getTask(id) == null) {
            System.out.println("Задача с таким id не существует");
            return;
        }
        for (HashMap<Integer, Object> value : tasks.values()) {
            if (value.containsKey(id)) {
                if (value.get(id).getClass().getName().equals("Epic")) {
                    Epic epic = (Epic) value.get(id);
                    HashMap<Integer, SubTask> subTasksToRemove = epic.getSubTasks();

                    for (Integer i : subTasksToRemove.keySet()) {
                        task = tasks.get("SubTasks");
                        task.remove(i);
                        tasks.put("SubTasks", task);
                        taskId -= 1;
                    }
                    task = tasks.get("Epic");
                    task.remove(epic.id);
                    tasks.put("Epic", task);
                    taskId -= 1;
                    System.out.println("Epic " + id + " и всего его SubTask удалены");
                } else if (value.get(id).getClass().getName().equals("SubTask")) {
                    SubTask subTask = (SubTask) value.get(id);
                    Epic epicToUpdate = (Epic) tasks.get("Epic").get(subTask.epicId);
                    HashMap<Integer, SubTask> newSubTasks = epicToUpdate.getSubTasks();

                    newSubTasks.remove(subTask.id);
                    epicToUpdate.subTasks = newSubTasks;
                    epicToUpdate.controlEpicStatus();
                    task = tasks.get("Epic");
                    task.put(epicToUpdate.id, epicToUpdate);
                    tasks.put("Epic", task);

                    task = tasks.get("SubTask");
                    task.remove(id);
                    tasks.put("SubTask", task);
                    taskId -= 1;
                    System.out.println("SubTask " + id + " удалена");
                } else {
                    task = tasks.get("Task");
                    task.remove(id);
                    tasks.put("Task", task);
                    taskId -= 1;
                    System.out.println("Task " + id + " успешно удалена");
                }
            }
        }
    }

    public HashMap<Integer, SubTask> getEpicSubTasks(int id) {
        if (!chekEpic(id)) return null;
        Epic epic = (Epic) tasks.get("Epic").get(id);
        return epic.getSubTasks();
    }
}