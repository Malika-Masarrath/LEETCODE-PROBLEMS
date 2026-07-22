class Solution {
    public String addStrings(String num1, String num2) {
        int i = num1.length() - 1;
        int j = num2.length() - 1;
        int carry = 0;
        StringBuilder result = new StringBuilder();
        for (; i >= 0 || j >= 0 || carry != 0; i--, j--) {
            int x = (i >= 0) ? num1.charAt(i) - '0' : 0;
            int y = (j >= 0) ? num2.charAt(j) - '0' : 0;
            int sum = x + y + carry;
            result.append(sum % 10);
            carry = sum / 10;
        }
        return result.reverse().toString();
    }
}
