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
                    if (taskManager.getCurrentTasks(type) == null) {
                        System.out.println("У этого типа пока нет задач или вы ввели неверный тип");
                    } else {
                        System.out.println(taskManager.getCurrentTasks(type));
                    }
                    break;
                case 2:
                    System.out.println("Введите тип, задачи которого вы хотите удалить \n" +
                            "Типы задач - Epic, Task, SubTask \n" +
                            "Удаляя задачи типа 'Epic' вы так же удаляете все задачи типа 'SubTask'");
                    type = scanner.next();
                    taskManager.deleteAllCurrentTasks(type);
                    break;
                case 3:
                    System.out.println("Введите id задачи, которую хотите получить");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    if (taskManager.getTask(id) == null) {
                        System.out.println("Введен неверный id");
                    } else {
                        System.out.println(taskManager.getTask(id));
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
                            } else if (title != null) {
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
                            if (title != null) {
                                taskManager.createTask(new Epic(new Task(title, description, taskManager.taskId,
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
                                if (!taskManager.chekEpic(epicId)) {
                                    System.out.println("Epic задачи с таким id не существует");
                                } else if (title != null) {
                                    taskManager.createTask(new SubTask(new Task(title, description, taskManager.taskId,
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
                    System.out.println("Введите id задачи, которую вы хотите обновить");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    Object tempTask = taskManager.getTask(id);
                    if (tempTask == null) {
                        System.out.println("Задача с таким id не существует");
                    } else {
                        System.out.println("Чтобы не менять значение нажмите Enter");
                        System.out.println("Введите новое название:");
                        title = scanner.nextLine();
                        System.out.println("Введите новое описание");
                        description = scanner.nextLine();
                        status = null;
                        if (taskManager.chekEpic(id)) {
                            System.out.println("Статус Epic задачи поменять нельзя");
                        } else {
                            System.out.println("Введите новый статус");
                            tempStatus = scanner.nextLine();
                            if (tempStatus != null) {
                                if (!isValidStatus(tempStatus)) {
                                    System.out.println("Введен неверный статус задачи");
                                } else {
                                    status = Status.valueOf(tempStatus);
                                }
                            }
                        }
                        switch (tempTask.getClass().getName()) {
                            case "Task":
                                taskManager.updateTask(new Task(title, description, id, status), id);
                            case "Epic":
                                taskManager.updateTask(new Epic(new Task(title, description, id, null)), id);
                            case "SubTask":
                                SubTask subTask = (SubTask) tempTask;
                                taskManager.updateTask(new SubTask(new Task(title, description, id, status),
                                        subTask.epicId), id);
                        }
                    }
                    break;
                case 6:
                    System.out.println("Введите id задачи, которую необходимо удалить");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    taskManager.deleteTask(id);
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
                case 8:
                    System.out.println("Введите id задачи статус, которой хотите поменять");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    if (taskManager.getTask(id) == null) {
                        System.out.println("Задача с таким id не существует");
                    } else if (taskManager.chekEpic(id)) {
                        System.out.println("Статус Epic задачи поменять нельзя");
                    } else {
                        System.out.println("Текущий статус задачи: " + taskManager.getStatus(id));
                        System.out.println("Введите новый статус задачи");
                        tempStatus = scanner.nextLine();
                        if (isValidStatus(tempStatus)) {
                            status = Status.valueOf(tempStatus);
                            taskManager.setStatus(status, taskManager.getTask(id));
                        }
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
        System.out.println("8. Изменить статус задачи");
        System.out.println("0. Выход");
    }

    public static boolean isValidStatus(String status) {
        return status.equals("NEW") ||
                status.equals("IN_PROGRESS") ||
                status.equals("DONE");
    }
}