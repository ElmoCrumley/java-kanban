import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();

    // Получение списка всех задач.
    public ArrayList<String> getTasksList() {
        ArrayList<String> tasksList = new ArrayList<>();

        for (Task task : tasks.values()) {
            tasksList.add(task.getName());
        }
        return tasksList;
    }

    public ArrayList<String> getEpicsList() {
        ArrayList<String> epicsList = new ArrayList<>();

        for (Epic epic : epics.values()) {
            epicsList.add(epic.getName());
        }
        return epicsList;
    }

    public ArrayList<String> getSubTasksList() {
        ArrayList<String> subTasksList = new ArrayList<>();

        for (SubTask subTask : subTasks.values()) {
            subTasksList.add(subTask.getName());
        }
        return subTasksList;
    }

    // Удаление всех задач.
    public void removeTasks() {
        for (int key : tasks.keySet()) {
            tasks.remove(key);
        }
    }

    public void removeEpics() {
        for (int key : epics.keySet()) {
            epics.remove(key);
        }
    }

    public void removeSubTasks() {
        for (int key : subTasks.keySet()) {
            subTasks.remove(key);
        }
    }

    // Получение по идентификатору.
    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public SubTask getSubTask(int id) {
        return subTasks.get(id);
    }

    // Создание.
    public void createTask (Task task) {
        int id = task.hashCode();

        task.setId(id);
        tasks.put(id, task);
    }

    public void createEpic (Epic epic) {
        int id = epic.hashCode();

        epic.setId(id);
        epics.put(id, epic);
    }

    public void createSubTask (SubTask subTask, String epicsName) {
        int id = subTask.hashCode();

        subTask.setId(id);
        subTasks.put(id, subTask);
        subTask.setEpicsName(epicsName);

        for (Epic epic : epics.values()) {
            if (epic.getName().equals(epicsName)) {
                epic.setSubtaskToList(subTask.getName());
                epic.backlogLevel++;
            }
        }
    }

    // Обновление.
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        if (task.getStatus().equals(Status.NEW)) {
            task.setStatus(Status.IN_PROGRESS.name());
        } else if (task.getStatus().equals(Status.IN_PROGRESS)) {
            task.setStatus(Status.DONE.name());
        }
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        if (subTask.getStatus().equals(Status.NEW)) {
            subTask.setStatus(Status.IN_PROGRESS.name());
            for (Epic epic : epics.values()) {
                if (epic.getName().equals(subTask.getEpicsName())) {
                    epic.setStatus(Status.IN_PROGRESS.name());
                }
            }
        } else if (subTask.getStatus().equals(Status.IN_PROGRESS)) {
            subTask.setStatus(Status.DONE.name());
            for (Epic epic : epics.values()) {
                if (subTask.getEpicsName().equals(epic.getName())) {
                    epic.backlogLevel--;
                    if (epic.backlogLevel == 0) {
                        epic.setStatus(Status.DONE.name());
                    }
                }
            }
        }
    }

    // Удаление по идентификатору.
    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeEpic(int id) {
        epics.remove(id);
    }

    public void removeSubTask(int id) {
        subTasks.remove(id);
    }

    // Получение списка всех подзадач определённого эпика.
    public ArrayList<String> getEpicsSubTasksList(Epic epic) {
        return epic.getEpicsSubTasksList();
    }
}
