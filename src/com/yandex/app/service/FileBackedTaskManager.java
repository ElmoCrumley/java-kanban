package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager  {
    final File autoSave;

    public FileBackedTaskManager(File autoSave) {
        this.autoSave = autoSave;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask, int epicsId) {
        super.createSubTask(subTask, epicsId);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubTasks() {
        super.removeSubTasks();
        save();
    }

    // Запись в файл.
    private void save() {
        List<Task> tasks = null;
        List<Epic> epics = null;
        List<SubTask> subTasks = null;

        try {
            tasks = super.getTasksList();
            epics = super.getEpicsList();
            subTasks = super.getSubTasksList();
        } catch (NotFoundException ignored) {

        }

        try (Writer bufferedWriter = new BufferedWriter(new FileWriter(autoSave.getAbsoluteFile()))) {
            if (tasks != null) {
                for (Task task : super.getTasksList()) {
                    bufferedWriter.write(toString(task) + "\n");
                }
            }

            if (epics != null) {
                for (Epic epic : super.getEpicsList()) {
                    bufferedWriter.write(toString(epic) + "\n");
                }
            }

            if (subTasks != null) {
                for (SubTask subTask : super.getSubTasksList()) {
                    bufferedWriter.write(toString(subTask) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    private String toString(Task task) {
        return task.getId() + ","
                + task.getType() + ","
                + task.getName() + ","
                + task.getStatus() + ","
                + task.getDescription() + ","
                + task.getStartTime() + ","
                + task.getDuration().toMinutes();
    }

    private String toString(SubTask subTask) {
        return subTask.getId() + ","
                + subTask.getType() + ","
                + subTask.getName() + ","
                + subTask.getStatus() + ","
                + subTask.getDescription() + ","
                + subTask.getStartTime() + ","
                + subTask.getDuration().toMinutes() + ","
                + subTask.getEpicsId();
    }

    // Восстановление данных из файла.
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try {
            Reader fileReader = new FileReader(file.getAbsolutePath());
            BufferedReader br = new BufferedReader(fileReader);

            while (br.ready()) {
                String line = br.readLine();
                Task task = fromString(line);

                if (task != null) {
                    switch (task.getType()) {
                        case Type.TASK:
                            fileBackedTaskManager.getTasks().put(task.getId(), task);
                            break;
                        case Type.EPIC:
                            fileBackedTaskManager.getEpics().put(task.getId(), (Epic) task);
                            break;
                        case Type.SUBTASK:
                            fileBackedTaskManager.getSubTasks().put(task.getId(), (SubTask) task);
                            for (Epic epic : fileBackedTaskManager.getEpics().values()) {
                                if (epic.getId() == ((SubTask) task).getEpicsId()) {
                                    epic.addSubTaskToList((SubTask) task);
                                }
                            }
                            break;
                    }
                }
            }

            fileReader.close();
            br.close();
            return fileBackedTaskManager;
        } catch (IOException x) {
            throw new RuntimeException(x);
        }
    }

    public static Task fromString(String value) {
        String[] split = value.split(","); // id,type,name,status,description,epic
        String id = split[7];
        switch (split[1]) {
            case "TASK":
                Task task = new Task(
                        split[2],
                        split[4],
                        Duration.ofMinutes(Integer.parseInt(split[6])),
                        LocalDateTime.parse(split[5])
                );

                task.setId(Integer.parseInt(split[0]));
                task.setStatus(Status.valueOf(split[3]));
                return task;
            case "EPIC":
                Epic epic = new Epic(
                        split[2],
                        split[4],
                        Duration.ofMinutes(Integer.parseInt(split[6])),
                        LocalDateTime.parse(split[5])
                );

                epic.setId(Integer.parseInt(split[0]));
                epic.setStatus(Status.valueOf(split[3]));
                return epic;
            case "SUBTASK":
                SubTask subTask = new SubTask(
                        split[2],
                        split[4],
                        Duration.ofMinutes(Integer.parseInt(split[6])),
                        LocalDateTime.parse(split[5]),
                        Integer.parseInt(split[7])
                );

                subTask.setId(Integer.parseInt(split[0]));
                subTask.setStatus(Status.valueOf(split[3]));
                return subTask;
            default:
                return null;
        }
    }
}
