package se.lexicon.subscriptionapi.domain.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import se.lexicon.subscriptionapi.domain.constant.UserRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    @ToString.Exclude
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "customer_roles", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @ToString.Exclude
    private Set<UserRole> roles = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_detail_id")
    @ToString.Exclude
    private CustomerDetail customerDetail;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Subscription> subscriptions = new HashSet<>();

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;
}
