package ru.vsu.cs.course1.tree.bst;

import java.util.function.Function;
import ru.vsu.cs.course1.tree.BinaryTree;
import ru.vsu.cs.course1.tree.DefaultBinaryTree;

/**
 * Класс, реализующий простое (наивное) дерево поиска
 * @param <T>
 */
public class SimpleBSTree<T extends Comparable<? super T>> extends BinaryTree<T> implements DefaultBSTree<T> {

    private static class CheckBSTResult<T> {

        public boolean result;
        public int size;
        public T min;
        public T max;
        public int k;
        StringBuilder path;
        public DefaultBinaryTree.TreeNode<T> minEl;
        public DefaultBinaryTree.TreeNode<T> maxEl;

        public CheckBSTResult(boolean result, int size, T min, T max) {
            this.result = result;
            this.size = size;
            this.min = min;
            this.max = max;
            this.k = 0;
            this.path = new StringBuilder();
        }

        public CheckBSTResult(boolean result, int size, T min, T max, DefaultBinaryTree.TreeNode<T> minEl, DefaultBinaryTree.TreeNode<T> maxEl) {
            this.result = result;
            this.size = size;
            this.min = min;
            this.max = max;
            this.minEl = minEl;
            this.maxEl = maxEl;
            this.k = 0;
            this.path = new StringBuilder();
        }
    }

    int size = 0;

    public SimpleBSTree(Function<String, T> fromStrFunc, Function<T, String> toStrFunc) {
        super(fromStrFunc, toStrFunc);
    }

    public SimpleBSTree(Function<String, T> fromStrFunc) {
        super(fromStrFunc);
    }

    public SimpleBSTree() {
        super();
    }

    private static <T extends Comparable<? super T>> CheckBSTResult<T> isBSTInner(DefaultBinaryTree.TreeNode<T> node) {
        if (node == null) {
            return null;
        }

        CheckBSTResult<T> leftResult = isBSTInner(node.getLeft());
        CheckBSTResult<T> rightResult = isBSTInner(node.getRight());
        CheckBSTResult<T> result = new CheckBSTResult<>(true, 1, node.getValue(), node.getValue());
        if (leftResult != null) {
            result.result &= leftResult.result;
            result.result &= leftResult.max.compareTo(node.getValue()) < 0;
            result.size += leftResult.size;
            result.min = leftResult.min;
        }
        if (rightResult != null) {
            result.result &= rightResult.result;
            result.size += rightResult.size;
            result.result &= rightResult.min.compareTo(node.getValue()) > 0;
            result.max = rightResult.max;
        }
        return result;
    }

    /**
     * Проверка, является ли поддерево деревом поиска
     *
     * @param <T>
     * @param node Поддерево
     * @return treu/false
     */
    public static <T extends Comparable<? super T>> boolean isBST(DefaultBinaryTree.TreeNode<T> node) {
        return node == null ? true : isBSTInner(node).result;
    }

    /**
     * Загрузка дерева из скобочного представления
     *
     * @param bracketStr
     * @throws Exception Если переаддное скобочное представление не является
     * деревом поиска
     */
    @Override
    public void fromBracketNotation(String bracketStr) throws Exception {
        BinaryTree tempTree = new BinaryTree(this.fromStrFunc);
        tempTree.fromBracketNotation(bracketStr);
        CheckBSTResult<T> tempTreeResult = isBSTInner(tempTree.getRoot());
        if (!tempTreeResult.result) {
            throw new Exception("Заданное дерево не является деревом поиска!");
        }
        super.fromBracketNotation(bracketStr);
        this.size = tempTreeResult.size;
    }

    public String deletedElement(String bracketStr) throws Exception {//8 (3 (1 (5), 6), 10 (, 15 (12, 18)))
       BinaryTree tempTree = new BinaryTree(this.fromStrFunc);
        tempTree.fromBracketNotation(bracketStr);
        CheckBSTResult<T> tempTreeResult = myIsBSTInner(tempTree.getRoot());
        if (tempTreeResult.k >= 2) {
            return "More error element";
        }
        if (tempTreeResult.k == 1) {
            return tempTreeResult.path.toString();
        }
        return "No error element";
    }

    private static <T extends Comparable<? super T>> CheckBSTResult<T> myIsBSTInner(DefaultBinaryTree.TreeNode<T> node) {
        if (node == null) {
            return null;
        }

        CheckBSTResult<T> leftResult = myIsBSTInner(node.getLeft());
        CheckBSTResult<T> rightResult = myIsBSTInner(node.getRight());
        CheckBSTResult<T> result = new CheckBSTResult<>(true, 1, node.getValue(), node.getValue(), node, node);
        if (leftResult != null) {
            result.minEl = leftResult.minEl;
            result.result &= leftResult.result;
            result.result &= leftResult.max.compareTo(node.getValue()) < 0;
            result.k += leftResult.k;
            if (!(leftResult.max.compareTo(node.getValue()) < 0)) {
                if (leftResult.maxEl.getLeft() != null || leftResult.maxEl.getRight() != null) {
                    result.k = 2;
                    return result;
                }
                int n = countMax(node, node.getLeft());
                if (n > 1) {
                    result.k = 2;
                    return result;
                }
                result.path.append(getPath(node, leftResult.maxEl, ""));
                result.k++;

            }
            if (!leftResult.result) {
                result.path.append("L");
                result.path.append(leftResult.path);
            }
            result.size += leftResult.size;
            result.min = leftResult.min;
        }
        if (rightResult != null) {
            result.result &= rightResult.result;
            result.maxEl = rightResult.maxEl;
            result.size += rightResult.size;
            result.result &= rightResult.min.compareTo(node.getValue()) > 0;//8 (3 (1 (5), 6), 10 (, 15 (12, 18)))
            result.k += rightResult.k;
            if (!(rightResult.min.compareTo(node.getValue()) > 0)) {

                if (rightResult.minEl.getLeft() != null || rightResult.minEl.getRight() != null) {
                    result.k = 2;
                    return result;
                }
                int n = countMin(node, node.getRight());
                if (n > 1) {
                    result.k = 2;
                    return result;
                }
                result.path.append(getPath(node, rightResult.minEl, ""));
                result.k++;
            }
            if (!rightResult.result) {
                result.path.append("R");
                result.path.append(rightResult.path);
            }
            result.max = rightResult.max;
        }
        return result;
    }

