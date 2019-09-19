# 2019KAKAOBLINDRECRUITMENT_2nd_Java_Elevator_ver.01 (FIFO)
2019 카카오 블라인드 공채 2차 오프라인 코딩 테스트 문제

## 1. 문제 설명

문제와 Elevator 서버 코드: https://github.com/kakao-recruit/2019-blind-2nd-elevator

문제와 해설: https://tech.kakao.com/2018/10/23/kakao-blind-recruitment-round-2/

## 2. 코드 구성
> 코드 리팩토링이 되지 않아 구조가 조금 복잡합니다.

### Main

시작을 위한 메인 클래스
``` java
start()
while (true) {
  onCalls()
  if (isFinished())
    break;
  
  for (Elevator elevator: elevators) {
    commands.add(generateCommand(elevator));
  }
  action(commands);
}
```
의 큰 구조를 따르며 시작시 필요한 변수들이 선언되어 있다.

### TokenManager

토큰 관리를 위한 클래스

Connection의 start를 이용하여 token을 받아오고 이후 token이 필요한 곳에서 TokenManager인스턴스를 가져와 이용한다.
``` String token = TokenManager.getInstance().getToken() ```

### Connection

REST API이용을 위한 클래스

start, onCalls, action를 제공하며 Connection이 필요한 곳에서 Connection 인스턴스를 가져와 이용한다.
``` java
Conection connection = Connection.getInstance();

connection.start();

connection.oncalls();

connection.action();
```

### JSONParser

REST API를 통해 가져온 JSON Format 데이터를 처리할 수 있는 Data형태로 파싱하는 클래스.

주로 onCalls를 통해 가져온 JSONObject를 파싱하여 Elevator와 Call을 생성한다.

### GlobalData

REST API이용에 필요한 URL이 정의되어 있는 클래스

## Resource
### Elevator

Elevator 클래스

### Call

Call 클래스

### Command

Command 클래스

## 3. 결과

> Start Params
```java
String userKey = "tester";
		int problemId = 0; 
    /* 0: 어피치 맨션
    *  1: 제이지 빌딩
    *  2: 라이언 타워
    */
		int numOfElevators = 4;
```
 **1. 어피치 맨션 Total Timestamp: 21**

    AveWait: 5.333333, AveTravel: 5.666667, AveTotal: 11.000000, LastTs: 21, Status: OK

  **2. 제이지 빌딩 Total Timestamp 1231**

    AveWait: 366.505000, AveTravel: 12.735000, AveTotal: 379.240000, LastTs: 1231, Status: OK

 **3. 라이언 타워 Total Timestamp 3212**

    AveWait: 687.474000, AveTravel: 13.954000, AveTotal: 701.428000, LastTs: 3212, Status: OK
    
## 4. 문제점

10초이내 생성된 토큰이 있으면 403 Forbidden이 나오며, 이를 무시하고 실행시 id가 중복된 Call들이 onCall API를 통하여 수신되었다. call이 고유한 아이디를 가지는 것으로 생각했는데 에러로 인해 잘못온 데이터를 기준으로 코드를 구현하다가 Call들을 저장하고 있는 Container를 HashMap으로 둔후, Call마다 중복된 갯수를 카운트하는 HashMap을 별도로 두어서 구현하였다. 하지만 잘못된 이유를 알게되니 이를 꼭 HashMap이 아닌 Queue로 구현하면 더 쉽게 FIFO 알고리즘의 엘리베이터를 구현할 수 있다고 생각한다.

FIFO알고리즘에 따라 처리되기 때문에 엘리베이터의 정원이 **8**명임에도 불구하고 **1**명 밖에 탑승하지 못하는 문제점이 있다. 또한 순서대로 처리되어 starvation(기아현상)이 없는것 처럼 보이지만 고객 수가 많아질수록 평균 대기시간과 응답시간이 길어진다는 큰 문제점이 존재한다.

실제 우리가 이용하는 엘리베이터처럼 구현을 하려고 하는것이 한번에 하는것이 어려워서 쉬운 구현단계부터 시작해보기로 하였다.
**1. FCFS**
2. SCAN(LOOK)

디스크 알고리즘 중 첫 알고리즘인 FCFS(First come, First serve)를 이용하였지만 Scan 혹은 더 효율적인 LOOK알고리즘을 이용하여 엘리베이터의 방향과 일치하는 방향의 고객을 태워가도록 발전시키면 평균 대기시간과 응답시간을 줄일 수 있을 것이다. 엘리베이터의 이동방향과 요청한 고객의 이동방향을 저장해놓을 수 있도록 별도의 변수를 두어야 할 것 같다.
