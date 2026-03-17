package se.lexicon.subscriptionapi.domain.entity;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.persistence.*;
import lombok.*;

import se.lexicon.subscriptionapi.domain.constant.PlanStatus;
import se.lexicon.subscriptionapi.domain.constant.ServiceType;

@Getter
@Setter
@Entity
@Table(
    name = "plans",
    uniqueConstraints = @UniqueConstraint(columnNames = {"operator_id", "name"})
)
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", nullable = false)
    private Operator operator;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, scale = 2, precision = 10)
    private BigDecimal price;

    @Column
    private BigInteger limit; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status;
}
