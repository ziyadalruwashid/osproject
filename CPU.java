import java.util.Iterator;

public class CPU extends Thread {
    public Process runningProcess;
    private Memory m;
    public boolean excuting = false;
    public Object lock = new Object();

    public CPU(Memory m) {
	this.m = m;
    }

    @Override
    public void run() {

//	while (!m.areQueuesEmpty() || runningProcess != null) {
	while (true) {
	    synchronized (lock) {
		try {
//		    System.out.println("cpu waiting");
		    lock.wait();
//		    System.out.println("cpu waiting done");

		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }

	    excuting = true;
	    simulateMachineExecuteCycle();
	    excuting = false;

	    synchronized (lock) {
//		System.out.println("cycle notifing");
		lock.notifyAll();
//		System.out.println("cycle notified");

	    }
	}
    }

    private void simulateMachineExecuteCycle() {

	// System.out.println(Clock.instance.cpuCounter);
	// System.out.println(m.readyQueue);
	// System.out.println(m.waitingQueue);
	Clock.instance.cpuCounter++;

	if (runningProcess == null) {
	    Process p = getFromReadyQueue();

	    if (p == null) {
		m.decrementWaitingTime(1);
		Clock.instance.nullTime++;
		if (isDeadlock()) {
		    int memSize = m.size;
		    m.terminateLargestWaitingProcess();
		    System.out.println(memSize + " " + m.size);
		    System.out.println("DEADLOCK! deleting largest waiting process");
		}
		System.out.println(Clock.instance.cpuCounter + " " + m.waitingQueue.size() + " " + m.readyQueue.size() + " " + m.allocationQueue.size() + " "
			+ runningProcess + " " + m.size);
		return;
	    }
	    runningProcess = p;
	}
	System.out.println(Clock.instance.cpuCounter + " " + m.waitingQueue.size() + " " + m.readyQueue.size() + " " + m.allocationQueue.size() + " "
		+ runningProcess + " " + m.size);
	if (runningProcess.CPUBursts.get(runningProcess.getIOCounter()) == 0) {
	    // System.out.println(runningProcess);

	    if (runningProcess.getIOCounter() == runningProcess.IOBursts.size()) {
		runningProcess.setState(ProcessState.TERMINATED);
		runningProcess.setTerminationKillTime(Clock.instance.cpuCounter);
		m.reverseMemoryAllocation(runningProcess);
		m.finishedProcesses.add(runningProcess);
	    } else {
		runningProcess.setState(ProcessState.WAITING);
		runningProcess.incrementIOCounter();
		m.waitingQueue.add(runningProcess);
	    }

	    runningProcess = null;

	} else if (checkForPreemption()) {
	    runningProcess.incrementPreemptionCounter();
	    runningProcess.setState(ProcessState.WAITING);
	    m.readyQueue.add(runningProcess);
	    runningProcess = getFromReadyQueue();
	}

	m.decrementWaitingTime(1);

	executeRunningProcess(1);

    }

    public void incrementCPUTime(int delta) {
	Clock.instance.cpuCounter += delta;
	try {
//	    Thread.sleep(delta);
	} catch (Exception e) {
	}
    }

    private Process getFromReadyQueue() {
	synchronized (m.lock) {

	    if (m.readyQueue.isEmpty())
		return null;
	    Process cp = (m.readyQueue.poll());
	    cp.incrementCPUCounter();
	    cp.setState(ProcessState.RUNNING);
	    cp.setReadyQueueEmtryTime(Clock.instance.cpuCounter);
	    return cp;
	}
    }

    private boolean checkForPreemption() {
	Process peek = m.readyQueue.peek();
	if (peek != null && peek.CPUBursts.get(peek.getIOCounter()) < runningProcess.CPUBursts
		.get(runningProcess.getIOCounter())) {
	    return true;
	}
	return false;
    }

    private synchronized void executeRunningProcess(int time) {
	if (runningProcess == null) {
	    return;
	}
	int i = runningProcess.getIOCounter();
	runningProcess.CPUBursts.set(i, runningProcess.CPUBursts.get(i) - time);
	runningProcess.incrementCPUTime();
    }

    public boolean isDeadlock() {

	if (runningProcess != null)
	    return false;
	synchronized (lock) {
	    if (!m.readyQueue.isEmpty())
		return false;
	}
	if (m.waitingQueue.isEmpty())
	    return false;
	Iterator<Process> waitingIterator = m.waitingQueue.iterator();

	while (waitingIterator.hasNext()) {
	    Process p = waitingIterator.next();
	    if (p.IOBursts.get(p.getIOCounter() - 1) != 0)
		return false;
	}

	return true;

    }

}
