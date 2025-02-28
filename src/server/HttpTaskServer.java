package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import server.deserializer.DateTimeAdapter;
import server.deserializer.DurationAdapter;
import server.handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static HttpServer httpServer;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new DateTimeAdapter())
            .create();
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer(new InMemoryTaskManager());
        try {
            httpTaskServer.start();
            System.out.println("Server ready");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            httpTaskServer.stop();
        }
    }

    public static Gson getGson() {
        return gson;
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.createContext("/subtasks", new SubTaskHandler());
        httpServer.createContext("/epics", new EpicHandler());
        httpServer.createContext("/history", new HistoryHandler());
        httpServer.createContext("/prioritized", new PrioritizedHandler());
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }
}