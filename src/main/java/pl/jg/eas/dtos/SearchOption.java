package pl.jg.eas.dtos;

public class SearchOption {
    private String optionValue;
    private String optionName;

    public SearchOption(String optionValue, String criteriaName) {
        this.optionValue = optionValue;
        this.optionName = criteriaName;
    }

    public String getOptionName() {
        return optionName;
    }

    public String getOptionValue() {
        return optionValue;
    }
}
