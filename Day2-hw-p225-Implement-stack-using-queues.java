import java.util.LinkedList;
import java.util.Queue;

class MyStack {
    private Queue<Integer> q1;
    private Queue<Integer> q2;

    // constructor
    public MyStack() {
        q1 = new LinkedList<>();
        q2 = new LinkedList<>();
    }

    // push element onto stack
    public void push(int x) {
        // step 1: add to q2
        q2.offer(x);

        // step 2: move all elements from q1 to q2
        while (!q1.isEmpty()) {
            q2.offer(q1.poll());
        }

        // step 3: swap q1 and q2
        Queue<Integer> temp = q1;
        q1 = q2;
        q2 = temp;
    }

    // removes the element on top of the stack
    public int pop() {
        return q1.poll();
    }

    // get the top element
    public int top() {
        return q1.peek();
    }

    // check if stack is empty
    public boolean empty() {
        return q1.isEmpty();
    }
}

