
public class Clock extends Thread {
    public static Clock instance;
    
    public static void factory(Memory mainMemory, CPU mainProcessor, Object onFinish) {
	if (instance == null) {
	    instance = new Clock();
	    instance.mainMemory = mainMemory;
	    instance.mainProcessor = mainProcessor;
	    instance.onFinish = onFinish;
	}
    }

    public int cpuCounter;
    public int nullTime;
    private Memory mainMemory;
    private CPU mainProcessor;
    private Object onFinish;

    @Override
    public void run() {
	while (!mainMemory.areQueuesEmpty() || mainProcessor.runningProcess != null) {
	    if (cpuCounter % 200 == 0) {
		synchronized (mainMemory.lock) {
		    mainMemory.lock.notify();
		}
	    }
	    while (!mainProcessor.excuting)

		synchronized (mainProcessor.lock) {
//		    System.out.println("notifying cpu");
		    mainProcessor.lock.notifyAll();
//		    System.out.println("notified cpu");

		}
	    while (mainProcessor.excuting)

		synchronized (mainProcessor.lock) {
//		    System.out.println("cycle waiting");
		    try {
			mainProcessor.lock.wait(1);
		    } catch (InterruptedException e) {
			e.printStackTrace();
		    }
//		    System.out.println("cycle waiting done");

		}

	    cpuCounter++;
	}
	synchronized (onFinish) {
	    onFinish.notifyAll();
	}
	super.run();
    }
}
