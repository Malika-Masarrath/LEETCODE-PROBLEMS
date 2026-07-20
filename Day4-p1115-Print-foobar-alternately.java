import java.util.concurrent.Semaphore;

class FooBar {
    private int n;
    private Semaphore fooSem = new Semaphore(1);
    private Semaphore barSem = new Semaphore(0);

    public FooBar(int n) {
        this.n = n;
    }

    public void foo(Runnable printFoo) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            fooSem.acquire();       // wait for foo turn
            printFoo.run();         // print "foo"
            barSem.release();       // allow bar
        }
    }

    public void bar(Runnable printBar) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            barSem.acquire();       // wait for bar turn
            printBar.run();         // print "bar"
            fooSem.release();       // allow foo
        }
    }
}
