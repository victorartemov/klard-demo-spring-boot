package com.example.demo.service;

import com.example.demo.integration.anton.cyclone.tools.kladrapi.client.poc.ZipDetails;
import com.example.demo.integration.anton.cyclone.tools.kladrapi.client.poc.ZipDetailsService;
import com.example.demo.model.City;
import com.example.demo.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;


@Service
public class CityKladrService {

    Logger LOG = Logger.getLogger(CityKladrService.class.getName());

    private ZipDetailsService zipDetailsService;

    private CityRepository cityRepository;

    public CityKladrService(ZipDetailsService zipDetailsService, CityRepository cityRepository) {
        this.zipDetailsService = zipDetailsService;
        this.cityRepository = cityRepository;
    }

    public void makeKladrCalls(){
        Map<String, City> cityToDeliverMap = new HashMap<>();
        List<String> zips = new ArrayList<>(50000);
        List<String> failedZip = new ArrayList<>();

        long readFromFileStart = System.currentTimeMillis();
        try {
            Path path = Paths.get(getClass().getClassLoader().getResource("zips.txt").toURI());
            Stream<String> lines = Files.lines(path);
            lines.forEach(zips::add);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        long readFromFileFinish = System.currentTimeMillis();
        long readFromFileTime = readFromFileFinish - readFromFileStart;


        long workWithZipServiceStart = System.currentTimeMillis();
        for (String zip : zips) {
            ZipDetails zipDetails = zipDetailsService.resolveZip(zip);
            if (zipDetails != null && zipDetails.getCityName() != null) {
                if (cityToDeliverMap.containsKey(zipDetails.getCityName())) {
                    City cityToDeliver = cityToDeliverMap.get(zipDetails.getCityName());
                    cityToDeliver.getZips().add(zip);
                } else {
                    City cityToDeliver = new City();
                    cityToDeliver.setId(zipDetails.getCityId());
                    cityToDeliver.setCityName(zipDetails.getCityName());
                    cityToDeliver.setDistrictName(zipDetails.getDistrictName());
                    cityToDeliver.setRegionName(zipDetails.getRegionName());
                    cityToDeliver.setZips(new HashSet<>(Arrays.asList(zip)));
                    cityToDeliverMap.put(zipDetails.getCityName(), cityToDeliver);
                }
                System.out.println(zipDetails.getCityId() + " with ZIP " + zip + " parsed");
            } else if(zipDetails == null) {
                failedZip.add(zip);
            }
        }
        long workWithZipServiceFinish = System.currentTimeMillis();
        long workWithZipServiceTime = workWithZipServiceFinish - workWithZipServiceStart;


        long saveToBaseStart = System.currentTimeMillis();
        cityRepository.saveAll(cityToDeliverMap.values());
        long saveToBaseFinish = System.currentTimeMillis();
        long saveToBaseTime = saveToBaseFinish - saveToBaseStart;

        String result = String.format("[RESULTS] Zips count = %s, read from file time = %s sec, work with zip codes time = %s, save to base time = %s, cites count = %s",
                zips.size(), readFromFileTime / 1000, workWithZipServiceTime / 1000, saveToBaseTime/1000, cityToDeliverMap.size());

        System.out.println(result);

        failedZip.add(result);

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! failed zips: " + failedZip);
    }
}
