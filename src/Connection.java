import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Connection {
	private static Connection instance = null;
	
	public static Connection getInstance() {
		if (instance == null) {
			instance = new Connection();
		}
		return instance;
	}
	
	
	// POST /action
	//	X-Auth-Token: {Token}
	//	Content-Type: application/json
	public JSONObject action(JSONArray commandArrays) {
		HttpURLConnection conn = null;
		JSONObject responseJson = null;
		
		try {
			
			URL url = new URL(GlobalData.HOST_URL + GlobalData.POST_ACTION);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("X-Auth-Token", TokenManger.getInstance().getToken());
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			JSONObject commands = new JSONObject();
			commands.put("commands", commandArrays);
			bw.write(commands.toString());
			bw.flush();
			bw.close();
			
			int responseCode = conn.getResponseCode();
			if (responseCode == 400) {
				System.out.println("400:: 해당 명령을 실행할 수 없음 (실행할 수 없는 상태일 때, 엘리베이터 수와 Command 수가 일치하지 않을 때, 엘리베이터 정원을 초과하여 태울 때)");
			} else if (responseCode == 401) {
				System.out.println("401:: X-Auth-Token Header가 잘못됨");
			} else if (responseCode == 500) {
				System.out.println("500:: 서버 에러, 문의 필요");
			} else { // 성공
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				
				responseJson = new JSONObject(sb.toString());
				
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("not JSON Format response");
			e.printStackTrace();
		}
		return responseJson;
	}
		
	
	
	// GET /oncalls
	//	X-Auth-Token: {Token}
	public JSONObject onCalls() {
		HttpURLConnection conn = null;
		JSONObject responseJson = null;
		
		try {
			URL url = new URL(GlobalData.HOST_URL + GlobalData.GET_ONCALLS);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("X-Auth-Token", TokenManger.getInstance().getToken());
			
			int responseCode = conn.getResponseCode();
			if (responseCode == 401) {
				System.out.println("401:: X-Auth-Token Header가 잘못됨");
			} else if (responseCode == 500) {
				System.out.println("500:: 서버 에러, 문의 필요");
			} else { // 성공
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				
				responseJson = new JSONObject(sb.toString());
				
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("not JSON Format response");
			e.printStackTrace();
		}
		return responseJson;
	}
	
	
	
	// POST /start/{user_key}/{problem_id}/{number_of_elevators}
	public String start(String userKey, int problemId, int numOfElevators) {
		HttpURLConnection conn = null;
		JSONObject responseJson = null;
		String response = null;
		String startParams = "/" + userKey + "/" + Integer.toString(problemId) + "/" + Integer.toString(numOfElevators);
		
		try {
			URL url = new URL(GlobalData.HOST_URL + GlobalData.POST_START + startParams);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			
			int responseCode = conn.getResponseCode();
			if (responseCode == 200) { // 성공
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				
				responseJson = new JSONObject(sb.toString());
				response = responseJson.getString("token");
			} else {
				response = String.valueOf(responseCode);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("not JSON Format response");
			e.printStackTrace();
		}
		
		return response;
	}
	
	
}
