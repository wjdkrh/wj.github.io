package com.atguigu.yygh.t;

import io.swagger.models.auth.In;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName Demo * @Description TODO
 * @Author ehdk
 * @Date 11:32 2022/7/28
 * @Version 1.0
 **/
public class Demo {
    @Test
    public void t1() {
        ArrayList<Object> list = new ArrayList<>();

    }

    @Test
    public int[] twoSum(int[] nums, int target) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    list.add(i);
                    list.add(j);
                }
            }
        }
        int[] index = new int[list.size()];
        for (int i = 0; i < index.length; i++) {
            index[i] = list.get(i);
        }
        return index;

    }

    @Test
    public void t2() {
        int[] nums = {1, 2, 3, 5, 6, 7, 8, 9};
        int[] ints = this.twoSum(nums, 10);
        System.out.println(Arrays.toString(ints));
    }


    @Test
    public int[] twoSum1(int[] nums, int target) {
        //新建哈希表，用于存储数据；key为下表为i的值，value为数组的下标
        Map<Integer, Integer> map = new HashMap<>();

        //for循环遍历数组，先判断哈希表中是否存在target-i，若不存在将当前i的值和下标i存入哈希表
        for (int i = 0; i < nums.length; i++) {
            int cur = nums[i];
            if (map.containsKey(target - cur)) {
                return new int[]{i, map.get(target - cur)};
            }
            map.put(cur, i);
        }
        //哈希表遍历完都没返回，说明数组中不存在和为target的两个数
        return new int[]{0};
    }

    @Test
    public void t3() {
        int[] a = {1, 2, 3};
        this.twoSum1(a, 4);
    }

    public int[] twoSum2(int[] nums, int target) {
        HashMap<Integer, Integer> hashMap = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int cur = nums[i];
            if (hashMap.containsKey(target - cur)) {
                return new int[]{i, hashMap.get(target - cur)};
            }

            hashMap.put(cur, i);

        }
        return new int[]{0};
    }

    @Test
    public void t4() {
        int[] nums = {6, 5, 3, 1, 8, 7, 2, 4};
        int count = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            int changecount = 0;
            for (int j = i + 1; j < nums.length; j++) {
                count++;
                if (nums[i] > nums[j]) {
                    int tep = nums[i];
                    nums[i] = nums[j];
                    nums[j] = tep;
                    changecount++;
                }
            }
            if (changecount == 0) {
                break;
            }
            System.out.println(Arrays.toString(nums));
        }

        System.out.println(count);
    }

    public int search(int[] nums, int target) {
        int left = 0;
        int right = nums.length;
        while (left <= right) {
            int mid = (right-left) / 2+left;
            if (nums[mid] > target) {
                right = mid - 1;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else if(nums[mid] == target) {
                return mid;
            }
                return -1;

        }
        return -1;
    }


    @Test
    public void t9() {
        int[] nums = {-1, 0, 3, 5, 9, 12};
        int search = this.search(nums, 13);
        System.out.println(search);
    }



}
