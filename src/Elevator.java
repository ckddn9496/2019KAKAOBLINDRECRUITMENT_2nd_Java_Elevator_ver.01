import java.util.ArrayList;

public class Elevator {
	private int id;
	private int floor;
	private ArrayList<Call> passengers;
	private String status;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFloor() {
		return floor;
	}
	public void setFloor(int floor) {
		this.floor = floor;
	}
	public ArrayList<Call> getPassengers() {
		return passengers;
	}
	public void setPassengers(ArrayList<Call> passengers) {
		this.passengers = passengers;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public boolean hasPassenger() {
		if (passengers == null)
			return false;
		else if (passengers.size() == 0)
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (passengers == null)
			return id + "번 엘리베이터 정보,  현재 " + floor + "층, 상태: " + status + "\n";
		else
			return id + "번 엘리베이터 정보,  현재 " + floor + "층, 상태: " + status + ", 탑승객 정보: " + passengers + "\n";
	}
}
