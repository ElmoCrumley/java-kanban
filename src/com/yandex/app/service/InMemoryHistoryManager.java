package com.yandex.app.service;

import com.yandex.app.model.Node;
import com.yandex.app.model.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final DoublyLinkedList<Task> historyLinkedList = new DoublyLinkedList<>();
    private final Map<Integer, Node> historyMap = new HashMap<>(); // Map<Node<task.id>, Node<task>>

    @Override
    public void addTask(Task task) {
        if (task != null) {
            int taskId = task.getId();

            if (historyMap.containsKey(taskId)) {
                remove(taskId);
            }
            historyLinkedList.addLast(task, task.getId());
            historyMap.put(taskId, historyLinkedList.getLast());
        }
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            removeNode(historyMap.remove(id));
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
        if (node != null) {
            historyLinkedList.unlink(node);
        }
    }

    @Override
    public void clearAllHistory() {
        historyMap.clear();
        historyLinkedList.clear();
    }

    public static class DoublyLinkedList<T> {
        public Node<T> head;
        public Node<T> tail;
        private int size = 0;

        public DoublyLinkedList() {
            head = new Node<>(null, null, tail);
            tail = new Node<>(head, null, null);
        }

        public void addLast(T element, int taskId) {
            // [head](null, null, next) [0](prev, last, next) ... [tail](prev, null, null)
            Node<T> prev = tail.prev;
            Node<T> newNode = new Node<>(prev, element, tail);

            newNode.prev.next = newNode;
            newNode.next.prev = newNode;
            size++;
        }

        public void unlink(Node<T> node) {
            final Node<T> next = node.next;
            final Node<T> prev = node.prev;

            prev.next = next;
            next.prev = prev;
            node.data = null;
            size--;
        }

        public Node<T> getLast() {
            return tail.prev;
        }

        public int getSize() {
            return size;
        }

        public void clear() {
            Node<T> nodeForClear = head.next;
            Node<T> nodeNext = nodeForClear.next;
            while (nodeForClear != tail) {
                nodeForClear.prev = null;
                nodeForClear.data = null;
                nodeForClear.next = null;
                nodeForClear = nodeNext;
                nodeNext = nodeForClear.next;
            }
            head.next = tail;
            tail.prev = head;
        }
    }
}