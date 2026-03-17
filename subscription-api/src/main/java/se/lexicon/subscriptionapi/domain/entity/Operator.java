package se.lexicon.subscriptionapi.domain.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "operators")
public class Operator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "operator", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Plan> plans = new HashSet<>();

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;
}
