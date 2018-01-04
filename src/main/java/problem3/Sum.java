package problem3;

import java.util.Arrays;

public class Sum {
    static final int[] numbers = new int[]{3,4,1,-1,-3};
    static final int count = 5;
    public static void main(String args[]) {
        int len = numbers.length;
        for(int i=0;i<len;i++) {
            for(int j=i+1;j<=len;j++){
                getSum(Arrays.copyOfRange(numbers,i,j), count);
            }
        }
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
