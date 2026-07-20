import java.util.function.IntConsumer;

class FizzBuzz {
    private int n;
    private int current = 1;

    public FizzBuzz(int n) {
        this.n = n;
    }

    // printFizz.run() outputs "fizz".
    public void fizz(Runnable printFizz) throws InterruptedException {
        while (true) {
            synchronized (this) {
                while (current <= n && !(current % 3 == 0 && current % 5 != 0)) {
                    wait();
                }
                if (current > n) {
                    notifyAll();
                    break;
                }
                printFizz.run();
                current++;
                notifyAll();
            }
        }
    }

    // printBuzz.run() outputs "buzz".
    public void buzz(Runnable printBuzz) throws InterruptedException {
        while (true) {
            synchronized (this) {
                while (current <= n && !(current % 5 == 0 && current % 3 != 0)) {
                    wait();
                }
                if (current > n) {
                    notifyAll();
                    break;
                }
                printBuzz.run();
                current++;
                notifyAll();
            }
        }
    }

    // printFizzBuzz.run() outputs "fizzbuzz".
    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        while (true) {
            synchronized (this) {
                while (current <= n && !(current % 15 == 0)) {
                    wait();
                }
                if (current > n) {
                    notifyAll();
                    break;
                }
                printFizzBuzz.run();
                current++;
                notifyAll();
            }
        }
    }

    // printNumber.accept(x) outputs "x".
    public void number(IntConsumer printNumber) throws InterruptedException {
        while (true) {
            synchronized (this) {
                while (current <= n && (current % 3 == 0 || current % 5 == 0)) {
                    wait();
                }
                if (current > n) {
                    notifyAll();
                    break;
                }
                printNumber.accept(current);
                current++;
                notifyAll();
            }
        }
    }
} // ✅ final closing brace for the class
