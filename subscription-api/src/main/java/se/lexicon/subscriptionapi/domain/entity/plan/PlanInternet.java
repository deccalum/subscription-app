package se.lexicon.subscriptionapi.domain.entity.plan;

import jakarta.persistence.*;
import lombok.*;
import se.lexicon.subscriptionapi.domain.entity.Plan;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Entity
@DiscriminatorValue("INTERNET")
public class PlanInternet extends Plan {

    // Stored as a single "upload:download" column via SpeedConverter
    @Convert(converter = PlanInternet.SpeedConverter.class)
    @Column(name = "speed")
    private Speed speed;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Speed {
        private Integer upload;
        private Integer download;
    }

    @Converter
    public static class SpeedConverter implements AttributeConverter<Speed, String> {
        @Override
        public String convertToDatabaseColumn(Speed speed) {
            if (speed == null) return null;
            String up = speed.getUpload() == null ? "" : speed.getUpload().toString();
            String down = speed.getDownload() == null ? "" : speed.getDownload().toString();
            return up + ":" + down;
        }

        @Override
        public Speed convertToEntityAttribute(String column) {
            if (column == null || column.isBlank()) return null;
            String[] parts = column.split(":", -1);
            Integer upload = parts[0].isBlank() ? null : Integer.parseInt(parts[0]);
            Integer download = parts.length > 1 && !parts[1].isBlank() ? Integer.parseInt(parts[1]) : null;
            return new Speed(upload, download);
        }
    }
}
