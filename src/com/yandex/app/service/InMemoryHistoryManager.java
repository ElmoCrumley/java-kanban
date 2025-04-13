package com.yandex.app.service;

import com.yandex.app.model.Node;
import com.yandex.app.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private DoublyLinkedList<Task> historyLinkedList = new DoublyLinkedList<>();
    public Map<Integer, Node> historyMap = new HashMap<>(); // Map<Node<task.id>, Node<task>>

    @Override
    public void addTask(Task task) {
        if (task != null) {
            if (historyMap.containsValue(task.getId())) {
                historyMap.remove(task.getId());
            }
            historyLinkedList.addLast(task);
        }
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)); {
            historyMap.remove(id);
            removeNode(historyMap.get(id));
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        int size = historyLinkedList.getSize();
        ArrayList<Task> historyList = new ArrayList<>(size);
        Node<Task> currentNode = historyLinkedList.head;

        while (null != currentNode) {
            historyList.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return historyList;
    }

    public void removeNode(Node node) {
        historyLinkedList.unlink(node);
    }

    public static class DoublyLinkedList<T> {
        public Node<T> head; // Node(null, T data, Node<T> next)
        public Node<T> tail; // Node(Node<T> prev, T data, null)
        // size of this shit (NULL, data, next)[0] ... (prev, data, next)[1] ... (prev, data, NULL)[size-1]
        private int size = 0;

        public void addLast(T element) {
            // [0 head](null, element, next) [1](prev, element, new) [2 tail](old, element, null)
            // [0 head](null, element, new) [1](prev, element, null)
            // [0 head](null, element, null)
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null) // Первый элемент
                head = newNode;
            else
                oldTail.next = newNode;
            size++;
        }

        public void unlink(Node<T> node) { // [0] [1] [2] -> [0] ... [1]
            // assert x != null;
            final T element = node.data; // [1]
            final Node<T> next = node.next; // [2]
            final Node<T> prev = node.prev; // [0]

            if (prev == null) { // [0] [1] -> ... [0]
                tail = next;
            } else { // [0] [1] [2] -> [0] ... [1] or [0] [1] -> [0] ...
                prev.next = next;
                node.prev = null;
            }

            if (next == null) { // [0] [1] -> [0] ...
                head = prev;
            } else { // [0] [1] [2] -> [0] ... [1] or [0] [1] -> ... [0]
                next.prev = prev;
                node.next = null;
            }

            node.data = null;
            size--;
        }

        public int getSize() {
            return size;
        }
    }
}