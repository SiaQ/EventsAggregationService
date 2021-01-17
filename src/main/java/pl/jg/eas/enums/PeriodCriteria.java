package pl.jg.eas.enums;

public enum PeriodCriteria {
    FUTURE("Future"),
    CURRENT_AND_FUTURE("Current and future"),
    ALL("All");

    String criteriaName;

    PeriodCriteria(String periodCriteria) {
        this.criteriaName = periodCriteria;
    }

    public String getCriteriaName() {
        return criteriaName;
    }

    @Override
    public String toString() {
        return criteriaName;
    }
}
