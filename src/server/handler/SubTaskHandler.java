package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.Managers;
import manager.exception.ManagerTaskCrossingException;
import manager.exception.NotFoundException;
import server.HttpTaskServer;
import task.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (method.equals("GET")) {
            if (path.length < 3) {
                getSubTasks(exchange);
            } else {
                try {
                    int id = Integer.parseInt(path[2]);
                    getSubTaskById(exchange, id);
                } catch (NumberFormatException e) {
                    send(exchange, 400, "Неверный формат id задачи");
                }
            }
        } else if (method.equals("POST")) {
            if (path.length < 3) {
                createSubTask(exchange, body);
            } else {
                try {
                    int id = Integer.parseInt(path[2]);
                    updateSubTask(exchange, body, id);
                } catch (NumberFormatException e) {
                    send(exchange, 400, "Неверный формат id задачи");
                }
            }
        } else if (method.equals("DELETE")) {
            if (path.length < 3) {
                deleteSubTasks(exchange);
            } else {
                try {
                    int id = Integer.parseInt(path[2]);
                    deleteSubTask(exchange, id);
                } catch (NumberFormatException e) {
                    send(exchange, 400, "Неверный формат id задачи");
                }
            }
        } else {
            send(exchange, 405, "Обработка данного метода не предусмотрена");
        }
    }

    private void getSubTasks(HttpExchange exchange) throws IOException {
        List<SubTask> subTasks = Managers.getDefault().getSubTasks();
        String json = HttpTaskServer.getGson().toJson(subTasks);
        sendJson(exchange, json);
    }

    private void getSubTaskById(HttpExchange exchange, int id) throws IOException {
        try {
            SubTask subTask = (SubTask) Managers.getDefault().getTask(id);
            String json = HttpTaskServer.getGson().toJson(subTask);
            sendJson(exchange, json);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void createSubTask(HttpExchange exchange, String body) throws IOException {
        try {
            SubTask subTask = (SubTask) HttpTaskServer.getGson().fromJson(body, SubTask.class);
            Managers.getDefault().create(subTask);
            send(exchange, 201, "Подзадача добавлена");
        } catch (ManagerTaskCrossingException e) {
            sendHasInteractions(exchange, e.getMessage());
        }
    }

    private void updateSubTask(HttpExchange exchange, String body, int id) throws IOException {
        try {
            SubTask subTask = (SubTask) HttpTaskServer.getGson().fromJson(body, SubTask.class);
            Managers.getDefault().update(subTask);
            send(exchange, 201, "Подзадача обновлена");
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void deleteSubTasks(HttpExchange exchange) throws IOException {
        Managers.getDefault().deleteAllSubTasks();
        send(exchange, 200, "Все подзадачи удалены");
    }

    private void deleteSubTask(HttpExchange exchange, int id) throws IOException {
        try {
            Managers.getDefault().deleteTask(id);
            send(exchange, 200, "Задача удалена");
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}