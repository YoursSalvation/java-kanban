import manager.*;
import manager.exception.ManagerLoadException;
import task.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TaskManager fileBackedTaskManager;
        Path path = Paths.get("tasks.csv");
        try {
            fileBackedTaskManager = FileBackedTaskManager.loadFromFile(path.toFile());
        } catch (ManagerLoadException e) {
            System.out.println(e.getMessage());
            fileBackedTaskManager = new FileBackedTaskManager(path);
            System.out.println("Создан пустой менеджер");
        }
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

            switch (command) {
                case 1:
                    System.out.println("Введите тип, задачи которого вы хотите получить \n" +
                            "Типы задач - Epic, Task, SubTask");
                    type = scanner.next();
                    switch (type) {
                        case "Task":
                            System.out.println(fileBackedTaskManager.getTasks());
                            break;
                        case "Epic":
                            System.out.println(fileBackedTaskManager.getEpics());
                            break;
                        case "SubTask":
                            System.out.println(fileBackedTaskManager.getSubTasks());
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
                            fileBackedTaskManager.deleteAllTasks();
                            break;
                        case "Epic":
                            fileBackedTaskManager.deleteAllEpics();
                            break;
                        case "SubTask":
                            fileBackedTaskManager.deleteAllSubTasks();
                            break;
                        case null, default:
                            System.out.println("Указан неверный тип задач");
                            break;
                    }
                    break;
                case 3:
                    System.out.println("Введите id задачи, которую хотите получить");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println(fileBackedTaskManager.getTask(id));
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
                                fileBackedTaskManager.create(new Task(title, description, 0, status));
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
                                fileBackedTaskManager.create(new Epic(new Task(title, description, 0, status)));
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
                                if (!title.isEmpty()) {
                                    fileBackedTaskManager.create(new SubTask(new Task(title, description,
                                            0, status), epicId));
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
                                fileBackedTaskManager.update(new Task(title, description, id, status));
                            } else {
                                System.out.println("Название не может быть пустым");
                            }
                            break;
                        case "Epic":
                            System.out.println("Введите через 'Enter': новые название и описание");
                            title = scanner.nextLine();
                            description = scanner.nextLine();
                            if (!title.isEmpty()) {
                                fileBackedTaskManager.update(new Epic(new Task(title, description, id,
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
                            scanner.nextLine();
                            if (!isValidStatus(tempStatus)) {
                                System.out.println("Введен неверный статус задачи");
                            } else {
                                status = Status.valueOf(tempStatus);
                                if (!title.isEmpty()) {
                                    fileBackedTaskManager.update(new SubTask(new Task(title, description, id,
                                            status), 0));
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
                    System.out.println("Введите id задачи, которую необходимо удалить");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    fileBackedTaskManager.deleteTask(id);
                    break;
                case 7:
                    System.out.println("Введите id эпика подзадачи, которого хотите получить");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    if (fileBackedTaskManager.getEpicSubTasks(id) != null) {
                        System.out.println(fileBackedTaskManager.getEpicSubTasks(id));
                    } else {
                        System.out.println("Такого эпика не существует");
                    }
                    break;
                case 8:
                    System.out.println(fileBackedTaskManager.getHistory());
                    break;
                case 0:
                    isWorking = false;
                    break;
                default:
                    System.out.println("Такой команды нет");
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
        System.out.println("8. Посмотреть историю просмотров");
        System.out.println("0. Выход");
    }

    public static boolean isValidStatus(String status) {
        return status.equals("NEW") ||
                status.equals("IN_PROGRESS") ||
                status.equals("DONE");
    }
}