package problem3;

import java.util.Arrays;

public class SumCombinations {
    static final int[] numbers = new int[]{1,2,3,4,5};
    static final int count = 10;
    public static void main(String args[]) {
        int len = numbers.length;
        for(int n=0;n<len;n++) {
            printCombination(numbers, len, n);
        }
    }

    static void combinationUtil(int arr[], int data[], int start, int end, int index, int r) {
        if (index == r) {
            getSum(Arrays.copyOfRange(data,0,r), count);
            return;
        }

        for (int i=start; i<= end && end-i+1 >= r-index; i++) {
            data[index] = arr[i];
            combinationUtil(arr, data, i+1, end, index+1, r);
        }
    }

    static void printCombination(int arr[], int n, int r) {
        int data[]=new int[r];
        combinationUtil(arr, data, 0, n-1, 0, r);
    }

    public static void getSum(int[] nums, int total) {
        int sum = 0;
        for(int i=0;i<nums.length;i++) {
            sum += nums[i];
        }
        if(sum == total) {
            System.out.print(Arrays.toString(nums));
        }
    }
}
