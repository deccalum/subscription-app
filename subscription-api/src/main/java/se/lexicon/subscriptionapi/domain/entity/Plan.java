package se.lexicon.subscriptionapi.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import se.lexicon.subscriptionapi.domain.constant.PlanKind;
import se.lexicon.subscriptionapi.domain.constant.PlanStatus;

@Getter @Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "plan_kind")
@Table(name = "plans")
public abstract class Plan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_kind", insertable = false, updatable = false)
    private PlanKind kind;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", nullable = false)
    private Operator operator;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    // difference month cost from one-time fee, if applicable

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status;
}