package com.sparta.clone.service;



import com.sparta.clone.controller.repo.MemberRepo;
import com.sparta.clone.entity.Member;
import com.sparta.clone.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  // Spring Security에서 유저의 정보를 가져오는 인터페이스

  private final MemberRepo memberRepo;

  @Override
  public UserDetails loadUserByUsername(String Id) throws UsernameNotFoundException {
    Member temp_member = memberRepo.findById(Id).orElseThrow(() -> new UsernameNotFoundException(Id+"<- 해당 사용자를 찾을 수 없습니다."));
    UserDetails member = CustomUserDetails.builder()
            .member(temp_member)
            .build();
    return member;

  }
}
