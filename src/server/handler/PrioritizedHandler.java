package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.Managers;
import server.HttpTaskServer;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (method.equals("GET")) {
            Set<Task> prioritized = Managers.getDefault().getPrioritizedTasks();
            String json = HttpTaskServer.getGson().toJson(prioritized);
            sendJson(exchange, json);
        } else {
            send(exchange, 405, "Обработка данного метода не предусмотрена");
        }
    }
}