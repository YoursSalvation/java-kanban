package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.Managers;
import manager.exception.NotFoundException;
import server.HttpTaskServer;
import task.Epic;
import task.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (method.equals("GET")) {
            if (path.length < 3) {
                getEpics(exchange);
            } else {
                try {
                    int id = Integer.parseInt(path[2]);
                    if (path.length < 4) {
                        getEpicById(exchange, id);
                    } else if (path[3].equals("subtasks")) {
                        getEpicSubTasks(exchange, id);
                    }
                } catch (NumberFormatException e) {
                    send(exchange, 400, "Неверный формат id задачи");
                }
            }
        } else if (method.equals("POST")) {
            createEpic(exchange, body);
        } else if (method.equals("DELETE")) {
            if (path.length < 3) {
                deleteEpics(exchange);
            } else {
                try {
                    int id = Integer.parseInt(path[2]);
                    deleteEpic(exchange, id);
                } catch (NumberFormatException e) {
                    send(exchange, 400, "Неверный формат id задачи");
                }
            }
        } else {
            send(exchange, 405, "Обработка данного метода не предусмотрена");
        }
    }

    private void getEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = Managers.getDefault().getEpics();
        String json = HttpTaskServer.getGson().toJson(epics);
        sendJson(exchange, json);
    }

    private void getEpicSubTasks(HttpExchange exchange, int id) throws IOException {
        try {
            List<SubTask> subTasks = Managers.getDefault().getEpicSubTasks(id);
            String json = HttpTaskServer.getGson().toJson(subTasks);
            sendJson(exchange, json);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void getEpicById(HttpExchange exchange, int id) throws IOException {
        try {
            Epic epic = (Epic) Managers.getDefault().getTask(id);
            String json = HttpTaskServer.getGson().toJson(epic);
            sendJson(exchange, json);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void createEpic(HttpExchange exchange, String body) throws IOException {
        Epic epic = HttpTaskServer.getGson().fromJson(body, Epic.class);
        Managers.getDefault().create(epic);
        send(exchange, 201, "Эпик добавлен");
    }

    private void deleteEpics(HttpExchange exchange) throws IOException {
        Managers.getDefault().deleteAllEpics();
        send(exchange, 200, "Все эпики и их подзадачи удалены");
    }

    private void deleteEpic(HttpExchange exchange, int id) throws IOException {
        try {
            Managers.getDefault().deleteTask(id);
            send(exchange, 200, "Эпик удален");
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}