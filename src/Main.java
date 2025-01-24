public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Задача 1.", "Описание задачи.");
        taskManager.createTask(task1);

        Task task2 = new Task("Задача 2.", "Описание задачи.");
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1.", "Описание эпика.");
        SubTask subTask1point1 = new SubTask("Подзадача 1.", "Описание подзадачи.");
        SubTask subTask1point2 = new SubTask("Подзадача 2.", "Описание подзадачи.");
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTask1point1, "Эпик 1.");
        taskManager.createSubTask(subTask1point2, "Эпик 1.");

        Epic epic2 = new Epic("Эпик 2.", "Описание эпика.");
        SubTask subTask2 = new SubTask("Подзадача 1.", "Описание подзадачи.");
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask2, "Эпик 2.");

        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getSubTasksList());
    }
}
