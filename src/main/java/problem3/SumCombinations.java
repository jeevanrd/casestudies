package problem3;

import java.util.Arrays;

public class SumCombinations {
    static final int[] numbers = new int[]{1,2,3,4,5};
    static final int count = 6;
    static int counter = 0;
    public static void main(String args[]) {
        int len = numbers.length;
        for(int n=1;n<=len;n++) {
            int data[]=new int[n];
            combinations(numbers, data, 0, len-1, 0, n);
        }
    }

    static void combinations(int arr[], int data[], int start, int end, int index, int r) {
        if (index == r) {
            getSum(data, count);
            return;
        }

        for (int i=start; i<= end && end-i+1 >= r-index; i++) {
            data[index] = arr[i];
            combinations(arr, data, i+1, end, index+1, r);
        }
    }


    public static void getSum(int[] nums, int total) {
        int sum = 0;
        for(int i=0;i<nums.length;i++) {
            sum += nums[i];
        }
        if(sum == total) {
            System.out.println(Arrays.toString(nums));
        }
    }
}
