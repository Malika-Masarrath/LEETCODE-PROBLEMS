class Solution {
    public int lengthOfLastWord(String s) {
        s = s.trim(); // remove leading/trailing spaces
        int lastSpace = s.lastIndexOf(' ');
        return s.length() - lastSpace - 1;
    }
}
