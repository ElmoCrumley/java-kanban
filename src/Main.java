public class Main {

    public static void main(String[] args) {
        Task task1 = new Task("Задача 1.", "Описание задачи.");
        Task task2 = new Task("Задача 2.", "Описание задачи.");
        Epic epic1 = new Epic("Эпик 1.", "Описание эпика.");
        SubTask subTask1point1 = new SubTask("Подзадача 1", "Описание подзадачи.");
        SubTask subTask1point2 = new SubTask("Подзадача 2", "Описание подзадачи.");
        Epic epic2 = new Epic("Эпик 1.", "Описание эпика.");
        SubTask subTask2 = new SubTask("Подзадача 1", "Описание подзадачи.");
    }
}
