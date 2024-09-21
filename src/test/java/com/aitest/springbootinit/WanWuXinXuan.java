package com.aitest.springbootinit;

import java.util.HashMap;
import java.util.Map;

class WanWuXinXuan {
    public int[] twoSum(int[] nums, int target) {
        // 创建哈希表
        Map<Integer, Integer> map = new HashMap<>();
        // 遍历数组
        for(int i = 0; i < nums.length; i++) {
            // 判断当前数字的补数是否已在哈希表中
            if(map.containsKey(target - nums[i])) {
                // 如果存在，返回两个数的索引
                return new int[] {map.get(target - nums[i]), i};
            }
            // 将当前数字存入哈希表
            map.put(nums[i], i);
        }
        // 如果没有找到符合条件的两个数，抛出异常
        throw new IllegalArgumentException("No two sum solution");
    }
    public static void main(String[] args) {
        WanWuXinXuan wanWuXinXuan = new WanWuXinXuan();
        int[] nums = {2, 0, 1, 8};
        int target = 3;
        
        int[] result = wanWuXinXuan.twoSum(nums, target);
        System.out.println("两个数的索引为: " + result[0] + ", " + result[1]);
    }
}
