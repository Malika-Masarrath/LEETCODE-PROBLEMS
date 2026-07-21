class Solution {
    public int lengthOfLastWord(String s) {
        s = s.trim(); 
        int lastSpace = s.lastIndexOf(' '); 
        return s.length() - lastSpace - 1;  // Step 3: length of last word
    }
}
