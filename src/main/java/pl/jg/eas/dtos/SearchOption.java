package pl.jg.eas.dtos;

public class SearchOption {
    private String optionName;

    public SearchOption(String criteriaName) {
        this.optionName = criteriaName;
    }

    public String getOptionName() {
        return optionName;
    }
}
