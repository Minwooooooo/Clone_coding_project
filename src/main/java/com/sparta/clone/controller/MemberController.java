package com.sparta.clone.controller;

import com.sparta.clone.service.NaverLoginApi;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.print.DocFlavor;
import java.net.URI;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final NaverLoginApi naverLoginApi;


    // 네이버에서 제공하는 로그인창으로 이동
    @RequestMapping(value = "/auth/naver")
    public String naverLogin(){
        return "redirect:"+naverLoginApi.makeLoginUrl();
        //성공시 developer 지정한 call-back URL로 redirect
    }

    // call-back URL
    @RequestMapping(value = "/auth/login")
    public ResponseEntity<?> naverAuth(@RequestParam String code, @RequestParam String state){
        // call-back URL 로 올시 code와 state를 파라미터로 반영해서 옴
        return naverLoginApi.getToken(code,state);
        // 받은 code와 state를 기반으로 Token발급 및 정보 조회
    }



}
