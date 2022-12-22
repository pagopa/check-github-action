package src.main.java.it.gov.pagopa.afm.calculator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Builder
public class Touchpoint {
    private String id;
    private String name;
    private LocalDateTime creationDate;
}
