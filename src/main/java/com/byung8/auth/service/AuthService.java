package com.byung8.auth.service;

import com.byung8.common.domain.Member;
import com.byung8.common.domain.Result;
import com.byung8.common.exception.Byung8Exception;

public interface AuthService {
	public Result issueToken(Member member, String txid) throws Byung8Exception;	
	public Result verifyToken(Long id, String txid) throws Byung8Exception;	
	public Result invalidateToken(Long id, String txid) throws Byung8Exception;
}
