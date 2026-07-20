lass Solution {
    public int findNumbers(int[] nums) {
        int count = 0;
        for (int num : nums) {
            int digits = digitCount(num);
            if (digits % 2 == 0) {
                count++;
            }
        }
        return count;
    }

    // Helper method to count digits without converting to string
    private int digitCount(int num) {
        int digits = 0;
        while (num > 0) {
            num /= 10;
            digits++;
        }
        return digits;
    }
}
