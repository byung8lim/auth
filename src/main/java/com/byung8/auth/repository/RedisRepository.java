package com.byung8.auth.repository;

import org.springframework.data.repository.CrudRepository;

import com.byung8.auth.entity.MemberAuthEntity;

public interface RedisRepository extends CrudRepository<MemberAuthEntity, Long> {
	public MemberAuthEntity findByMemberId(int memberId);
	public MemberAuthEntity findByEmailAndName(String email,String name);
}
