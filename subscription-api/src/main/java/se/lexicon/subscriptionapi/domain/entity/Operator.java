package se.lexicon.subscriptionapi.domain.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "operators")
public class Operator {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "operator", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Plan> plans = new HashSet<>();

    @Column(nullable = false, updatable = false, name = "write_instant")
    @CreationTimestamp
    private Instant writeInstant;
}
