package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.entity.Message;
import com.example.entity.Account;
import com.example.repository.MessageRepository;
import com.example.service.MessageService;
import java.util.List;
import java.util.Optional;


@Service
public class MessageService {

    private MessageRepository messageRepository;
    private AccountService accountService;

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountService accountService){
        this.messageRepository = messageRepository;
        this.accountService = accountService;
    }

    // Create Message
    public Message createMessage (Message message) {

        if (message.getMessageText() == null || message.getMessageText().trim().isEmpty() ) {
            return null;
        }
        if (message.getMessageText().length() > 255) {
            return null;
        }

        Optional<Account> account = accountService.findAccountById(message.getPostedBy() );
        if (!account.isPresent() ) {
            return null;
        }
        return messageRepository.save(message);
             
    }

    // Get all Messages
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    // Get Message By ID
    public Optional<Message> getMessageById(Integer messageId){
        return messageRepository.findById(messageId);
    }

    // Delete Message By ID
    public boolean deleteMessageById(Integer messageId) {
        Optional<Message> message = messageRepository.findById(messageId);
        if (message.isPresent() ) {        
            return true;
        }
        return false;
    }

    // Update Message By ID
    public int updateMessageText(Integer messageId, String newMessageText) {
        Optional<Message> messageOptional = messageRepository.findById(messageId);
        if (messageOptional.isPresent() ) {
            Message message = messageOptional.get();
            if(newMessageText != null && !newMessageText.trim().isEmpty() && newMessageText.length() <= 255) {
                message.setMessageText(newMessageText);
                messageRepository.save(message);
                return 1;
            }
        }
        return 0;
    }

    // Get Messages By ID
    public List<Message> getMessagesByUser(Integer userId){
        return messageRepository.findByPostedBy(userId);
    }
    

}
