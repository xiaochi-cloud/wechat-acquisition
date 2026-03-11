package com.wechat.acquisition.domain.acquisition;
import java.util.Map;
@Data
public class ProfileData {
    private String gender; private Integer age; private String city;
    private String industry; private String company; private String position;
    private Double incomeLevel; private Map<String, Object> extras;
    public ProfileData() {}
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public Double getIncomeLevel() { return incomeLevel; }
    public void setIncomeLevel(Double incomeLevel) { this.incomeLevel = incomeLevel; }
    public Map<String, Object> getExtras() { return extras; }
    public void setExtras(Map<String, Object> extras) { this.extras = extras; }
}
