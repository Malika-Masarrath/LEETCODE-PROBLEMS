import java.math.BigInteger;

class Solution {
    public String multiply(String num1, String num2) {
        // Convert strings to BigInteger
        BigInteger a = new BigInteger(num1);
        BigInteger b = new BigInteger(num2);

        // Multiply safely
        BigInteger mult = a.multiply(b);

        // Return result as string
        return mult.toString();
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        System.out.println(sol.multiply("123456789", "987654321")); 
        // Expected: 121932631112635269
    }
}
