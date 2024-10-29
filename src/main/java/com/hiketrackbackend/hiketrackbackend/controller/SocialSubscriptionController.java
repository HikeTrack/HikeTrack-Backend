package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.socialSubscription.SubscriptionRequestDto;
import com.hiketrackbackend.hiketrackbackend.service.SocialSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/social")
@RequiredArgsConstructor
@Validated
@Tag(name = "", description = "")
public class SocialSubscriptionController {
    private final SocialSubscriptionService socialSubscriptionService;

    @PostMapping("/subscribe")
    @Operation(summary = "", description = "")
    public UserDevMsgRespondDto createRegularEmailNewsletterSubscription(@RequestBody @Valid SubscriptionRequestDto requestDto) {
        return socialSubscriptionService.create(requestDto);
    }

    @PostMapping("/unsubscribe")
    @Operation(summary = "", description = "")
    public UserDevMsgRespondDto deleteRegularEmailNewsletterSubscription(@RequestParam("email") String email) {
        return socialSubscriptionService.delete(email);
    }
}
