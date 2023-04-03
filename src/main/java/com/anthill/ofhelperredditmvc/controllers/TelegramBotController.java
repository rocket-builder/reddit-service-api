package com.anthill.ofhelperredditmvc.controllers;

import com.anthill.ofhelperredditmvc.domain.User;
import com.anthill.ofhelperredditmvc.domain.dto.NewsletterDto;
import com.anthill.ofhelperredditmvc.domain.dto.TelegramUserDto;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.exceptions.TelegramBotServiceException;
import com.anthill.ofhelperredditmvc.services.TelegramBotService;
import com.anthill.ofhelperredditmvc.services.rest.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Telegram Bot")
@RequestMapping("/telegram")
@RestController
public class TelegramBotController {

    private final UserService userService;
    private final TelegramBotService botService;

    public TelegramBotController(UserService userService,
                                 TelegramBotService botService) {
        this.userService = userService;
        this.botService = botService;
    }

    @PostMapping("/newsletter")
    public ResponseEntity<String> sendNewsletter(@RequestBody String message) throws TelegramBotServiceException {
        var telegramIds = userService.findAllTelegramIdsForNewsletter();

        if(!telegramIds.isEmpty()){
            var newsletter = NewsletterDto.builder()
                    .telegramIds(telegramIds)
                    .message(message)
                    .build();

            botService.sendNewsletter(newsletter);
        }

        return new ResponseEntity<>("Successfully sent!", HttpStatus.OK);
    }

    @PostMapping("/signUp")
    public ResponseEntity<User> signUpTelegramUser(@RequestBody TelegramUserDto telegram)
            throws ResourceNotFoundedException {

        var user = userService.signUpTelegram(telegram);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{telegramId}/upVoteBalance")
    public ResponseEntity<Integer> getUpVoteBalance(@PathVariable("telegramId") long telegramId)
            throws ResourceNotFoundedException {
        if(telegramId == 0){
            throw new ResourceNotFoundedException();
        }

        var balance = userService.findUpVoteBalanceByTelegramId(telegramId);

        return new ResponseEntity<>(balance, HttpStatus.OK);
    }
}
