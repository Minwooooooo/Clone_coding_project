package com.sparta.clone.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomUserDetails implements UserDetails {

  //UserDetails : Spring Security에서 사용자의 정보를 담는 인터페이스이다.
  // 기본 Override Methods :
  //        getAuthorities() (return_type:Collection<? extends GrantedAuthority>)
  //                   : 사용자의 권한목록
  //        getPassword() (return_type:String)
  //                   : 사용자의 비밀번호
  //        getUsername() (return_type:String)
  //                   : 사용자의 고유한값 PK 또는 중복이 없는 값
  //        isAccountNonExpired() (return_type:Boolean)
  //                   : 계정의 만료여부
  //        isAccountNonLocked() (return_type:Boolean)
  //                   : 계정의 잠김여부
  //        isCredentialsNonExpired() (return_type:Boolean)
  //                   : 비밀번호 먄료여부
  //        isEnabled() (return_type:Boolean)
  //                   : 계정의 활성화여부

  private Member member;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(authority);
    return authorities;
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return member.getId();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
