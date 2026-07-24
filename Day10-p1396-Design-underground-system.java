import java.util.*;

class UndergroundSystem {
    private Map<Integer, String> inStation = new HashMap<>();
    private Map<Integer, Integer> inTime = new HashMap<>();
    private Map<String, double[]> stats = new HashMap<>();
    
    public void checkIn(int id, String stationName, int t) {
        inStation.put(id, stationName);
        inTime.put(id, t);
    }
    
    public void checkOut(int id, String stationName, int t) {
        String route = inStation.get(id) + "-" + stationName;
        int travel = t - inTime.get(id);
        
        stats.putIfAbsent(route, new double[2]); 
        stats.get(route)[0] += travel;
        stats.get(route)[1] += 1;
        
        inStation.remove(id);
        inTime.remove(id);
    }
    
    public double getAverageTime(String startStation, String endStation) {
        double[] data = stats.get(startStation + "-" + endStation);
        return data[0] / data[1];
    }
}
