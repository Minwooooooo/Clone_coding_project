package com.sparta.clone.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.ClientInfoStatus;
import java.util.Map;

@Service
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


    //Naver 로그인후 토큰받기
    public ResponseEntity<?> getToken(String code,String state){
        RestTemplate restTemplate =new RestTemplate(); // 토큰을 발급 받을 URL 초기화
        HttpEntity<?> httpentity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<Map> resultMap = restTemplate.exchange(makeAuthUrl(code,state), HttpMethod.GET,httpentity,Map.class); // GET 방식으로 토큰 발급 요청
        String NaverAccessToken=String.valueOf(resultMap.getBody().get("access_token")); // 받은 object를 String으로 변환
        String NaverRefreshToken=String.valueOf(resultMap.getBody().get("refresh_token"));
        String NaverTokenType=String.valueOf(resultMap.getBody().get("token_type")); // = "Bearer"
        String NaverExpiresIn=String.valueOf(resultMap.getBody().get("expires_in")); // = "3600"
        return nidMe(NaverAccessToken,NaverTokenType); // Naver에서 제공받은 토큰으로 로그인한 유저정보 조회
    }

    public ResponseEntity<?> nidMe(String NaverAccessToken,String NaverTokenType){
        RestTemplate restTemplate = new RestTemplate(); // 정보를 발급 받을 URL 초기화
        HttpHeaders headers = new HttpHeaders(); // URL에 보낼 헤더 초기화
        String nidMeHeader=NaverTokenType+" "+NaverAccessToken; // 헤더에 토큰 담기
        headers.set("Authorization",nidMeHeader);
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<Map> nidMeResponse = restTemplate.exchange(nidMeUrl,HttpMethod.GET,httpEntity,Map.class); // 정보 발급 요청
        return nidMeResponse;
    }

}
