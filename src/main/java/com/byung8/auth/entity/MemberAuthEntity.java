package com.byung8.auth.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@RedisHash("memberAuth")
@Getter
@Setter
@ToString
public class MemberAuthEntity implements Serializable {
	private static final long serialVersionUID = 1370692830319429806L;
	@Id
    private Long id;
	
	private int memberId;
	private String email;
	private String name;
	private int genderId;
	private String genderNm;
	private String birthday;
	private float height;
	private long expiredTime;
}
