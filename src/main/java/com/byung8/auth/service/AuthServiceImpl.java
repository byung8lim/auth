
package com.byung8.auth.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.byung8.auth.entity.MemberAuthEntity;
import com.byung8.auth.repository.RedisRepository;
import com.byung8.common.domain.Member;
import com.byung8.common.domain.Result;
import com.byung8.common.exception.Byung8Exception;
import com.byung8.common.rest.RestClientTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service("authService")
@Configuration
@Slf4j
public class AuthServiceImpl implements AuthService {
	@Autowired
	RedisRepository redisRepository;
	
	private static String server;
	private static int port;
	private static int connectionTimeout;
	private static int readTimeout;

	@Value("${rest.timeout.connection}")
	public void setConnectionTimeout(int timeout) {
		connectionTimeout = timeout;
	}
	
	@Value("${rest.timeout.read}")
	public void setReadTimeout(int timeout) {
		readTimeout = timeout;
	}
		
	@Value("${rest.server}")
	public void setServer(String s) {
		server = s;
	}
	
	@Value("${rest.port}")
	public void setPort(int p) {
		port = p;
	}
	
	public Result issueToken(Member member, String txid) throws Byung8Exception {
		Result result = null;
		try {
			MemberAuthEntity auth = fetchMemberAuth(member);
			result = new Result(txid, Result.OK).putValue("issueToken", auth.getId());
		} catch (Exception e) {
			if (e instanceof Byung8Exception) {
				throw (Byung8Exception) e;
			} else {
				throw new Byung8Exception(e);
			}
		}
		return result;
	}
	
	public Result verifyToken(Long id, String txid) throws Byung8Exception {
		Result result = null;
		Optional<MemberAuthEntity> auth = null;
		try {
			auth = redisRepository.findById(id);
			if (auth == null) {
				throw new Byung8Exception();
			}
			MemberAuthEntity memberAuth = auth.get();
			result = new Result(txid, Result.OK).putValue("valifyToken",true);
			log.info("token : " + memberAuth.toString());
		} catch (Exception e) {
			result = new Result(txid, Result.UNAUTHORIZED).putValue("valifyToken",false);
			e.printStackTrace();
		}
		return result;
	}
	
	public Result invalidateToken(Long id, String txid) throws Byung8Exception {
		Result result = null;
		try {
			redisRepository.deleteById(id);
			log.info(id+" was deleted!");
			result = new Result(txid, Result.OK).putValue("invalidateToken",true);
		} catch (Exception e) {
			result = new Result(txid, Result.OK).putValue("invalidateToken",false);
			e.printStackTrace();
		}
		return result;
	}
	
	private MemberAuthEntity fetchMemberAuth(Member member) throws Byung8Exception {
		RestTemplate restTemplate = new RestClientTemplate(connectionTimeout, readTimeout);
		MemberAuthEntity memberAuth = null;
		
		Map<String,String> params = new HashMap<String,String>();
				
		StringBuilder sb = new StringBuilder();
		sb.append("http://")
			.append(server)
			.append(":")
			.append(port)
			.append("/membership/member/")
			.append(member.getEmail())
			.append("/email");
		log.info("issueToken : "+sb.toString());
//		params.put("email", member.getEmail());
//		ResponseEntity<Result> res = restTemplate.exchange(sb.toString(), HttpMethod.GET, null, Result.class, params);

		Result res = restTemplate.getForObject(sb.toString(), Result.class);
		
		log.info("res : "+res.toJson());
		if (res == null || !res.isOK()) {
			throw new Byung8Exception("Unknown Member(email:"+member.getEmail()+")");
		}
		
		log.info("member : "+res.getResult().get("member"));
		
		ObjectMapper mapper = new ObjectMapper();
		Member ret = null;
		try {
			ret = mapper.convertValue(res.getResult().get("member"), Member.class);
			log.info("ret : " +ret.toString());
		} catch( Exception e) {
			e.printStackTrace();
		}

		if (!member.getEmail().equals(ret.getEmail()) || ! member.getPassword().equals(ret.getPassword())) {
			throw new Byung8Exception("Password Not Equal("+member.getEmail()+")");
		}
		memberAuth = new MemberAuthEntity();
		memberAuth.setBirthday(ret.getBirthday());
		memberAuth.setEmail(ret.getEmail());
		memberAuth.setExpiredTime(System.currentTimeMillis()+600000);
		memberAuth.setGenderId(ret.getGenderId());
		memberAuth.setGenderNm(ret.getGenderNm());
		memberAuth.setHeight(ret.getHeight());
		memberAuth.setMemberId(ret.getMemberId());
		memberAuth.setName(ret.getName());
		redisRepository.save(memberAuth);
		return memberAuth;
	}
}
