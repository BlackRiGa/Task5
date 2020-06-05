package ru.vsu.cs.course1.tree;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Интерфейс для двоичного дерева с реализацияей по умолчанию различных обходов
 * дерева
 *
 * @param <T>
 */
public interface DefaultBinaryTree<T> extends Iterable<T> {

    /**
     * Интерфейс для описания узла двоичного дерева
     *
     * @param <T>
     */
    interface TreeNode<T> {

        /**
         * @return Значение в узле дерева
         */
        T getValue();

        /**
         * @return Левое поддерево
         */
        default TreeNode<T> getLeft() {
            return null;
        }

        /**
         * @return Правое поддерево
         */
        default TreeNode<T> getRight() {
            return null;
        }

        /**
         * @return Цвет узла (для рисования и не только)
         */
        default Color getColor() {
            return Color.BLACK;
        }
    }

    /**
     * @return Корень (вершина) дерева
     */
    TreeNode<T> getRoot();

    @FunctionalInterface
    interface Visitor<T> {

        public void visit(T value, int level);
    }

    /**
     * Обход дерева "посетителем" в прямом/NLR порядке - рекурсивная реализпция
     *
     * @param visitor Посетитель
     */
    default void preOrderVisit(Visitor<T> visitor) {
        // данный класс нужен только для того, чтобы "спрятать" его метод (c 3-мя параметрами)
        class Inner {

            <T> void preOrderVisit(TreeNode<T> node, Visitor<T> visitor, int level) {
                if (node == null) {
                    return;
                }
                visitor.visit(node.getValue(), level);
                preOrderVisit(node.getLeft(), visitor, level + 1);
                preOrderVisit(node.getRight(), visitor, level + 1);
            }
        }
        // класс приходится создавать, т.к. статические методы в таких класс не поддерживаются
        new Inner().preOrderVisit(getRoot(), visitor, 0);
    }

