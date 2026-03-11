package com.wechat.acquisition.domain.acquisition;

public class AgeRange {
    private Integer minAge;
    private Integer maxAge;
    
    public AgeRange() {}
    public Integer getMinAge() { return minAge; }
    public void setMinAge(Integer minAge) { this.minAge = minAge; }
    public Integer getMaxAge() { return maxAge; }
    public void setMaxAge(Integer maxAge) { this.maxAge = maxAge; }
    
    public boolean contains(Integer age) {
        if (age == null) return true;
        if (minAge != null && age < minAge) return false;
        if (maxAge != null && age > maxAge) return false;
        return true;
    }
}
