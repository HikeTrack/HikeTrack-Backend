package com.hiketrackbackend.hiketrackbackend.controller;

import com.hiketrackbackend.hiketrackbackend.dto.UserDevMsgRespondDto;
import com.hiketrackbackend.hiketrackbackend.dto.subscription.SubscriptionRequestDto;
import com.hiketrackbackend.hiketrackbackend.service.SocialSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/socials")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Social Subscriptions",
        description = "Operations related to managing "
                + "social subscriptions and newsletters distribution.")
public class SocialSubscriptionController {
    private final SocialSubscriptionService socialSubscriptionService;

    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe to Newsletter",
            description = "Subscribe to the email newsletter using the provided email address.")
    public UserDevMsgRespondDto createEmailNewsletterSubscription(
            @RequestBody @Valid SubscriptionRequestDto requestDto
    ) {
        return socialSubscriptionService.create(requestDto);
    }

    @PostMapping("/unsubscribe")
    @Operation(summary = "Unsubscribe from Newsletter",
            description = "Unsubscribe from the email newsletter using the provided email address.")
    public UserDevMsgRespondDto deleteEmailNewsletterSubscription(
            @RequestParam("email") String email
    ) {
        return socialSubscriptionService.delete(email);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/newsletter")
    @Operation(
            summary = "Send Newsletter",
            description = "Allows an admin to send a newsletter to all subscribers."
    )
    public UserDevMsgRespondDto sendNewsletterToAllSubs(@RequestBody String text) {
        return socialSubscriptionService.createNewsletter(text);
    }
}
