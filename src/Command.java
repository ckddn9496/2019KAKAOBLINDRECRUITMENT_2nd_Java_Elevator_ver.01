import org.json.JSONException;
import org.json.JSONObject;

public class Command {
	private int elevatorId;
	private String command;
	private int[] callIds;
	
	public Command(int elevatorId) {
		this.elevatorId = elevatorId;
	}	
	
	public Command(int elevatorId, String command) {
		this.elevatorId = elevatorId;
		this.command = command;
	}
	
	public Command(int elevatorId, String command, int[] callIds) {
		this.elevatorId = elevatorId;
		this.command = command;
		this.callIds = callIds;
	}
	
	public int getElevatorId() {
		return elevatorId;
	}
	public void setElevatorId(int elevatorId) {
		this.elevatorId = elevatorId;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public int[] getCallIds() {
		return callIds;
	}
	public void setCallIds(int[] callIds) {
		this.callIds = callIds;
	}

	public JSONObject getJsonCommandData() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("elevator_id", elevatorId);
			jsonObject.put("command", command);
			if (callIds != null)
				jsonObject.put("call_ids", callIds);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}
}
