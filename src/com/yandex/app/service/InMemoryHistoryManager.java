package com.yandex.app.service;

import com.yandex.app.model.Node;
import com.yandex.app.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final DoublyLinkedList<Task> historyLinkedList = new DoublyLinkedList<>();
    public static Map<Integer, Node> historyMap = new HashMap<>(); // Map<Node<task.id>, Node<task>>

    @Override
    public void addTask(Task task) {
        int taskId = task.getId();
        if (task != null) {
            if (historyMap.containsValue(taskId)) {
                historyMap.remove(taskId);
            }
            historyLinkedList.addLast(task, taskId);
        }
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            removeNode(historyMap.get(id));
            historyMap.remove(id);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        int size = historyLinkedList.getSize();
        ArrayList<Task> historyList = new ArrayList<>(size);
        Node<Task> currentNode = historyLinkedList.head.next;

        while (currentNode != historyLinkedList.tail) {
            historyList.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return historyList;
    }

    public void removeNode(Node node) {
        historyLinkedList.unlink(node);
    }

    public static class DoublyLinkedList<T> {
        public Node<T> lastNode = new Node<>(null, null, null);
        public Node<T> head = new Node<>(null, null, null);
        public Node<T> tail = new Node<>(null, null, null);
        private int size = 0;

        public void addLast(T element, int taskId) {
            // [head](null, null, next) [0](prev, last, next) ... [tail](prev, null, null)
            final Node<T> oldNode = lastNode;

            lastNode = new Node<>(oldNode, element, tail);
            lastNode.prev.next = lastNode;
            lastNode.next.prev = lastNode;

            if (size == 0) {
                head.next = lastNode;
                lastNode.prev = head;
            }

            historyMap.put(taskId, lastNode);
            size++;
        }

        public void unlink(Node<T> node) {
            final T element = node.data;
            final Node<T> next = node.next;
            final Node<T> prev = node.prev;

            prev.next = next;
            next.prev = prev;
            node.next = null;
            size--;
        }

        public int getSize() {
            return size;
        }
    }
}