import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Memory extends Thread {

    public PriorityQueue<Process> readyQueue;
    public Queue<Process> waitingQueue;
    public Queue<Process> allocationQueue;
    public List<Process> finishedProcesses;
    public int size;
    public Object lock = new Object();

    public Memory(Queue<Process> allocationQueue, int size) {
	this.allocationQueue = allocationQueue;
	this.waitingQueue = new LinkedList<Process>();
	this.readyQueue = new PriorityQueue<Process>();
	this.finishedProcesses = new LinkedList<Process>();
	this.size = 0;
    }

    @Override
    public void run() {
//	while (!areQueuesEmpty()) {
	while (true) {
	    synchronized (lock) {
		try {
		    lock.wait();
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		longTermScheduele();
	    }
	}
    }

    public void longTermScheduele() {
	synchronized (lock) {
	    if (allocationQueue.isEmpty()) {
		return;
	    }

	    while (!allocationQueue.isEmpty() && allocationQueue.peek().memoryUsage.get(0) + size < 0.85 * 1024) {
		Process process = allocationQueue.poll();
		process.setState(ProcessState.READY);
		readyQueue.add(process);
		size += process.memoryUsage.get(0);
	    }
	}
    }

    public boolean areQueuesEmpty() {
	synchronized (lock) {

	    return allocationQueue.isEmpty() && waitingQueue.isEmpty() && readyQueue.isEmpty();
	}
    }

    public void decrementWaitingTime(int time) {
	synchronized (lock) {

	    Iterator<Process> queue = waitingQueue.iterator();

	    while (queue.hasNext()) {
		Process p = queue.next();
		int burst = p.IOBursts.get(p.getIOCounter() - 1).intValue();

		if (burst > 0) {
		    p.IOBursts.set(p.getIOCounter() - 1, --burst);
		    p.addToTotalIOTime(time);
		}
		if (size + p.memoryUsage.get(p.getIOCounter()) < 1024 && burst == 0) {
		    p.setState(ProcessState.READY);
		    queue.remove();
		    readyQueue.add(p);

		    size += p.memoryUsage.get(p.getIOCounter());
		} else {
		    p.incrementMemoryWaitCounter();
		}
	    }
	}
    }

    public synchronized void terminateLargestWaitingProcess() {
	Iterator<Process> waitingQueueIterator = this.waitingQueue.iterator();
	Process largestWaitingProcess = null;

	while (waitingQueueIterator.hasNext()) {
	    Process currentProcess = waitingQueueIterator.next();
	    if (largestWaitingProcess == null || largestWaitingProcess.memoryUsage.get(largestWaitingProcess.getIOCounter() - 1) < currentProcess.memoryUsage.get(currentProcess.getIOCounter() - 1)) {
		largestWaitingProcess = currentProcess;
	    }
	}

	reverseMemoryAllocation(largestWaitingProcess);
	largestWaitingProcess.setState(ProcessState.KILLED);
	largestWaitingProcess.setTerminationKillTime(Clock.instance.cpuCounter);
	finishedProcesses.add(largestWaitingProcess);
	waitingQueue.remove(largestWaitingProcess);
    }

    public synchronized void reverseMemoryAllocation(Process p) {
	if (p.getState() == ProcessState.TERMINATED) {
	    for (int i = 0; i < p.memoryUsage.size(); i++) {
		size -= p.memoryUsage.get(i);
	    }
	} else {
	    for (int i = 0; i < p.getIOCounter(); i++) {
		size -= p.memoryUsage.get(i);
	    }
	}
    }
}
