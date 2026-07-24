class Solution {
    public boolean detectCapitalUse(String word) {
        int capitals = 0;
        for (char c : word.toCharArray()) {
            if (Character.isUpperCase(c)) {
                capitals++;
            }
        }
        if (capitals == word.length()) return true;
        if (capitals == 0) return true;
        return capitals == 1 && Character.isUpperCase(word.charAt(0));
    }
}
