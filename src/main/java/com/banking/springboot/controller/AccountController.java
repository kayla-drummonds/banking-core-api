package com.banking.springboot.controller;

import com.banking.springboot.dto.AccountDto;
import com.banking.springboot.entity.Account;
import com.banking.springboot.exceptions.AccountDoesNotExistException;
import com.banking.springboot.exceptions.CustomError;
import com.banking.springboot.exceptions.CustomerDoesNotExistException;
import com.banking.springboot.service.impl.AccountServiceImpl;
import com.banking.springboot.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/user")
@CrossOrigin("http://localhost:3000")
@Slf4j
public class AccountController {

	private AccountServiceImpl accountService;

	@Autowired
	private Utility utility;

	public AccountController(AccountServiceImpl accountService) {
		super();
		this.accountService = accountService;
	}


	@GetMapping("/accounts/{id}")
	public ResponseEntity<Object> getAccountById(@PathVariable Integer id) {
		try {
			AccountDto account = accountService.getAccountById(id);
			return new ResponseEntity<>(account, HttpStatus.OK);
		} catch (AccountDoesNotExistException ae) {
			log.error("Error inside getAccountById: {}", ae.getMessage());
			return new ResponseEntity<>(ae.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Error inside getAccountById: {}", e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// get all accounts
	@GetMapping("/accounts")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Object> listAccounts() {
		log.info("Inside listAccounts");
		List<AccountDto> accounts = accountService.getAllAccounts();
		return new ResponseEntity<>(accounts, HttpStatus.OK);
	}

	// filter accounts by type
	@GetMapping("/accounts/search")
	public ResponseEntity<Object> listAccountsByProductType(@RequestParam("type") String type) {
		try {
			log.info("Inside listAccountsByProductType: {}", type);
			List<AccountDto> accountsByProduct = accountService.getAccountsByProductType(type);
			return new ResponseEntity<>(accountsByProduct, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error inside listAccountsByProductType: {}", e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/accounts/customer/{id}")
	public ResponseEntity<Object> listAccountsByCustomerId(@PathVariable Integer id) {
		try {
			log.info("Inside getAccountsByCustomerId: {}", id);
			List<AccountDto> accountsByCustomer = accountService.getAccountsByCustomerId(id);
			return new ResponseEntity<>(accountsByCustomer, HttpStatus.OK);
		} catch (CustomerDoesNotExistException ce) {
			log.error("Error inside listAccountsByCustomerId: {}", ce.getMessage());
			return new ResponseEntity<>(ce.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Error inside listAccountsByCustomerId: {}", e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// create a new account object
	@PostMapping("/accounts/new")
	public ResponseEntity<Object> createAccount(@RequestBody @Valid AccountDto accountDto, BindingResult bindingResult) {
		log.info("Inside createAccount");
		try {
			if(!bindingResult.hasErrors()) {
				Account newAccount = accountService.saveAccount(accountDto);
				return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
			} else {
				List<CustomError> allErrors = utility.listAllCustomErrors(bindingResult);
				log.error("Error creating account: {}", allErrors);
				return new ResponseEntity<>(allErrors, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error("Error inside createAccount: {}", e.getMessage());
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// activate/deactivate an account object
	@PutMapping("/accounts/status/{id}")
	public ResponseEntity<Object> toggleAccount(@PathVariable Integer id, @RequestBody AccountDto data) {
		log.info("Inside toggleAccount: {}", data);
		try {
			AccountDto existingAccount = accountService.toggleAccountStatus(id, data);
			return new ResponseEntity<>(existingAccount, HttpStatus.OK);
		} catch (JsonProcessingException e) {
			log.info("Error inside toggleAccount: {}", e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.info("Error inside toggleAccount: {}", e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
