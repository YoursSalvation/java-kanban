import manager.*;
import task.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();
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
                        case "task":
                            System.out.println(inMemoryTaskManager.getTasks());
                            break;
                        case "Epic":
                            System.out.println(inMemoryTaskManager.getEpics());
                            break;
                        case "SubTask":
                            System.out.println(inMemoryTaskManager.getSubTasks());
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
                        case "task":
                            inMemoryTaskManager.deleteAllTasks();
                            break;
                        case "Epic":
                            inMemoryTaskManager.deleteAllEpics();
                            break;
                        case "SubTask":
                            inMemoryTaskManager.deleteAllSubTasks();
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
                    System.out.println(inMemoryTaskManager.getTask(id));
                    break;
                case 4:
                    System.out.println("Введите тип задачи, которую хотите создать \n" +
                            "Типы задач - Epic, Task, SubTask");
                    type = scanner.next();
                    scanner.nextLine();
                    switch (type) {
                        case "task":
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
                                inMemoryTaskManager.create(new Task(title, description, 0, status));
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
                                inMemoryTaskManager.create(new Epic(new Task(title, description, 0, status)));
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
                                    inMemoryTaskManager.create(new SubTask(new Task(title, description,
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
                        case "task":
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
                                inMemoryTaskManager.update(new Task(title, description, id, status));
                            } else {
                                System.out.println("Название не может быть пустым");
                            }
                            break;
                        case "Epic":
                            System.out.println("Введите через 'Enter': новые название и описание");
                            title = scanner.nextLine();
                            description = scanner.nextLine();
                            if (!title.isEmpty()) {
                                inMemoryTaskManager.update(new Epic(new Task(title, description, id,
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
                                    inMemoryTaskManager.update(new SubTask(new Task(title, description, id,
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
                    inMemoryTaskManager.deleteTask(id);
                    break;
                case 7:
                    System.out.println("Введите id эпика подзадачи, которого хотите получить");
                    id = scanner.nextInt();
                    scanner.nextLine();
                    if (inMemoryTaskManager.getEpicSubTasks(id) != null) {
                        System.out.println(inMemoryTaskManager.getEpicSubTasks(id));
                    } else {
                        System.out.println("Такого эпика не существует");
                    }
                    break;
                case 8:
                    System.out.println(inMemoryTaskManager.getHistory());
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
        System.out.println("8. Посмотреть историю просмотров");
        System.out.println("0. Выход");
    }

    public static boolean isValidStatus(String status) {
        return status.equals("NEW") ||
                status.equals("IN_PROGRESS") ||
                status.equals("DONE");
    }
}