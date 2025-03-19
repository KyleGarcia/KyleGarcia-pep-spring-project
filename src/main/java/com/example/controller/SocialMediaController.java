package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.service.AccountService;
import com.example.service.MessageService;
import com.example.entity.Account;
import com.example.entity.Message;
import java.util.Optional;
import java.util.List;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

@RestController
@RequestMapping
public class SocialMediaController {

    // Register User Endpoint
    private final AccountService accountService;
    private final MessageService messageService;

   
    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    @PostMapping("/register")
    public ResponseEntity<Account> registerUser(@RequestBody Account account) {
        Account newAccount = accountService.registerUser(account);
        if(newAccount == null) {
            return ResponseEntity.status(409).build();
        }
        return ResponseEntity.ok(newAccount);
    }

    // Login a User Endpoint
    @PostMapping("/login")
    public ResponseEntity<Account> loginUser(@RequestBody Account account) {
        Account authenticatedUser = accountService.loginUser(account.getUsername(), account.getPassword() );

        if (authenticatedUser == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(authenticatedUser);
    }

    // Create a Message Endpoint
    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        try { 
            if (message.getMessageText() == null || message.getMessageText().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            if (message.getMessageText().length() > 255) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            Optional<Account> account = accountService.findAccountById(message.getPostedBy() );
            if (!account.isPresent() ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            Message savedMessage = messageService.createMessage(message);
            if (savedMessage == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            return ResponseEntity.status(HttpStatus.OK).body(savedMessage);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }     
    }

    // Get all Messages
    @GetMapping("/messages")
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }

    // Get Message By ID
    @GetMapping("/messages/{messageId}")
    public Message getMessageById(@PathVariable Integer messageId) {
        Optional<Message> message = messageService.getMessageById(messageId);
        return message.orElse(null);
    }

    // Delete Message By ID
    @DeleteMapping("/messages/{messageId}") 
    public ResponseEntity<String> deleteMessage(@PathVariable Integer messageId) {
        boolean exists = messageService.deleteMessageById(messageId);

        if (exists) {
            return ResponseEntity.ok("1");
        } else {
            return ResponseEntity.ok("");
        }
    }

    // Update Message By ID
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<String> updateMessageText(@PathVariable Integer messageId, @RequestBody Message updatedMessage) {

        if (updatedMessage.getMessageText() == null || updatedMessage.getMessageText().trim().isEmpty() ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Check if message exists
        Optional<Message> existingMessage = messageService.getMessageById(messageId);
        if (!existingMessage.isPresent() ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Check size of message is valid
        if (updatedMessage.getMessageText().length() > 255) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        int rowsUpdated = messageService.updateMessageText(messageId, updatedMessage.getMessageText() );
            if (rowsUpdated == 1) {
                return ResponseEntity.ok("1");

            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByUser(@PathVariable Integer accountId) {
        List<Message> messages = messageService.getMessagesByUser(accountId);

        return ResponseEntity.ok(messages);
    }
}
