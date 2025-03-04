package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.Managers;
import manager.exception.ManagerTaskCrossingException;
import manager.exception.NotFoundException;
import server.HttpTaskServer;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (method.equals("GET")) {
            if (path.length < 3) {
                getTasks(exchange);
            } else {
                try {
                    int id = Integer.parseInt(path[2]);
                    getTaskById(exchange, id);
                } catch (NumberFormatException e) {
                    send(exchange, 400, "Неверный формат id задачи");
                }
            }
        } else if (method.equals("POST")) {
            if (path.length < 3) {
                createTask(exchange, body);
            } else {
                try {
                    int id = Integer.parseInt(path[2]);
                    updateTask(exchange, body, id);
                } catch (NumberFormatException e) {
                    send(exchange, 400, "Неверный формат id задачи");
                }
            }
        } else if (method.equals("DELETE")) {
            if (path.length < 3) {
                deleteTasks(exchange);
            } else {
                try {
                    int id = Integer.parseInt(path[2]);
                    deleteTask(exchange, id);
                } catch (NumberFormatException e) {
                    send(exchange, 400, "Неверный формат id задачи");
                }
            }
        } else {
            send(exchange, 405, "Обработка данного метода не предусмотрена");
        }
    }

    private void getTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = Managers.getDefault().getTasks();
        String json = HttpTaskServer.getGson().toJson(tasks);
        sendJson(exchange, json);
    }

    private void getTaskById(HttpExchange exchange, int id) throws IOException {
        try {
            Task task = Managers.getDefault().getTask(id);
            String json = HttpTaskServer.getGson().toJson(task);
            sendJson(exchange, json);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void createTask(HttpExchange exchange, String body) throws IOException {
        try {
            Task task = HttpTaskServer.getGson().fromJson(body, Task.class);
            if (task instanceof Epic || task instanceof SubTask) {
                send(exchange, 400, "Неверное тело запроса, ожидалось Task");
            }
            Managers.getDefault().create(task);
            send(exchange, 201, "Задача добавлена");
        } catch (ManagerTaskCrossingException e) {
            sendHasInteractions(exchange, e.getMessage());
        }
    }

    private void updateTask(HttpExchange exchange, String body, int id) throws IOException {
        try {
            Task task = HttpTaskServer.getGson().fromJson(body, Task.class);
            if (task instanceof Epic || task instanceof SubTask) {
                send(exchange, 400, "Неверное тело запроса, ожидалось Task");
            }
            Managers.getDefault().update(task);
            send(exchange, 201, "Задача обновлена");
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void deleteTasks(HttpExchange exchange) throws IOException {
        Managers.getDefault().deleteAllTasks();
        send(exchange, 200, "Все задачи удалены");
    }

    private void deleteTask(HttpExchange exchange, int id) throws IOException {
        try {
            Managers.getDefault().deleteTask(id);
            send(exchange, 200, "Задача удалена");
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}