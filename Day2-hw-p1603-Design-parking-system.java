class ParkingSystem {
    // fields to track available slots
    private int big;
    private int medium;
    private int small;

    // constructor initializes counts
    public ParkingSystem(int big, int medium, int small) {
        this.big = big;
        this.medium = medium;
        this.small = small;
    }

    // method to add a car of given type
    public boolean addCar(int carType) {
        if (carType == 1) {          // big car
            if (big > 0) {
                big--;
                return true;
            }
        } else if (carType == 2) {   // medium car
            if (medium > 0) {
                medium--;
                return true;
            }
        } else if (carType == 3) {   // small car
            if (small > 0) {
                small--;
                return true;
            }
        }
        return false; // no space available
    }
}

