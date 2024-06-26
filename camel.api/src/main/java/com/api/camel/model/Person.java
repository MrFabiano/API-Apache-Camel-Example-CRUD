package com.api.camel.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @JsonIgnore
    private int ageInWeeks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAgeInWeeks() {
        return ageInWeeks;
    }

    public void setAgeInWeeks(int ageInWeeks) {
        this.ageInWeeks = ageInWeeks;
    }

    @JsonProperty("age")
    public String getAge() {
        if (ageInWeeks < 4) {
            return ageInWeeks + " weeks";
        } else if (ageInWeeks < 52) {
            int months = ageInWeeks / 4;
            return months + " months";
        } else {
            int years = ageInWeeks / 52;
            int remainingWeeks = ageInWeeks % 52;
            int months = remainingWeeks / 4;
            return years + " years" + (months > 0 ? " and " + months + " months" : "");
        }
    }

    @JsonProperty("age")
    public void setAge(String age) {
        if (age.endsWith("weeks")) {
            this.ageInWeeks = Integer.parseInt(age.replace(" weeks", "").trim());
        } else if (age.endsWith("months")) {
            int months = Integer.parseInt(age.replace(" months", "").trim());
            this.ageInWeeks = months * 4;
        } else if (age.endsWith("years")) {
            this.ageInWeeks = Integer.parseInt(age.replace(" years", "").trim()) * 52;
        } else if (age.contains("years") && age.contains("months")) {
            String[] parts = age.split("and");
            int years = Integer.parseInt(parts[0].replace(" years", "").trim());
            int months = Integer.parseInt(parts[1].replace(" months", "").trim());
            this.ageInWeeks = years * 52 + months * 4;
        } else {
            throw new IllegalArgumentException("Invalid age format");
        }
    }
}
