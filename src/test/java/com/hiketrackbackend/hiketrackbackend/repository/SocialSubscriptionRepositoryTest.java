package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.SocialSubscription;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SocialSubscriptionRepositoryTest {
    @Autowired
    private SocialSubscriptionRepository socialSubscriptionRepository;

    @Test
    @DisplayName("If subs is exist by mail")
    public void testExistsByEmailWhenEmailExistsThenReturnTrue() {
        SocialSubscription subscription = new SocialSubscription();
        subscription.setEmail("test@example.com");
        socialSubscriptionRepository.save(subscription);

        boolean exists = socialSubscriptionRepository.existsByEmail("test@example.com");

        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Is exist by mail negative result")
    public void testExistsByEmailWhenEmailDoesNotExistThenReturnFalse() {
        boolean exists = socialSubscriptionRepository.existsByEmail("nonexistent@example.com");

        Assertions.assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Delete by email")
    public void testDeleteByEmailWhenEmailExistsThenDeleteEntity() {
        SocialSubscription subscription = new SocialSubscription();
        subscription.setEmail("delete@example.com");
        socialSubscriptionRepository.save(subscription);

        socialSubscriptionRepository.deleteByEmail("delete@example.com");

        boolean exists = socialSubscriptionRepository.existsByEmail("delete@example.com");
        Assertions.assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Delete by email with not exists email")
    public void testDeleteByEmailWhenEmailDoesNotExistThenDoNothing() {
        long initialCount = socialSubscriptionRepository.count();

        socialSubscriptionRepository.deleteByEmail("nonexistent@example.com");

        long finalCount = socialSubscriptionRepository.count();
        Assertions.assertThat(finalCount).isEqualTo(initialCount);
    }

    @Test
    @DisplayName("Find all subs when DB is empty")
    public void testFindAllEmailsWhenNoSubscriptionsThenReturnEmptyList() {
        socialSubscriptionRepository.deleteAll();

        List<String> emails = socialSubscriptionRepository.findAllEmails();

        Assertions.assertThat(emails).isEmpty();
    }

    @Test
    @DisplayName("Find all subs emails")
    public void testFindAllEmailsWhenSubscriptionsExistThenReturnEmails() {
        SocialSubscription subscription1 = new SocialSubscription();
        subscription1.setEmail("email1@example.com");
        socialSubscriptionRepository.save(subscription1);

        SocialSubscription subscription2 = new SocialSubscription();
        subscription2.setEmail("email2@example.com");
        socialSubscriptionRepository.save(subscription2);

        List<String> emails = socialSubscriptionRepository.findAllEmails();

        Assertions.assertThat(emails).containsExactlyInAnyOrder("email1@example.com", "email2@example.com");
    }
}