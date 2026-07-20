class Solution {
    public boolean isPalindrome(int x) {
        // negative numbers are not palindromes
        if (x < 0) return false;

        int original = x;
        int reversed = 0;

        while (x != 0) {
            int digit = x % 10;   // extract last digit
            x /= 10;              // remove last digit

            // check overflow before updating reversed
            if (reversed > Integer.MAX_VALUE / 10 || 
               (reversed == Integer.MAX_VALUE / 10 && digit > 7)) {
                return false; // overflow means not palindrome
            }

            reversed = reversed * 10 + digit;
        }

        return original == reversed;
    }
}

