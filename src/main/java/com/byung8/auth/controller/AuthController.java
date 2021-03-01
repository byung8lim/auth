package com.byung8.auth.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.byung8.common.domain.Member;
import com.byung8.auth.service.AuthService;
import com.byung8.common.domain.IResult;
import com.byung8.common.domain.Result;
import com.byung8.common.exception.Byung8Exception;

import lombok.extern.slf4j.Slf4j;

/**
 * RestServiceController class
 * 
 * @author 06919
 *
 */
@RestController
@Slf4j
@RequestMapping(value = "/auth")
public class AuthController {

	@Autowired
	AuthService authService;
	
	private String txId() {
		return UUID.randomUUID().toString();
	}

	@RequestMapping(value = "/token/issue", method = RequestMethod.POST)
	public ResponseEntity<String> issueToken(@RequestBody final Member member) {

		log.debug("issue token : "+member.toString());
		String txid = txId();
		try {
			Result result = authService.issueToken(member, txid);
			return new ResponseEntity<String>(result.toJson(), result.status());
		} catch (Exception e) {
			log.error("Failed to issue Token", e);
			if (e instanceof NullPointerException) {
				return new ResponseEntity<String>(HttpStatus.EXPECTATION_FAILED);
			} else if (e instanceof Byung8Exception) {
				Result result = new Result(txid, IResult.ERROR, "").putValue("error", e.getMessage());
				return new ResponseEntity<String>(result.toJson(), HttpStatus.EXPECTATION_FAILED);
			} else {
				Result result = new Result(txid, IResult.ERROR, "").putValue("error", e);
				return new ResponseEntity<String>(result.toJson(), HttpStatus.EXPECTATION_FAILED);
			}
		}
	}
	
	@RequestMapping(value = "/token/{token}", method = RequestMethod.POST)
	public ResponseEntity<String> verifyAuth(@PathVariable("token") final Long token) {

		String txid = txId();
		try {
			Result result = authService.verifyToken(token, txid);
			return new ResponseEntity<String>(result.toJson(), result.status());
		} catch (Exception e) {
			log.error("Failed to check Token", e);
			if (e instanceof NullPointerException) {
				return new ResponseEntity<String>(HttpStatus.EXPECTATION_FAILED);
			} else if (e instanceof Byung8Exception) {
				Result result = new Result(txid, IResult.ERROR, "").putValue("error", e.getMessage());
				return new ResponseEntity<String>(result.toJson(), HttpStatus.EXPECTATION_FAILED);
			} else {
				Result result = new Result(txid, IResult.ERROR, "").putValue("error", e);
				return new ResponseEntity<String>(result.toJson(), HttpStatus.EXPECTATION_FAILED);
			}
		}
	}
	
	@RequestMapping(value = "/token/{token}", method = RequestMethod.DELETE)
	public ResponseEntity<String> invalidate(@PathVariable("token") final Long token) {

		String txid = txId();
		try {
			Result result = authService.invalidateToken(token, txid);
			return new ResponseEntity<String>(result.toJson(), result.status());
		} catch (Exception e) {
			log.error("Failed to register FitnessMember", e);
			if (e instanceof NullPointerException) {
				return new ResponseEntity<String>(HttpStatus.EXPECTATION_FAILED);
			} else if (e instanceof Byung8Exception) {
				Result result = new Result(txid, IResult.ERROR, "").putValue("error", e.getMessage());
				return new ResponseEntity<String>(result.toJson(), HttpStatus.EXPECTATION_FAILED);
			} else {
				Result result = new Result(txid, IResult.ERROR, "").putValue("error", e);
				return new ResponseEntity<String>(result.toJson(), HttpStatus.EXPECTATION_FAILED);
			}
		}
	}
}
