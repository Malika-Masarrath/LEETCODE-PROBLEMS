import java.util.*;
class Solution {
    public String[] findWords(String[] words) {
        String row1 = "qwertyuiop";
        String row2 = "asdfghjkl";
        String row3 = "zxcvbnm";
        Set<Character> set1 = toSet(row1);
        Set<Character> set2 = toSet(row2);
        Set<Character> set3 = toSet(row3);
        List<String> result = new ArrayList<>();
        for (String word : words) {
            if (canType(word.toLowerCase(), set1) ||
                canType(word.toLowerCase(), set2) ||
                canType(word.toLowerCase(), set3)) {
                result.add(word);
            }
        }
        return result.toArray(new String[0]);
    }
    private Set<Character> toSet(String row) {
        Set<Character> set = new HashSet<>();
        for (char c : row.toCharArray()) {
            set.add(c);
        }
        return set;
    }
    private boolean canType(String word, Set<Character> row) {
        for (char c : word.toCharArray()) {
            if (!row.contains(c)) return false;
        }
        return true;
    }
}
