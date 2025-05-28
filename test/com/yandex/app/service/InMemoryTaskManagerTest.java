package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Task;
import com.yandex.app.model.TasksForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
        tft = new TasksForTests();

        for (Task task : tft.tasks) {
            taskManager.createTask(task);
        }
        for (Epic epic : tft.epics) {
            taskManager.createEpic(epic);
        }
        for (int i = 0; i < tft.allSubtasks.size(); i++) {
            for (int j = 0; j < tft.allSubtasks.get(i).size(); j++) {
                taskManager.createSubTask(tft.allSubtasks.get(i).get(j), tft.epics.get(i).getId());
            }
        }
    }

    @Test
    public void createTaskMethodsTest() {
        testMultipleAssertionsWithStreamForCreateAnyTask();
    }

    @Test
    public void getTasksListMethodsTest() {
        testMultipleAssertionsWithStreamForGetList();
    }

    @Test
    public void removeAllTaskMethodsTest() {
        testMultipleAssertionsWithStreamForRemoveAllTask();
    }

    @Test
    public void getTaskMethodsTest() {
        testMultipleAssertionsWithStreamForGetAnyTask();
    }

    @Test
    public void updateTaskMethodsTest() {
        testMultipleAssertionsWithStreamForUpdateAnyTask();
    }

    @Test
    public void removeMethodsTest() {
        testMultipleAssertionsWithStreamForRemove();
    }

    @Test
    public void clearAllTasks() {
        testMultipleAssertionsWithStreamForClearAllTasks();
    }

    @Test
    public void getHistoryManagerTest() {
        testForGetHistoryManager();
    }

//    public Map<Integer, Task> getTasks() {
//        return tasks;
//    }
//
//    public Map<Integer, Epic> getEpics() {
//        return epics;
//    }
//
//    public Map<Integer, SubTask> getSubTasks() {
//        return subTasks;
//    }
//
//    // Получение списка всех подзадач определённого эпика. (дополнительный метод)
//    public static ArrayList<SubTask> getEpicsSubTasksList(Epic epic) {
//        return new ArrayList<>(epic.getSubTasksList());
//    }
//
//    public ArrayList<Task> getPrioritizedTasks() {
//        return new ArrayList<>(allTasksWithDuration);
//    }
//
//    public boolean isNotIntersectAny(Task t2) {
//        if (!allTasksWithDuration.isEmpty()) {
//            return (allTasksWithDuration.stream().filter(task -> task.isIntersect(t2)).count() == 0);
//        } else{
//            return true;
//        }
//    }
//
//    public static class DataComparator implements Comparator<Task> {
//        @Override
//        public int compare(Task o1, Task o2) {
//            if (o1.getStartTime().isBefore(o2.getStartTime())) {
//                return -1;
//            } else if (o1.getStartTime().isAfter(o2.getStartTime())) {
//                return 1;
//            } else {
//                return 0;
//            }
//        }
//    }
}
