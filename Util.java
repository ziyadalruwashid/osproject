import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Util {
    static Random random = new Random();

    public static void main(String[] args) {
	File f = new File("input.txt");
	try {
	    writeFile(1000);

	    Queue<Process> readFile = readFile(f);
	    Memory mainMemory = new Memory(readFile, 1024);
	    CPU mainProcessor = new CPU(mainMemory);
	    Object onFinish = new Object();
	    Clock.factory(mainMemory, mainProcessor, onFinish);

	    mainMemory.start();
	    mainProcessor.start();
	    Clock.instance.start();
	    synchronized (onFinish) {
		onFinish.wait();
	    }
	    System.out.println(mainMemory.finishedProcesses);
	    writeProcessOutputs((LinkedList<Process>) mainMemory.finishedProcesses);
	    System.exit(0);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static Queue<Process> readFile(File f) throws FileNotFoundException {
	LinkedList<Process> q = new LinkedList<Process>();
	Scanner sc = new Scanner(f);

	while (sc.hasNextLine()) {

	    String currentProcessName = "";
	    ArrayList<Integer> currentIObursts = new ArrayList<Integer>();
	    ArrayList<Integer> currentCPUbursts = new ArrayList<Integer>();
	    ArrayList<Integer> currentMemoryUsage = new ArrayList<Integer>();

	    String currentLine = sc.nextLine();
	    if (currentLine.matches("")) {
		continue;
	    }
	    String[] split = currentLine.split("\\[");
	    currentProcessName = split[0].split(" ")[0];
	    getList(currentCPUbursts, split[1]);
	    getList(currentMemoryUsage, split[2]);
	    getList(currentIObursts, split[3]);
	    int arrivalTime = Integer.parseInt(currentLine.split(" ")[1]);

	    Process p = new Process(currentProcessName, currentCPUbursts, currentIObursts, currentMemoryUsage);
	    p.setArrivalTime(arrivalTime);
	    q.add(p);

	    currentIObursts = new ArrayList<Integer>();
	    currentCPUbursts = new ArrayList<Integer>();
	    currentMemoryUsage = new ArrayList<Integer>();

	}

	Collections.sort(q, new Comparator<Process>() {
	    @Override
	    public int compare(Process a, Process b) {
		return a.getArrivalTime() - b.getArrivalTime();
	    }
	});

	sc.close();
	return q;
    }

    private static void getList(ArrayList<Integer> currentCPUbursts, String substring) {
	substring = substring.trim().replaceAll("[\\[\\]]", "");
	String[] split = substring.split(",");
	for (String string : split) {
	    currentCPUbursts.add(Integer.parseInt(string.trim()));
	}
    }

    public static void writeProcessOutputs(LinkedList<Process> finishedProcesses) throws IOException {
	File output = new File("proccess_output.txt");
	PrintWriter w = new PrintWriter(output);
	String prcstat = "CPU Util:" + ((Clock.instance.cpuCounter - Clock.instance.nullTime) / (double) Clock.instance.cpuCounter) * 100 + "%";

	for (int i = 0; i < finishedProcesses.size(); i++) {
	    prcstat += "\n-------------------------";
	    prcstat += "\nProcess ID: " + finishedProcesses.get(i).getId();
	    prcstat += "\nProgram name: " + finishedProcesses.get(i).getName();
	    prcstat += "\nReady queue entry time: " + finishedProcesses.get(i).getReadyQueueEntryTime();
	    prcstat += "\nNo. of times in the CPU: " + finishedProcesses.get(i).getTotalCPUTime();
	    prcstat += "\nTotal time spent in CPU: " + finishedProcesses.get(i).getCPUBurstTime();
	    prcstat += "\nNo. of times in I/O: " + finishedProcesses.get(i).getIOTotalCounter();
	    prcstat += "\nTotal time spent in I/O: " + finishedProcesses.get(i).getIOTotalTime();
	    prcstat += "\nTotal time waiting for memory: " + finishedProcesses.get(i).getMemoryWaitCounter();
	    prcstat += "\nNo. of times it was preempted: " + finishedProcesses.get(i).getPreemptionCounter();
	    prcstat += "\nTime it was TERMINATED/KILLED: " + finishedProcesses.get(i).getTerminationKillTime();
	    prcstat += "\nFinal state: " + finishedProcesses.get(i).getState();
//	    prcstat += "\nCPU utilization: " + 
	    prcstat += "\n-------------------------";
	    w.println(prcstat);
	    prcstat = "";
	}
	w.close();
    }

    public static void writeFile(int n) throws IOException {
	File f = new File("input.txt");
	PrintWriter w = new PrintWriter(f);

	for (int i = 0; i < n; i++) {
	    w.println(getRandomProcess(i));
	}

	w.close();
    }

    private static Process getRandomProcess(int i) {
	Process process = new Process();
	process.setName(generateProcessName()); // Change to string later.
	process.setArrivalTime(getNumber(1, 80));
	int n = getNumber(4, 9);

	List<Integer> cpu = new ArrayList<>(), memory = new ArrayList<>(), io = new ArrayList<>();
	process.setCPUBursts(cpu);
	process.setMemoryUsage(memory);
	process.setIOBursts(io);
	int mem = 0, c;

	for (int j = 0; j < n; j++) {
	    cpu.add(getNumber(10, 100));
	    while (mem + (c = getNumber(5, 200) * (getNumber(1, 100) > 50 ? 1 : -1)) < 0)
		;
	    memory.add(c);
	    mem += c;
	    io.add(getNumber(20, 60));
	}

	io.remove(io.size() - 1);
	return process;
    }

    private static int getNumber(int min, int max) {
	return random.nextInt((max - min) + 1) + min;
    }

    private static String generateProcessName() {
	String output = "";

	for (int i = 0; i < 8; i++) {
	    output += (char) getNumber('A', 'Z');
	}

	return output;
    }

}
