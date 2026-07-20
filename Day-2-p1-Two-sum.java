import java.util.HashMap;
import java.util.Map;

class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>(); // value → index

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];

            // check if complement already exists
            if (map.containsKey(complement)) {
                return new int[] { map.get(complement), i };
            }

            // store current value with its index
            map.put(nums[i], i);
        }

        // problem guarantees one solution, but return empty if not found
        return new int[] {};
    }
}

