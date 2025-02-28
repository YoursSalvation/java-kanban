package server;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import manager.exception.ManagerTaskCrossingException;
import manager.exception.NotFoundException;
import org.junit.jupiter.api.*;
import server.deserializer.EpicListTypeToken;
import server.deserializer.SubTaskListTypeToken;
import server.deserializer.TaskListTypeToken;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HttpTaskServerTest {
    protected static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
    protected TaskManager taskManager = new InMemoryTaskManager();
    protected HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    protected void work_space_to_default() {
        taskManager.deleteAllSubTasks();
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        try {
            taskManager.create(new Task("test", "des", 0, Status.NEW, Duration.ofMinutes(60)
                    , LocalDateTime.parse("12.01.2025, 12:00", formatter)));
            taskManager.create(new Task("test1", "test1", 1, Status.NEW, Duration.ofMinutes(60)
                    , LocalDateTime.parse("12.01.2025, 14:00", formatter)));
            taskManager.create(new Task("test", "test", 2, Status.NEW
                    , Duration.ofMinutes(10), LocalDateTime.parse("10.01.2025, 10:00", formatter)));
            taskManager.create(new Epic(new Task("test", "Epic1", 3, Status.NEW)));
            taskManager.create(new SubTask(new Task("test", "test", 4, Status.NEW
                    , Duration.ofMinutes(60), LocalDateTime.parse("12.03.2025, 10:00", formatter)), 3));
            taskManager.create(new SubTask(new Task("test1", "test1", 5, Status.NEW
                    , Duration.ofMinutes(30), LocalDateTime.parse("10.03.2025, 10:00", formatter)), 3));
        } catch (ManagerTaskCrossingException e) {
            System.out.println(e.getMessage());
        }
        try {
            httpTaskServer.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    @Order(1)
    protected void get_methods() throws IOException, InterruptedException, NotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);
        HttpRequest.Builder requsetBuilder = HttpRequest.newBuilder();
        HttpRequest request;
        HttpResponse<String> response;

        request = requsetBuilder.GET()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Accept", "application/json")
                .build();
        response = client.send(request, handler);
        List<Task> excepted = taskManager.getTasks();
        List<Task> actual = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(excepted, actual);

        request = requsetBuilder.GET()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Accept", "application/json")
                .build();
        response = client.send(request, handler);
        List<Epic> exceptedEpics = taskManager.getEpics();
        List<Epic> actualEpics = gson.fromJson(response.body(), new EpicListTypeToken().getType());
        assertEquals(exceptedEpics, actualEpics);

        request = requsetBuilder.GET()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Accept", "application/json")
                .build();
        response = client.send(request, handler);
        List<SubTask> exceptedSubTasks = taskManager.getSubTasks();
        List<SubTask> actualSubTasks = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());
        assertEquals(exceptedSubTasks, actualSubTasks);

        request = requsetBuilder.GET()
                .uri(URI.create("http://localhost:8080/tasks/0"))
                .header("Accept", "application/json")
                .build();
        response = client.send(request, handler);
        Task exceptedTask = taskManager.getTask(0);
        Task actualTask = gson.fromJson(response.body(), Task.class);
        assertEquals(exceptedTask, actualTask);

        request = requsetBuilder.GET()
                .uri(URI.create("http://localhost:8080/epics/3"))
                .header("Accept", "application/json")
                .build();
        response = client.send(request, handler);
        Epic exceptedEpic = (Epic) taskManager.getTask(3);
        Epic actualEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(exceptedEpic, actualEpic);

        request = requsetBuilder.GET()
                .uri(URI.create("http://localhost:8080/subtasks/4"))
                .header("Accept", "application/json")
                .build();
        response = client.send(request, handler);
        SubTask exceptedSubTask = (SubTask) taskManager.getTask(4);
        SubTask actualSubTask = gson.fromJson(response.body(), SubTask.class);
        assertEquals(exceptedSubTask, actualSubTask);

        request = requsetBuilder.GET()
                .uri(URI.create("http://localhost:8080/epics/3/subtasks"))
                .header("Accept", "application/json")
                .build();
        response = client.send(request, handler);
        List<SubTask> exceptedListSubTask = taskManager.getEpicSubTasks(3);
        List<SubTask> actualListSubTask = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());
        assertEquals(exceptedListSubTask, actualListSubTask);

        request = requsetBuilder.GET()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .header("Accept", "application/json")
                .build();
        response = client.send(request, handler);
        assertEquals(200, response.statusCode());
    }

    @Test
    @Order(2)
    protected void post_methods() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);
        HttpRequest.Builder requsetBuilder = HttpRequest.newBuilder();
        HttpRequest request;
        HttpResponse<String> response;

        Task task = new Task("test", "test", 6, Status.NEW, Duration.ofMinutes(60),
                LocalDateTime.parse("12.01.2025, 10:00", formatter));
        request = requsetBuilder.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        response = client.send(request, handler);
        assertEquals(201, response.statusCode());

        Epic epic = new Epic(new Task("test", "test", 7, Status.NEW));
        request = requsetBuilder.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        response = client.send(request, handler);
        assertEquals(201, response.statusCode());

        SubTask subTask = new SubTask(new Task("test", "test", 8, Status.NEW, Duration.ofMinutes(60),
                LocalDateTime.parse("22.11.2025, 12:00", formatter)), 7);
        request = requsetBuilder.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        response = client.send(request, handler);
        assertEquals(201, response.statusCode());

        SubTask updateSubTask = new SubTask(new Task("title", "test1", 8, Status.NEW, Duration.ofMinutes(60),
                LocalDateTime.parse("20.01.2025, 12:00", formatter)), 7);
        request = requsetBuilder.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(updateSubTask)))
                .uri(URI.create("http://localhost:8080/subtasks/8"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        response = client.send(request, handler);
        assertEquals(201, response.statusCode());

        Task updateTask = new Task("title", "title", 6, Status.NEW, Duration.ofMinutes(60),
                LocalDateTime.parse("12.01.2025, 10:00", formatter));
        request = requsetBuilder.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(updateTask)))
                .uri(URI.create("http://localhost:8080/tasks/6"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        response = client.send(request, handler);
        assertEquals(201, response.statusCode());
    }

    @Test
    @Order(3)
    protected void delete_methods() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);
        HttpRequest.Builder requsetBuilder = HttpRequest.newBuilder();
        HttpRequest request;
        HttpResponse<String> response;

        request = requsetBuilder.DELETE()
                .uri(URI.create("http://localhost:8080/tasks/6"))
                .header("Accept", "application/json")
                .build();
        response = client.send(request, handler);
        assertEquals(200, response.statusCode());

        request = requsetBuilder.DELETE()
                .uri(URI.create("http://localhost:8080/subtasks/8"))
                .header("Accept", "application/json")
                .build();
        response = client.send(request, handler);
        assertEquals(200, response.statusCode());

        request = requsetBuilder.DELETE()
                .uri(URI.create("http://localhost:8080/epics/7"))
                .header("Accept", "application/json")
                .build();
        response = client.send(request, handler);
        assertEquals(200, response.statusCode());
    }

    @AfterEach
    protected void server_stop() {
        httpTaskServer.stop();
    }
}