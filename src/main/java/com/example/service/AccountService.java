package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.entity.Account;
import com.example.repository.AccountRepository;
import com.example.service.AccountService;
import java.util.Optional;

@Service 
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService (AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    // Handle Logging in
    public Account loginUser (String username, String password) {

        Optional<Account> existingAccount = accountRepository.findByUsername(username);
        if (existingAccount.isPresent() && existingAccount.get().getPassword().equals(password) ) {
            return existingAccount.get();
        }
        
        // Login failure
        return null;
    }

    public Account registerUser(Account account) {

        // Check if username is null / empty
        if (account.getUsername() == null || account.getUsername().trim().isEmpty() ) {
            return null;
            // Pass to Controller to handle the error
        }

        // Check for valid Password
        if (account.getPassword() == null || account.getPassword().length() < 5) {
            return null;
        }

        // Check if account already exists
        Optional<Account> existingAccount = accountRepository.findByUsername(account.getUsername() );
        if (existingAccount.isPresent() ) {
            return null;
        }

        // Save the account
        return accountRepository.save(account);
    }

    // Find account by ID
    public Optional<Account> findAccountById(Integer accountId) {
        
        return accountRepository.findById(accountId);
    }
}
