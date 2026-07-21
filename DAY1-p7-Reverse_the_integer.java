class Solution {
    public int reverse(int x) {
        long rev = 0; // use long to detect overflow
        while (x != 0) {
            int digit = x % 10;   // extract last digit
            rev = rev * 10 + digit; // build reversed number
            x /= 10;              // remove last digit
        }
        // check overflow
        if (rev < Integer.MIN_VALUE || rev > Integer.MAX_VALUE) {
            return 0;
        }
        return (int) rev;
    }
}
