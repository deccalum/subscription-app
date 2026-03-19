package se.lexicon.subscriptionapi.domain.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import se.lexicon.subscriptionapi.domain.constant.SubscriptionStatus;


@Getter @Setter
@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column(nullable = false, updatable = false, name = "write_instant")
    @CreationTimestamp
    private Instant writeInstant;

    @Column(name = "cancel_instant")
    private Instant cancelInstant;
}
