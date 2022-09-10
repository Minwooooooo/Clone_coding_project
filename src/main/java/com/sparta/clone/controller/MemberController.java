package com.sparta.clone.controller;

import com.sparta.clone.controller.dto.response.ResponseDto;
import com.sparta.clone.service.NaverLoginApi;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

@Controller
//@Controller : 주로 View를 반환
//@RestController : 주로 Data를 반환 (@Controller+ Method에 @ResponseBody 추가)
@RequiredArgsConstructor
public class MemberController {

    private final NaverLoginApi naverLoginApi;


    // 네이버에서 제공하는 로그인창으로 이동
    @RequestMapping(value = "/auth/naver")
    public String naverLogin(){
        return "redirect:"+naverLoginApi.makeLoginUrl();
        //성공시 developer 지정한 call-back URL로 redirect
    }

    // call-back URL -> 정보조회 (-> 회원가입) -> 로그인
    @RequestMapping(value = "/auth/login")
    public @ResponseBody ResponseDto<?> naverAuth(@RequestParam String code, @RequestParam String state, HttpServletResponse response){
        // call-back URL 로 올시 code와 state를 파라미터로 반영해서 옴
        return naverLoginApi.login(code,state,response);
        // 받은 code와 state를 기반으로 Token발급 및 정보 조회
    }

    //Test 토큰 정보 조회
    @GetMapping(value = "/test/auth/info")
    public @ResponseBody UserDetails testInfoByToken(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;

        return userDetails;
    }



}
