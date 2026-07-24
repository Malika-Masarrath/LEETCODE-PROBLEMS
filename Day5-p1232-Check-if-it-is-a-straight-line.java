 class Solution {
    public boolean checkStraightLine(int[][] coordinates) {
        if (coordinates.length <= 2) return true;

        int x0 = coordinates[0][0], y0 = coordinates[0][1];
        int x1 = coordinates[1][0], y1 = coordinates[1][1];

        int dx = x1 - x0;
        int dy = y1 - y0;

        for (int i = 2; i < coordinates.length; i++) {
            int x = coordinates[i][0], y = coordinates[i][1];
            int dxCurr = x - x0;
            int dyCurr = y - y0;

            if (dx * dyCurr - dy * dxCurr != 0) {
                return false;
            }
        }
        return true;
    }
}
 
 
 
