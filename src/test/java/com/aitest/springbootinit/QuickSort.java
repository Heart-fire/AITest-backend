package com.aitest.springbootinit;
public class QuickSort {
    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high); // 获取基准元素的位置
            quickSort(arr, low, pi - 1);  // 递归排序基准左侧
            quickSort(arr, pi + 1, high); // 递归排序基准右侧
        }
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high]; // 选择最后一个元素作为基准
        int i = (low - 1);     // i是小于基准的元素的索引
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) { // 将小于或等于基准的元素移动到左侧
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high); // 将基准元素放置到正确的位置
        return i + 1;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {
        int[] arr = {10, 7, 8, 9, 1, 5};
        //           0   1  2  3  4  5
        int n = arr.length;
        quickSort(arr, 0, n - 1);
        System.out.println("快速排序后:");
        // 遍历排序后的数组
        for (int num : arr) {
            System.out.print(num + " ");
        }
    }
}
