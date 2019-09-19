import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Main {

	public static final String COMMAND_STOP = "STOP";
	public static final String COMMAND_ENTER = "ENTER";
	public static final String COMMAND_EXIT = "EXIT";
	public static final String COMMAND_UP = "UP";
	public static final String COMMAND_DOWN = "DOWN";
	public static final String COMMAND_OPEN = "OPEN";
	public static final String COMMAND_CLOSE = "CLOSE";
	
	public static final String STATUS_STOPPED = "STOPPED";
	public static final String STATUS_OPENED = "OPENED";
	public static final String STATUS_UPWARD = "UPWARD";
	public static final String STATUS_DOWNWARD = "DOWNWARD";

	public static Call[] jobs;
	
	public static void main(String[] args) {
		// 문제
		String userKey = "tester2";
		int problemId = 2;
		int numOfElevators = 4;
		
		
		// datas
		ArrayList<Elevator> elevatorList = new ArrayList<>();
		HashMap<Integer, Call> callMap = new HashMap<>();
//		HashMap<Integer, Integer> callCounter = new HashMap<>();
		
		ArrayList<Command> commandList = null;
		
		String response = start(userKey, problemId, numOfElevators);
		
		if (response.equals("200")) {
			
			jobs = new Call[numOfElevators];
			
			int time = -1;
			
			while (true) {
				time++;
				System.out.println("\n\n---------------timestamp : " + time + "--------------");
				
				elevatorList = new ArrayList<>();
				callMap = new HashMap<>();
				boolean is_finished = onCalls(elevatorList, callMap);
//				boolean is_finished = onCalls(elevatorList, callMap, callCounter);
				if (is_finished) {
					System.out.println("<< FINISHED!! >>");
					break;
				}
				
				System.out.println("남은  Call 개수 :" + callMap.size());
				commandList = new ArrayList<>();
//				System.out.println("\n***elevator command generator loop start\n");
				
				for (int i = 0; i < elevatorList.size(); i++) {
					Elevator elevator = elevatorList.get(i);
//					System.out.println("\nelevator: " + elevator);
					Command command = generateCommand(elevator, callMap, jobs[i]);
//					Command command = generateCommand(elevator, callMap, callCounter, jobs[i]);
					commandList.add(command);
				}
				
//				System.out.println("\n***elevator command generator loop end\n");		
				commandList.stream().forEach(c -> System.out.println(c.getJsonCommandData().toString()));
				System.out.println(" - job list - \n" + Arrays.toString(jobs));
				
				action(commandList);
				try {
					Thread.sleep(25); // 40 request per second
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		
	}


	private static void action(ArrayList<Command> commandList) {
		System.out.println("\n\n>>>> api.action()");
		JSONParser parser = new JSONParser();
		JSONArray commandArray = parser.getCommandsJSONArray(commandList);
		
//		System.out.println(commandArray);
		Connection.getInstance().action(commandArray);
	}
	
	private static Comparator<Call> compById = new Comparator<Call>() {
		public int compare(Call c1, Call c2) {
			return c1.getId() - c2.getId();
		}
	};
	
	private static Command generateCommand(Elevator elevator, HashMap<Integer, Call> callMap, Call job) {
		Command command = new Command(elevator.getId());
		
		if (job == null) { // job이 없을 때 FIFO에 따라 최신 job넣어주기
			if (callMap.isEmpty()) { // 할 일도 없으면 STOP
				if (elevator.getStatus().equals(STATUS_OPENED))
					return new Command(elevator.getId(), COMMAND_CLOSE);
				else 
					return new Command(elevator.getId(), COMMAND_STOP);
			}
			List<Call> calls = new ArrayList<Call>(callMap.values());
			
			for (int i = 0; i < jobs.length; i++) {
				Call call = jobs[i];
				if (call == null)
					continue;
				else {
					int id = call.getId();
					calls.removeIf(c -> c.getId() == id);
				}
			}
			if (calls.isEmpty())
				if (elevator.getStatus().equals(STATUS_OPENED))
					return new Command(elevator.getId(), COMMAND_CLOSE);
				else 
					return new Command(elevator.getId(), COMMAND_STOP);
			
			Collections.sort(calls, compById);
			jobs[elevator.getId()] = calls.get(0);
			
			job = calls.get(0);
		} 
		
//		System.out.println(elevator.getId()+"의 job =" + job);
		
		// for next elevator
		callMap.remove(job.getId());
		String status = elevator.getStatus();
		
		if (elevator.hasPassenger()) { // 탑승 후, 승객이 있을 때
			if (elevator.getFloor() < job.getEnd()) {
	//			up();
				if (status.equals(STATUS_OPENED)) {
					command.setCommand(COMMAND_CLOSE);
				} else if (status.equals(STATUS_STOPPED)) {
					command.setCommand(COMMAND_UP);
				} else {
					command.setCommand(COMMAND_UP);
				}
				
			} else if (elevator.getFloor() > job.getEnd()) {
	//			down();
				if (status.equals(STATUS_OPENED)) {
					command.setCommand(COMMAND_CLOSE);
				} else if (status.equals(STATUS_STOPPED)) {
					command.setCommand(COMMAND_DOWN);
				} else {
					command.setCommand(COMMAND_DOWN);
				}
				
			} else { // 현재층이면 
				
				if (status.equals(STATUS_UPWARD) || status.equals(STATUS_DOWNWARD)) {
					command.setCommand(COMMAND_STOP);
				} else if (status.equals(STATUS_STOPPED)) {
					command.setCommand(COMMAND_OPEN);
				} else if (status.equals(STATUS_OPENED)) {
					command.setCommand(COMMAND_EXIT);
					int[] callIds = new int[1];
					callIds[0] = job.getId();
					command.setCallIds(callIds);
					jobs[elevator.getId()] = null; // 일 마침
				}
			}
			
			
		} else { // 승객이 없을 때 
			if (elevator.getFloor() < job.getStart()) {
	//			up();
				if (status.equals(STATUS_OPENED)) {
					command.setCommand(COMMAND_CLOSE);
				} else if (status.equals(STATUS_STOPPED)) {
					command.setCommand(COMMAND_UP);
				} else {
					command.setCommand(COMMAND_UP);
				}
				
			} else if (elevator.getFloor() > job.getStart()) {
	//			down();
				if (status.equals(STATUS_OPENED)) {
					command.setCommand(COMMAND_CLOSE);
				} else if (status.equals(STATUS_STOPPED)) {
					command.setCommand(COMMAND_DOWN);
				} else {
					command.setCommand(COMMAND_DOWN);
				}
				
			} else { // 현재층이면 
				
				if (status.equals(STATUS_UPWARD) || status.equals(STATUS_DOWNWARD)) {
					command.setCommand(COMMAND_STOP);
				} else if (status.equals(STATUS_STOPPED)) {
					command.setCommand(COMMAND_OPEN);
				} else if (status.equals(STATUS_OPENED)) {
					command.setCommand(COMMAND_ENTER);
					
					int[] callIds = new int[1];
					callIds[0] = job.getId();
					command.setCallIds(callIds);
				}
			}
		}
		
		return command;
	}
	
	private static boolean onCalls(ArrayList<Elevator> elevatorList, HashMap<Integer, Call> callMap) {
		System.out.println("\n\n>>>> api.on_calls()");
		JSONObject responseJson = Connection.getInstance().onCalls();
//		System.out.println("onCalls Json Form data : " + responseJson + "\n");
		JSONParser parser = new JSONParser();
		try {
			elevatorList.addAll(parser.getElevatorsFromOnCalls(responseJson));
//			System.out.println("***elevatorList\n" + elevatorList);
			callMap.putAll(parser.getCallMapFromOnCalls(responseJson));
//			System.out.println("***callMap\n" + callMap);
//			callCounter.putAll(parser.getCallCounterFromOnCalls(responseJson));
//			System.out.println("***callCount\n" + callCounter);
			
			return responseJson.getBoolean("is_end");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return false;
	}

	private static String start(String userKey, int problemId, int numOfElevators) {
		System.out.println(">>>> api.start()");
		String response = TokenManger.getInstance().createToken(userKey, problemId, numOfElevators);
		System.out.println("Token : " + TokenManger.getInstance().getToken());
		return response;
	}

}