    /**
     * Обход дерева в виде итератора в прямом/NLR порядке (предполагается, что в
     * процессе обхода дерево не меняется)
     *
     * @return Итератор
     */
    default Iterable<T> preOrderValues() {
        return () -> {
            Stack<TreeNode<T>> stack = new Stack<>();
            if (getRoot() != null) {
                stack.push(getRoot());
            }

            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return stack.size() > 0;
                }

                @Override
                public T next() {
                    TreeNode<T> node = stack.pop();
                    if (node.getRight() != null) {
                        stack.push(node.getRight());
                    }
                    if (node.getLeft() != null) {
                        stack.push(node.getLeft());
                    }
                    return node.getValue();
                }

            };
        };
    }

    /**
     * Обход дерева "посетителем" в симметричном/поперечном/центрированном/LNR
     * порядке - рекурсивная реализпция
     *
     * @param visitor Посетитель
     */
    default void inOrderVisit(Visitor<T> visitor) {
        // данный класс нужен только для того, чтобы "спрятать" его метод (c 3-мя параметрами)
        class Inner {

            <T> void inOrderVisit(TreeNode<T> node, Visitor<T> visitor, int level) {
                if (node == null) {
                    return;
                }
                inOrderVisit(node.getLeft(), visitor, level + 1);
                visitor.visit(node.getValue(), level);
                inOrderVisit(node.getRight(), visitor, level + 1);
            }
        }
        // класс приходится создавать, т.к. статические методы в таких класс не поддерживаются
        new Inner().inOrderVisit(getRoot(), visitor, 0);
    }

    /**
     * Обход дерева в виде итератора в
     * симметричном/поперечном/центрированном/LNR порядке (предполагается, что в
     * процессе обхода дерево не меняется)
     *
     * @return Итератор
     */
    default Iterable<T> inOrderValues() {
        return () -> {
            Stack<TreeNode<T>> stack = new Stack<>();
            TreeNode<T> node = getRoot();
            while (node != null) {
                stack.push(node);
                node = node.getLeft();
            }

            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return !stack.isEmpty();
                }

                @Override
                public T next() {
                    TreeNode<T> node = stack.pop();
                    T result = node.getValue();
                    if (node.getRight() != null) {
                        node = node.getRight();
                        while (node != null) {
                            stack.push(node);
                            node = node.getLeft();
                        }
                    }
                    return result;
                }
            };
        };
    }

    /**
     * Обход дерева "посетителем" в обратном/LRN порядке - рекурсивная
     * реализпция
     *
     * @param visitor Посетитель
     */
    default void postOrderVisit(Visitor<T> visitor) {
        // данный класс нужен только для того, чтобы "спрятать" его метод (c 3-мя параметрами)
        class Inner {

            <T> void postOrderVisit(TreeNode<T> node, Visitor<T> visitor, int level) {
                if (node == null) {
                    return;
                }
                postOrderVisit(node.getLeft(), visitor, level + 1);
                postOrderVisit(node.getRight(), visitor, level + 1);
                visitor.visit(node.getValue(), level);
            }
        }
        // класс приходится создавать, т.к. статические методы в таких класс не поддерживаются
        new Inner().postOrderVisit(getRoot(), visitor, 0);
    }

    /**
     * Обход дерева в виде итератора в обратном/LRN порядке (предполагается, что
     * в процессе обхода дерево не меняется)
     *
     * @return Итератор
     */
    default Iterable<T> postOrderValues() {
        return () -> {
            // Реализация TreeNode<T>, где left = right = null
            TreeNode<T> emptyNode = () -> null;

            Stack<TreeNode<T>> stack = new Stack<>();
            Stack<T> valuesStack = new Stack<>();
            if (getRoot() != null) {
                stack.push(getRoot());
            }

            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return stack.size() > 0;
                }

                @Override
                public T next() {
                    for (TreeNode<T> node = stack.pop(); node != emptyNode; node = stack.pop()) {
                        if (node.getRight() == null && node.getLeft() == null) {
                            return node.getValue();
                        }
                        valuesStack.push(node.getValue());
                        stack.push(emptyNode);
                        if (node.getRight() != null) {
                            stack.push(node.getRight());
                        }
                        if (node.getLeft() != null) {
                            stack.push(node.getLeft());
                        }
                    }
                    return valuesStack.pop();
                }
            };
        };
    }

    /**
     * Обход дерева "посетителем" по уровням
     *
     * @param visitor Посетитель
     */
    default void byLevelVisit(Visitor<T> visitor) {
        class QueueItem {

            public DefaultBinaryTree.TreeNode<T> node;
            public int level;

            public QueueItem(DefaultBinaryTree.TreeNode<T> node, int level) {
                this.node = node;
                this.level = level;
            }
        }

        if (getRoot() == null) {
            return;
        }
        Queue<QueueItem> queue = new LinkedList<>();
        queue.add(new QueueItem(getRoot(), 0));
        while (!queue.isEmpty()) {
            QueueItem item = queue.poll();
            if (item.node.getLeft() != null) {
                queue.add(new QueueItem(item.node.getLeft(), item.level + 1));
            }
            if (item.node.getRight() != null) {
                queue.add(new QueueItem(item.node.getRight(), item.level + 1));
            }
            visitor.visit(item.node.getValue(), item.level);
        }
    }

    /**
     * Обход дерева в виде итератора по уровням (предполагается, что в процессе
     * обхода дерево не меняется)
     *
     * @return Итератор
     */
    default Iterable<T> byLevelValues() {
        return () -> {
            class QueueItem {

                public DefaultBinaryTree.TreeNode<T> node;
                public int level;

                public QueueItem(DefaultBinaryTree.TreeNode<T> node, int level) {
                    this.node = node;
                    this.level = level;
                }
            }

            Queue<QueueItem> queue = new LinkedList<>();
            if (getRoot() != null) {
                queue.add(new QueueItem(getRoot(), 0));
            }

            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return queue.size() > 0;
                }

                @Override
                public T next() {
                    QueueItem item = queue.poll();
                    if (item.node.getLeft() != null) {
                        queue.add(new QueueItem(item.node.getLeft(), item.level + 1));
                    }
                    if (item.node.getRight() != null) {
                        queue.add(new QueueItem(item.node.getRight(), item.level + 1));
                    }
                    return item.node.getValue();
                }
            };
        };
    }

    /**
     * Реализация Iterable&lt;T&gt;
     *
     * @return Итератор
     */
    @Override
    default Iterator<T> iterator() {
        return inOrderValues().iterator();
    }
}
