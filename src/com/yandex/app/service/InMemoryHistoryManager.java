package com.yandex.app.service;

import com.yandex.app.model.Node;
import com.yandex.app.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    public DoublyLinkedList<Task> historyLinkedList = new DoublyLinkedList<>();
    public Map<Integer, Node> historyHashMap = new HashMap<>();

    @Override
    public void addTask(Task task) {
        if (task != null) {
            removeNode(historyHashMap.get(task.getId()));
            historyLinkedList.linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        historyHashMap.remove(id);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyLinkedList.tasksArraylist;
    }

    public void removeNode(Node node) {
        historyHashMap.remove(node);
    }

    public static class DoublyLinkedList<T> {
        public Node<T> head;
        public Node<T> tail;
        private int size = 0;
        public ArrayList<T> tasksArraylist = new ArrayList<>();

        public void linkLast(T element) {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(null, element, oldTail);
            tail = newNode;
            if (oldTail == null)
                head = newNode;
            else
                oldTail.prev = newNode;
            size++;
            getTasks(element);
        }

        public void getTasks(T element) {
            tasksArraylist.add(element);
        }
    }
}
