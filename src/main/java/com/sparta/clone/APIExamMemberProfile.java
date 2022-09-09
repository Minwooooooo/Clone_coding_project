package com.sparta.clone;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// 네이버 API 예제 - 회원프로필 조회
// 네이버 회원 프로필을 조회하는 API를 호출하는 코드를 Java로 작성한 예
// 네이버 로그인해서 획득한 접근 토큰을 요청 헤더에 추가해 프로필 조회 API를 RESTful API 방식으로 호출합니다. 반환받은 결괏값은 JSON 형식으로 출력합니다.
//#access_token=AAAAObX7OcXlSByTmnd1Lnt41NFwXgb46s_4arLZiOyuS9SqbMK5s7qfW9rso9t1Hzf1rd5K0K9OkdAmV_8bwWOM_dg&state=fe405196-6811-4697-a831-17ae7edcc1f8&token_type=bearer&expires_in=3600
public class APIExamMemberProfile {

    public static void main(String[] args) {
        String token = "AAAAObX7OcXlSByTmnd1Lnt41NFwXgb46s_4arLZiOyuS9SqbMK5s7qfW9rso9t1Hzf1rd5K0K9OkdAmV_8bwWOM_dg";// 네아로 접근 토큰 값";
        String header = "Bearer " + token; // Bearer 다음에 공백 추가
        try {
            String apiURL = "https://openapi.naver.com/v1/nid/me";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", header);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            System.out.println(response.toString());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
