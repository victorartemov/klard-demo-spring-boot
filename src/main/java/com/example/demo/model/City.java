package com.example.demo.model;

import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class City {
    @Id
    private String id;
    private String cityName;
    private String districtName;
    private String regionName;

    @ElementCollection
    private Set<String> zips = new HashSet<>();
}
