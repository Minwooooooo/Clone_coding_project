package com.sparta.clone.security.jwt;

import com.sparta.clone.entity.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component

public class JwtTokenProvider {

    private final Key JWT_KEY;

    public JwtTokenProvider(@Value("${jwt.key}")String secretKey){
        byte[] key= Decoders.BASE64.decode(secretKey);
        this.JWT_KEY= Keys.hmacShaKeyFor(key);
    }



    public String creatToken(Member member){
        System.out.println(member);

        return Jwts.builder()
                .setSubject(member.getName())
                .claim("auth",member.getUserRole())
                .signWith(JWT_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String[] getInfoFromToken(String token){
        Claims claims = tempClaim(token);
        System.out.println(claims);
        String authorities = claims.get("auth").toString();
        String name = claims.getSubject();
        String[] infos=new String[2];
        infos[0]=name;
        infos[1]=authorities;
        return infos;
    }

    public Authentication getAuthentication(String token){
        Claims claims=tempClaim(token);
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        User principal = new User(claims.getSubject(),"",authorities);

        return new UsernamePasswordAuthenticationToken(principal,null,authorities);
    }

    public Claims tempClaim(String token){
        System.out.println("복호화 시작");
        //토큰 복호화
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(JWT_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        System.out.println("복호화 결과 : "+claims);
        return claims;
    }


    public boolean validateToken(String token) {
        try{
            tempClaim(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }
}
