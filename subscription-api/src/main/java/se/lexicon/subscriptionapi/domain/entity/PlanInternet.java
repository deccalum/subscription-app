package se.lexicon.subscriptionapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@DiscriminatorValue("INTERNET")
public class PlanInternet extends Plan {
    @Column(name = "upload_speed_mbps")
    private Integer uploadSpeedMbps;

    @Column(name = "download_speed_mbps")
    private Integer downloadSpeedMbps;
}