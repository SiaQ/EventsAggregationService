package pl.jg.eas.dtos;

import lombok.Getter;

@Getter
public class SearchOption {
    private String optionValue;
    private String optionName;

    public SearchOption(String optionValue, String criteriaName) {
        this.optionValue = optionValue;
        this.optionName = criteriaName;
    }
}
