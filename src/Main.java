import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Scanner scanner = new Scanner(System.in);
        boolean isWorking = true;

        while (isWorking) {
            printMenu();
            int command = scanner.nextInt();
            String type;
            int id;
            String title;
            String description;
            String tempStatus;
            Status status;

            if (command < 0 || command > 8) return;

            switch (command) {
                case 1:
                    System.out.println("Введите тип, задачи которого вы хотите получить \n" +
                            "Типы задач - Epic, Task, SubTask");
                    type = scanner.next();
                    switch (type) {
                        case "Task":
                            System.out.println(taskManager.getTasks());
                            break;
                        case "Epic":
                            System.out.println(taskManager.getEpics());
                            break;
                        case "SubTask":
                            System.out.println(taskManager.getSubTasks());
                            break;
                        case null, default:
                            System.out.println("Указан неверный тип задач");
                            break;
                    }
                    break;
                case 2:
                    System.out.println("Введите тип, задачи которого вы хотите удалить \n" +
                            "Типы задач - Epic, Task, SubTask \n" +
                            "Удаляя задачи типа 'Epic' вы так же удаляете все задачи типа 'SubTask'");
                    type = scanner.next();
                    switch (type) {
                        case "Task":
                            taskManager.deleteAllTasks();
                            break;
                        case "Epic":
                            taskManager.deleteAllEpics();
                            break;
                        case "SubTask":
                            taskManager.deleteAllSubTasks();
                            break;
                        case null, default:
                            System.out.println("Указан неверный тип задач");
                            break;
                    }
                    break;
                case 3:
                    System.out.println("Введите тип задачи, которую хотите получить");
                    type = scanner.next();
                    System.out.println("Введите id задачи, которую хотите получить");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    switch (type) {
                        case "Task":
                            Task task = taskManager.getTask(id);
                            if (task != null) {
                                System.out.println(task);
                            } else {
                                System.out.println("Задача с таким id не найдена");
                            }
                            break;
                        case "Epic":
                            Epic epic = taskManager.getEpic(id);
                            if (epic != null) {
                                System.out.println(epic);
                            } else {
                                System.out.println("Epic с таким id не найден");
                            }
                            break;
                        case "SubTask":
                            SubTask subTask = taskManager.getSubTask(id);
                            if (subTask != null) {
                                System.out.println(subTask);
                            } else {
                                System.out.println("SubTask с таким id не найден");
                            }
                            break;
                        case null, default:
                            System.out.println("Указан неверный тип задачи");
                    }
                    break;
                case 4:
                    System.out.println("Введите тип задачи, которую хотите создать \n" +
                            "Типы задач - Epic, Task, SubTask");
                    type = scanner.next();
                    scanner.nextLine();
                    switch (type) {
                        case "Task":
                            System.out.println("Введите через 'Enter': название, описание и статус задачи \n" +
                                    "Возможные статусы задачи: NEW, IN_PROGRESS, DONE");
                            title = scanner.nextLine();
                            description = scanner.nextLine();
                            tempStatus = scanner.next();
                            scanner.nextLine();
                            if (!isValidStatus(tempStatus)) {
                                System.out.println("Введен неверный статус задачи");
                            } else if (!title.isEmpty()) {
                                status = Status.valueOf(tempStatus);
                                taskManager.createTask(new Task(title, description, taskManager.taskId, status));
                            } else {
                                System.out.println("Название не может быть пустым");
                            }
                            break;
                        case "Epic":
                            System.out.println("Введите через 'Enter': название и описание");
                            title = scanner.nextLine();
                            description = scanner.nextLine();
                            status = Status.NEW;
                            if (!title.isEmpty()) {
                                taskManager.createEpic(new Epic(new Task(title, description, taskManager.taskId,
                                        status)));
                            } else {
                                System.out.println("Название не может быть пустым");
                            }
                            break;
                        case "SubTask":
                            System.out.println("Введите через 'Enter': название, описание, " +
                                    "статус задачи и id Epic задачи \n" +
                                    "Возможные статусы задачи: NEW, IN_PROGRESS, DONE");
                            title = scanner.nextLine();
                            description = scanner.nextLine();
                            tempStatus = scanner.next();
                            int epicId = scanner.nextInt();
                            scanner.nextLine();
                            if (!isValidStatus(tempStatus)) {
                                System.out.println("Введен неверный статус задачи");
                            } else {
                                status = Status.valueOf(tempStatus);
                                if (taskManager.getEpic(epicId) == null) {
                                    System.out.println("Epic задачи с таким id не существует");
                                } else if (!title.isEmpty()) {
                                    taskManager.createSubTask(new SubTask(new Task(title, description, taskManager.taskId,
                                            status), epicId));
                                } else {
                                    System.out.println("Название не может быть пустым");
                                }
                            }
                            break;
                        case null, default:
                            System.out.println("Указан неверный тип задачи");
                            break;
                    }
                    break;
                case 5:
                    System.out.println("Введите тип задачи, которую вы хотите обновить");
                    type = scanner.next();
                    System.out.println("Введите id задачи, которую вы хотите обновить");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    switch (type) {
                        case "Task":
                            System.out.println("Введите через 'Enter': новые название, описание и статус задачи \n" +
                                    "Возможные статусы задачи: NEW, IN_PROGRESS, DONE");
                            title = scanner.nextLine();
                            description = scanner.nextLine();
                            tempStatus = scanner.next();
                            scanner.nextLine();
                            if (!isValidStatus(tempStatus)) {
                                System.out.println("Введен неверный статус задачи");
                            } else if (!title.isEmpty()) {
                                status = Status.valueOf(tempStatus);
                                taskManager.updateTask(new Task(title, description, id, status));
                            } else {
                                System.out.println("Название не может быть пустым");
                            }
                            break;
                        case "Epic":
                            System.out.println("Введите через 'Enter': новые название и описание");
                            title = scanner.nextLine();
                            description = scanner.nextLine();
                            if (!title.isEmpty()) {
                                taskManager.updateEpic(new Epic(new Task(title, description, id,
                                        null)));
                            } else {
                                System.out.println("Название не может быть пустым");
                            }
                            break;
                        case "SubTask":
                            System.out.println("Введите через 'Enter': новые название, описание, " +
                                    "статус задачи\n" +
                                    "Возможные статусы задачи: NEW, IN_PROGRESS, DONE");
                            title = scanner.nextLine();
                            description = scanner.nextLine();
                            tempStatus = scanner.next();
                            int epicId = taskManager.getSubTask(id).getEpicId();
                            scanner.nextLine();
                            if (!isValidStatus(tempStatus)) {
                                System.out.println("Введен неверный статус задачи");
                            } else {
                                status = Status.valueOf(tempStatus);
                                if (!title.isEmpty()) {
                                    taskManager.updateSubTask(new SubTask(new Task(title, description, id,
                                            status), epicId));
                                } else {
                                    System.out.println("Название не может быть пустым");
                                }
                            }
                            break;
                        case null, default:
                            System.out.println("Указан неверный тип задачи");
                            break;
                    }
                    break;
                case 6:
                    System.out.println("Введите тип задачи, которую необходимо удалить");
                    type = scanner.next();
                    System.out.println("Введите id задачи, которую необходимо удалить");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    switch (type) {
                        case "Task":
                            taskManager.deleteTask(id);
                            break;
                        case "Epic":
                            taskManager.deleteEpic(id);
                            break;
                        case "SubTask":
                            taskManager.deleteSubTask(id);
                            break;
                        case null, default:
                            System.out.println("Указан неверный тип задачи");
                            break;
                    }
                    break;
                case 7:
                    System.out.println("Введите id эпика подзадачи, которого хотите получить");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    if (taskManager.getEpicSubTasks(id) != null) {
                        System.out.println(taskManager.getEpicSubTasks(id));
                    } else {
                        System.out.println("Такого эпика не существует");
                    }
                    break;
                case 0:
                    isWorking = false;
                    break;
            }
        }
    }

    public static void printMenu() {
        System.out.println("Введите номер команды");
        System.out.println("1. Для получения списка всех задач выбранного типа");
        System.out.println("2. Удаление всех задач выбранного типа");
        System.out.println("3. Получение задачи по идентификатору");
        System.out.println("4. Создать задачу");
        System.out.println("5. Обновить задачу ");
        System.out.println("6. Удалить задачу по идентификатору");
        System.out.println("7. Получить список всех подзадач эпика");
        System.out.println("0. Выход");
    }

    public static boolean isValidStatus(String status) {
        return status.equals("NEW") ||
                status.equals("IN_PROGRESS") ||
                status.equals("DONE");
    }
}