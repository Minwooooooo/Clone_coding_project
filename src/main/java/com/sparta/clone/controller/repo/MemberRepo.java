package com.sparta.clone.controller.repo;

import com.sparta.clone.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepo extends JpaRepository<Member, String> {

    Optional<Member> findByName(String name);
}
