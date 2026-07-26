

class Foo {
    private final Object lock = new Object();
    private int state = 1;
 public Foo() {
 }
 public void first(Runnable printFirst) throws InterruptedException {
        synchronized (lock) {
         printFirst.run();
          state = 2;
          lock.notifyAll();
        }
	}
 public void second(Runnable printSecond) throws InterruptedException {
        synchronized (lock) {
            while (state != 2) {
            	lock.wait();
            }
            printSecond.run();
            state = 3;
            lock.notifyAll();
        }
	}
 public void third(Runnable printThird) throws InterruptedException {
        synchronized (lock) {
            while (state != 3) {
                lock.wait();
            }
            printThird.run();
        }
	}
}

