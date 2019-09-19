import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {
	
	public ArrayList<Elevator> getElevatorsFromOnCalls(JSONObject responseJson) {
		ArrayList<Elevator> elevatorList = new ArrayList<>();
		
		try {
			JSONArray elevators = responseJson.getJSONArray("elevators");
			for (int i = 0; i < elevators.length(); i++) {
				JSONObject data = elevators.getJSONObject(i);
				
				JSONArray passengerdatas = data.getJSONArray("passengers");
				ArrayList<Call> passengers = new ArrayList<>();
				for (int j = 0; j < passengerdatas.length(); j++) {
					JSONObject passengerData = passengerdatas.getJSONObject(j);
					Call call = new Call();
					call.setId(passengerData.getInt("id"));
					call.setTimestamp(passengerData.getInt("timestamp"));
					call.setStart(passengerData.getInt("start"));
					call.setEnd(passengerData.getInt("end"));
					passengers.add(call);
				}
				
				Elevator elevator = new Elevator();
				elevator.setId(data.getInt("id"));
				elevator.setFloor(data.getInt("floor"));
				elevator.setStatus(data.getString("status"));
				elevator.setPassengers(passengers);
				elevatorList.add(elevator);
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return elevatorList;
	}

	public HashMap<Integer, Call> getCallMapFromOnCalls(JSONObject responseJson) {
		HashMap<Integer, Call> callMap = new HashMap<>();
		
		try {
			JSONArray calls = responseJson.getJSONArray("calls");
			
			for (int i = 0; i < calls.length(); i++) {
				JSONObject data = calls.getJSONObject(i);
				Call call = new Call();
				call.setId(data.getInt("id"));
				call.setTimestamp(data.getInt("timestamp"));
				call.setStart(data.getInt("start"));
				call.setEnd(data.getInt("end"));	
				if (!callMap.containsKey(call.getId())) { // 포함하지 않고 있다면 추가
					callMap.put(call.getId(), call);
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return callMap;
	}

	public HashMap<Integer, Integer> getCallCounterFromOnCalls(JSONObject responseJson) {
		HashMap<Integer, Integer> callCounter = new HashMap<>();
		
		try {
			JSONArray calls = responseJson.getJSONArray("calls");
			
			for (int i = 0; i < calls.length(); i++) {
				JSONObject data = calls.getJSONObject(i);
				int callId = data.getInt("id");
				if (callCounter.containsKey(callId)) { // 포함하지 않고 있다면 추가
					callCounter.put(callId, callCounter.get(callId) + 1);
				} else {
					callCounter.put(callId, 1);
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return callCounter;
	}

	
	
	public JSONArray getCommandsJSONArray(ArrayList<Command> commandList) {
		JSONArray commandArray = new JSONArray();
		for (Command command : commandList) {
			commandArray.put(command.getJsonCommandData());
		}
		
		return commandArray;
	}


}
