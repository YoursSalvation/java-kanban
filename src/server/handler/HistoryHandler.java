package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.Managers;
import server.HttpTaskServer;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (method.equals("GET")) {
            List<Task> history = Managers.getDefaultHistory().getHistory();
            String json = HttpTaskServer.getGson().toJson(history);
            sendJson(exchange, json);
        } else {
            send(exchange, 405, "Обработка данного метода не предусмотрена");
        }
    }
}