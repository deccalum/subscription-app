package se.lexicon.subscriptionapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Entity
@DiscriminatorValue("INTERNET")
public class PlanInternet extends Plan {

    @Embedded
    private Speed speed;

    @Embeddable
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Speed {
        @Column(name = "upload_speed_mbps")
        private Integer upload;

        @Column(name = "download_speed_mbps")
        private Integer download;
    }
}