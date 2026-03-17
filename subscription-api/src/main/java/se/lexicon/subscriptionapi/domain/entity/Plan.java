package se.lexicon.subscriptionapi.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "plan_kind")
@Table(name = "plans")
public abstract class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", nullable = false)
    private Operator operator;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status;
}