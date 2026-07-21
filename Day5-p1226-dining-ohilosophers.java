import java.util.concurrent.Semaphore;

class DiningPhilosophers {
    // 5 forks represented by semaphores
    private Semaphore[] forks = new Semaphore[5];

    public DiningPhilosophers() {
        for (int i = 0; i < 5; i++) {
            forks[i] = new Semaphore(1); // each fork can be held by one philosopher
        }
    }

    public void wantsToEat(int philosopher,
                           Runnable pickLeftFork,
                           Runnable pickRightFork,
                           Runnable eat,
                           Runnable putLeftFork,
                           Runnable putRightFork) throws InterruptedException {
        int left = philosopher;
        int right = (philosopher + 1) % 5;

        // To avoid deadlock, always pick the lower-numbered fork first
        int first = Math.min(left, right);
        int second = Math.max(left, right);

        forks[first].acquire();
        forks[second].acquire();

        // Actions
        pickLeftFork.run();
        pickRightFork.run();
        eat.run();
        putRightFork.run();
        putLeftFork.run();

        // Release forks
        forks[second].release();
        forks[first].release();
    }
}
