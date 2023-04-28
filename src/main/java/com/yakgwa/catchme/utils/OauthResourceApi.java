package com.yakgwa.catchme.utils;

import com.yakgwa.catchme.dto.MemberDto;
import com.yakgwa.catchme.dto.SignUpRequestDto;
import com.yakgwa.catchme.exception.MemberDataNotLoadException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OauthResourceApi {
    private static String apiURL = "https://openapi.naver.com/v1/nid/me";

    public static SignUpRequestDto loadDataFromNaver(String accessToken) {
        String header = "Bearer " + accessToken; // Bearer 다음에 공백 추가
        SignUpRequestDto signUpRequestDto = null;

        try {
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", header);

            // 응답 코드
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
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



            System.out.println("응답 결과");
            System.out.println(response.toString());
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response.toString());
            JSONObject jsonObj = (JSONObject) obj;
            JSONObject information = (JSONObject) jsonObj.get("response");

            String id= (String) information.get("id");
            String profileImage = (String) information.get("profile_image");
            String gender = (String) information.get("gender");
            String email = (String) information.get("email");
            String mobile = (String) information.get("mobile");
            String birthYear = (String) information.get("birthyear");
            // JSON 파싱 정보
//            System.out.println("birthYear = " + birthYear);
//            System.out.println("id = " + id);
//            System.out.println("gender = " + gender);
//            System.out.println("profileImage = " + profileImage);
//            System.out.println("email = " + email);
//            System.out.println("mobile = " + mobile);
            signUpRequestDto = new SignUpRequestDto(id, id, mobile, email, id, birthYear, gender);

        } catch (Exception e) {
            System.out.println(e);
            throw new MemberDataNotLoadException("[네이버]액세스 토큰을 통해 정보를 불러오는데 실패했습니다.");
        }
        return signUpRequestDto;
    }


    public static SignUpRequestDto loadDataFromTest(String accessToken) {
        return new SignUpRequestDto("testid", "testid", "010-0000-0000", "test_user@mail.com","test_nickname", "1999", "M");
    }
}
