package pl.jg.eas.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventApiInfoDto {
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String shortDescription;
}