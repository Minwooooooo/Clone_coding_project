package com.sparta.clone.service;

import com.sparta.clone.controller.dto.response.ResponseDto;
import com.sparta.clone.controller.repo.MemberRepo;
import com.sparta.clone.entity.Member;
import com.sparta.clone.security.jwt.JwtTokenProvider;
import com.sun.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;

import javax.servlet.http.HttpServletResponse;
import java.rmi.MarshalledObject;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NaverLoginApi {

    // https://nid.naver.com/oauth2.0/authorize?client_id=43KVChnXUmyM5z9u7el8&response_type=code&redirect_uri=http://localhost:8080/auth/login&state=123
    // https://nid.naver.com/oauth2.0/token?client_id=43KVChnXUmyM5z9u7el8&client_secret=xdF8fijKNu&grant_type=authorization_code&state=123&code=1ZqeK34Agy2HCJENzc

    private String baseUrl = "https://nid.naver.com/oauth2.0/";
    private String nidMeUrl ="https://openapi.naver.com/v1/nid/me";
    private String authUrl = "authorize?client_id=";
    private String authUrl2="&response_type=code&redirect_uri=http://localhost:8080/auth/login&state=";
    private String tokenUrlId ="token?client_id=";
    private String tokenUrlSecret = "&client_secret=";
    private String tokenUrlState="&grant_type=authorization_code&state=";
    private String tokenUrlCode ="&code=";

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    String ClientId;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    String ClientSecret;
    String temp_state="123";

    private final MemberRepo memberRepo;
    private final JwtTokenProvider jwtTokenProvider;


    // 네이버에서 제공하는 로그인창 url
    public String makeLoginUrl(){
        String loginUrl = baseUrl+authUrl+ClientId+authUrl2+temp_state;
        return loginUrl;
    }

    // 네이버에서 제공하는 token발급 url
    public String makeAuthUrl(String code,String state){
        String authUrl = baseUrl+tokenUrlId+ClientId+tokenUrlSecret+ClientSecret+tokenUrlState+state+tokenUrlCode+code;
        return authUrl;
    }


    //네이버 로그인후 토큰받기
    public ResponseDto<?> login(String code, String state, HttpServletResponse response){
        RestTemplate restTemplate =new RestTemplate(); // 토큰을 발급 받을 URL 초기화
        HttpEntity<?> httpentity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<Map> resultMap = restTemplate.exchange(makeAuthUrl(code,state), HttpMethod.GET,httpentity,Map.class); // GET 방식으로 토큰 발급 요청
        String NaverAccessToken=String.valueOf(resultMap.getBody().get("access_token")); // 받은 object를 String으로 변환
        String NaverRefreshToken=String.valueOf(resultMap.getBody().get("refresh_token"));
        String NaverTokenType=String.valueOf(resultMap.getBody().get("token_type")); // = "Bearer"
        String NaverExpiresIn=String.valueOf(resultMap.getBody().get("expires_in")); // = "3600"

        // 정보받기
        response.addHeader("RefreshToken",NaverRefreshToken);
        Map userData=nidMe(NaverAccessToken,NaverTokenType);
        // 받은 정보로 회원테이블 조회 및 신규 회원일시 자동 가입
        signIn(userData);

        // 로그인한 회원 객체 생성


        String id = String.valueOf(userData.get("id"));
        Member member = memberRepo.findById(id).get();



        // 토큰생성
        String accessToken = jwtTokenProvider.creatToken(member);


        // 생성된 토큰 해더에 추가
        response.addHeader("Authorization","Bearer "+accessToken);
        System.out.println("accesstoken : "+accessToken);

        //
        String msg = member.getName()+"님 반갑습니다.";
        return ResponseDto.success(msg);
    }


    //네이버 로그인 기반 유저 정보 받기
    public Map nidMe(String NaverAccessToken,String NaverTokenType){
        RestTemplate restTemplate = new RestTemplate(); // 정보를 발급 받을 URL 초기화
        HttpHeaders headers = new HttpHeaders(); // URL에 보낼 헤더 초기화
        String nidMeHeader=NaverTokenType+" "+NaverAccessToken; // 헤더에 토큰 담기
        headers.set("Authorization",nidMeHeader);
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<Map> userInfo = restTemplate.exchange(nidMeUrl,HttpMethod.GET,httpEntity, Map.class); // 정보 발급 요청
        Map userData = (Map) userInfo.getBody().get("response");
        System.out.println(userData.get("id"));
        return userData;
    }

    //유저 정보 기반 회원테이블 생성
    public void signIn(Map userData){
        String id = String.valueOf(userData.get("id"));
        String name = String.valueOf(userData.get("name"));
        int gender=0;
        if (String.valueOf(userData.get("gender")).equals("M")){
            gender=1;
        }
        if (String.valueOf(userData.get("gender")).equals("F")){
            gender=2;
        }
        int birthyear= Integer.valueOf(String.valueOf(userData.get("birthyear")));

        if(!memberRepo.existsById(id)){
            Member member = Member.builder()
                    .id(id)
                    .name(name)
                    .gender(gender)
                    .birthyear(birthyear)
                    .userRole("ROLE_USER")
                    .build();
            memberRepo.save(member);
        }
        String msg = name+"님 환영합니다.";
    }

}
