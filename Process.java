import java.util.*;

public class Process implements Comparable<Process> {
	private static int idCounter = 0;
	// class attributes
	private ProcessState state;
	private String name;
	private int id;
	private int arrivalTime;
	public List<Integer> CPUBursts;
	public List<Integer> IOBursts;
	public List<Integer> memoryUsage;
	private int readyQueueEntryTime = -1;
	private int CPUCounter;
	private int IOTotalCounter;
	private int IOTotalTime;
	private int memoryWaitCounter;
	private int terminationKillTime;
	private int preemptionCounter;
	private int totalCPUTime;

	public Process() {

	}

	// constructor
	public Process(String name,ArrayList<Integer> CPUBursts,ArrayList<Integer> IOBursts,ArrayList<Integer> memoryUsage) {
		this.name = name;
		this.CPUBursts = CPUBursts;
		this.IOBursts = IOBursts;
		this.memoryUsage = memoryUsage;
		this.id = idCounter++;		
	}

	public int getCPUBurstTime() {
		int length = this.CPUBursts.size();
		int sum_time = 0;
		for (int i = 0; i < length; i++)
			sum_time += this.CPUBursts.get(i);
		return sum_time;
	}

	public void setReadyQueueEmtryTime(int t) {
		this.readyQueueEntryTime = t;
	}

	public void setState(ProcessState p) {
		this.state = p;
	}

	public void incrementCPUCounter() {
		this.CPUCounter++;
	}

	public void incrementIOCounter() {
		this.IOTotalCounter++;
	}

	public int getIOCounter() {
		return IOTotalCounter;
	}

	public void incrementMemoryWaitCounter() {
		this.memoryWaitCounter++;
	}

	public void incrementPreemptionCounter() {
		this.preemptionCounter++;
	}

	public void addToTotalIOTime(int time) {
		this.IOTotalTime += time;
	}

	public void incrementCPUTime() {
		this.totalCPUTime++;
	}

	public ProcessState getState() {
		return state;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}


	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public List<Integer> getCPUBursts() {
		return CPUBursts;
	}

	public void setCPUBursts(List<Integer> CPUBursts) {
		this.CPUBursts = CPUBursts;
	}

	public List<Integer> getIOBursts() {
		return IOBursts;
	}

	public void setIOBursts(List<Integer> IOBursts) {
		this.IOBursts = IOBursts;
	}

	public List<Integer> getMemoryUsage() {
		return memoryUsage;
	}

	public void setMemoryUsage(List<Integer> memoryUsage) {
		this.memoryUsage = memoryUsage;
	}

	public int getReadyQueueEntryTime() {
		return readyQueueEntryTime;
	}

	public void setReadyQueueEntryTime(int readyQueueEntryTime) {
		this.readyQueueEntryTime = readyQueueEntryTime;
	}

	public int getCPUCounter() {
		return CPUCounter;
	}

	public void setCPUCounter(int CPUCounter) {
		this.CPUCounter = CPUCounter;
	}

	public int getIOTotalCounter() {
		return IOTotalCounter;
	}

	public void setIOTotalCounter(int IOTotalCounter) {
		this.IOTotalCounter = IOTotalCounter;
	}

	public int getIOTotalTime() {
		return IOTotalTime;
	}

	public void setIOTotalTime(int IOTotalTime) {
		this.IOTotalTime = IOTotalTime;
	}

	public int getMemoryWaitCounter() {
		return memoryWaitCounter;
	}

	public void setMemoryWaitCounter(int memoryWaitCounter) {
		this.memoryWaitCounter = memoryWaitCounter;
	}

	public int getTerminationKillTime() {
		return terminationKillTime;
	}

	public void setTerminationKillTime(int terminationKillTime) {
		this.terminationKillTime = terminationKillTime;
	}

	public int getPreemptionCounter() {
		return preemptionCounter;
	}

	public void setPreemptionCounter(int preemptionCounter) {
		this.preemptionCounter = preemptionCounter;
	}
 
	public int getTotalCPUTime() {
		return totalCPUTime;
	}

	@Override
	public String toString() {
		return name + " " + arrivalTime + " " + CPUBursts.toString() + " " + memoryUsage.toString() + " " + IOBursts.toString();
	}

	@Override
	public synchronized int compareTo(Process o) {
		return this.CPUBursts.get(this.getIOCounter()) - o.CPUBursts.get(o.getIOCounter());
	}
}