    private static <T extends Comparable<? super T>> int countMin(DefaultBinaryTree.TreeNode<T> root, DefaultBinaryTree.TreeNode<T> node) {
        int n = 0;
        if (node.getRight() != null) {
            n += countMin(root, node.getRight());//
        }
        if (node.getLeft() != null) {
            n += countMin(root, node.getLeft());//
        }
        if (!(node.getValue().compareTo(root.getValue()) > 0)) {
            n++;
        }
        return n;
    }

    private static <T extends Comparable<? super T>> int countMax(DefaultBinaryTree.TreeNode<T> root, DefaultBinaryTree.TreeNode<T> node) {
        int n = 0;
        if (node.getRight() != null) {
            n += countMin(root, node.getRight());
        }
        if (node.getLeft() != null) {
            n += countMin(root, node.getLeft());
        }
        if (!(node.getValue().compareTo(root.getValue()) < 0)) {
            n++;
        }
        return n;
    }

    private static <T extends Comparable<? super T>> String getPath(DefaultBinaryTree.TreeNode<T> node, DefaultBinaryTree.TreeNode<T> err, String c) {
        StringBuilder path = new StringBuilder();
        if (err != node) {
            if (node.getRight() != null) {
                if (!"".equals(getPath(node.getRight(), err, "R"))) {
                    path.append(c);
                }
                path.append(getPath(node.getRight(), err, "R"));
            }
            if (node.getLeft() != null) {
                if (!"".equals(getPath(node.getLeft(), err, "L"))) {
                    path.append(c);
                }
                path.append(getPath(node.getLeft(), err, "L"));
            }
            return path.toString();
        }
        path.append(c);
        if (err == node) {
            return path.toString();
        }
        return null;
    }
    
    /**
     * Рекурсивное добавление значения в поддерево node
     *
     * @param node Узел, в который (в него или его поддеревья) добавляем
     * значение value
     * @param value Добавляемое значение
     * @return Старое значение, равное value, если есть
     */
    private T put(SimpleTreeNode node, T value) {
        int cmp = value.compareTo(node.value);
        if (cmp == 0) {
            // в узле значение, равное value
            T oldValue = node.value;
            node.value = value;
            return oldValue;
        } else {
            if (cmp < 0) {
                if (node.left == null) {
                    node.left = new SimpleTreeNode(value);
                    size++;
                    return null;
                } else {
                    return put(node.left, value);
                }
            } else {
                if (node.right == null) {
                    node.right = new SimpleTreeNode(value);
                    size++;
                    return null;
                } else {
                    return put(node.right, value);
                }
            }
        }
    }
    
    /**
     * Рекурсивное удаления значения из поддерева node
     * 
     * @param node
     * @param nodeParent Родитель узла
     * @param value
     * @return Старое значение, равное value, если есть
     */
    private T remove(SimpleTreeNode node, SimpleTreeNode nodeParent, T value)
    {
        if (node == null) {
            return null;
        }
        int cmp = value.compareTo(node.value);
        if (cmp == 0) {
            // в узле значение, равное value
            T oldValue = node.value;
            if (node.left != null && node.right != null) {
                // если у node есть и левое и правое поддерево
                SimpleTreeNode minParent = getMinNodeParent(node.right);
                if (minParent == null) {
                    node.value = node.right.value;
                    node.right = node.right.right;
                } else {
                    node.value = minParent.left.value;
                    minParent.left = minParent.left.right;
                }
            } else {
                SimpleTreeNode child = (node.left != null) ? node.left : node.right;
                if (nodeParent == null) {
                    // возможно, если только node == root
                    root = child;
                } else if (nodeParent.left == node) {
                    nodeParent.left = child;
                } else {
                    nodeParent.right = child;
                }                    
            }
            size--;
            return oldValue;
        } else if (cmp < 0) 
            return remove(node.left, node, value);
        else {
            return remove(node.right, node, value);
        }
    }
    
    /**
     * Поиск родителя минимально TreeNode в поддереве node
     *
     * @param node Поддерево в котором надо искать родителя минимального элемент
     * @return Узел, содержащий минимальный элемент
     */
    private SimpleTreeNode getMinNodeParent(SimpleTreeNode node) {
        if (node == null) {
            return null;
        }
        SimpleTreeNode parent = null;
        for (; node.left != null; node = node.left) {
            parent = node;
        }
        return parent;
    }
    
    // Реализация BSTree<T>
    
    @Override
    public T put(T value) {
        if (root == null) {
            root = new SimpleTreeNode(value);
            size++;
            return null;
        }
        return put(root, value);
    }

    @Override
    public T remove(T value) {
        return remove(root, null, value);
    }

    @Override
    public int size() {
        return size;
    }
}
