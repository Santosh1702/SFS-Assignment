package com.sfs.config;

import com.sfs.entities.Plant;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "sfs.plants")
public class PlantConfig {

    private List<Plant> list;

    public List<Plant> getList() {
        return list;
    }

    public void setList(List<Plant> list) {
        this.list = list;
    }
}