package club.ccit.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListUtils {
    /**
     * 根据指定的谓词过滤列表，并将过滤后的元素收集到一个新的列表中
     *
     * @param list      要过滤的列表
     * @param predicate 用于过滤的谓词
     * @return 过滤后的元素列表
     */
    public static <T> List<T> filterAndCollect(List<T> list, Predicate<T> predicate) {
        // 将列表转换为流
        return list.stream()
                // 根据谓词过滤流中的元素
                .filter(predicate)
                // 将过滤后的流收集到一个新的列表中
                .collect(Collectors.toList());
    }

    /**
     * 向列表中添加元素，如果列表为空，则创建一个新的列表
     *
     * @param list      要添加元素的列表
     * @param elements  要添加的元素
     * @param predicate 用于检查列表是否为空的谓词
     * @return 添加元素后的列表
     */
    public static <T> List<T> addAndCollect(List<T> list, T elements, Predicate<T> predicate) {
        if (list == null) {
            list = new ArrayList<>();
        }
        // 使用 noneMatch 方法检查列表中是否没有元素满足谓词条件
        if (list.stream().noneMatch(predicate)) {
            list.add(elements);
        }
        return list;
    }

    /**
     * 查找并返回列表中满足指定谓词的元素
     *
     * @param list      要搜索的元素列表
     * @param predicate 用于测试元素的谓词
     * @return 一个包含满足谓词的元素 T，如果没有找到这样的元素，则返回一个空
     */
    public static <T> T findElement(List<T> list, Predicate<T> predicate) {
        for (int i = 0; i < list.size(); i++) {
            if (predicate.test(list.get(i))) {
                return list.get(i);
            }
        }
        return null;
    }

    /**
     * 查找并返回满足指定谓词的元素列表
     *
     * @param list      要搜索的元素列表
     * @param predicate 用于测试元素的谓词
     * @return 一个包含谓词的元素的列表
     */
    public static <T> List<T> findElements(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (predicate.test(list.get(i))) {
                result.add(list.get(i));
            }
        }
        return result;
    }

    /**
     * 将两个列表合并为一个新的列表
     *
     * @param firstList 第一个列表
     * @param endList   第二个列表
     * @return 合并后的列表
     */
    public static <T> List<T> mergeLists(List<T> firstList, List<T> endList) {
        // 使用addAll方法将第二个列表合并到第一个列表中
        firstList.addAll(endList);
        // 返回合并后的列表
        return firstList;
    }

    /**
     * 从列表中移除指定元素
     *
     * @param list      要移除元素的列表
     * @param predicate 要移除的元素的条件
     * @return 移除元素后的列表
     */
    public static <T> List<T> removeIf(List<T> list, Predicate<T> predicate) {
        // 创建一个新列表来存储移除元素后的结果
        List<T> result = new ArrayList<>(list);
        // 使用removeIf方法根据条件移除元素
        result.removeIf(predicate);
        // 返回移除元素后的列表
        return result;
    }

    /**
     * 查找列表中指定元素的索引
     *
     * @param list      要查找的列表
     * @param predicate 要查找的元素条件
     * @return 元素在列表中的索引，如果未找到则返回-1
     */
    public static <T> int indexOf(List<T> list, Predicate<T> predicate) {
        for (int i = 0; i < list.size(); i++) {
            if (predicate.test(list.get(i))) {
                return i;
            }
        }
        // 如果没有找到满足条件的元素，返回-1
        return -1;
    }

    /**
     * 判断列表是否为空
     *
     * @param list 要检查的列表
     * @return 如果列表为空则返回true，否则返回false
     */
    public static <T> boolean isEmpty(List<T> list) {
        // 使用isEmpty方法检查列表是否为空
        return (list == null || list.isEmpty());
    }
}