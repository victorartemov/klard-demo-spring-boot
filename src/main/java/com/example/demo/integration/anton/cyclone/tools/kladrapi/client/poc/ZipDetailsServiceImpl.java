package com.example.demo.integration.anton.cyclone.tools.kladrapi.client.poc;

import com.example.demo.integration.anton.cyclone.tools.kladrapi.client.KladrApiClient;
import com.example.demo.integration.anton.cyclone.tools.kladrapi.client.KladrObject;
import org.springframework.stereotype.Service;

@Service
public class ZipDetailsServiceImpl implements ZipDetailsService {

    private KladrApiClient kladrApiClient;

    public ZipDetailsServiceImpl(KladrApiClient kladrApiClient) {
        this.kladrApiClient = kladrApiClient;
    }

    @Override
    public ZipDetails resolveZip(String zipCode) {

        KladrObject kladrObject = kladrApiClient.getBuildingWithParentsByZipCode(zipCode);

        if (kladrObject == null) {
            return null;
        }

        String cityId = null, cityName = null, districtName = null, regionName = null;

        for (KladrObject parent : kladrObject.getParents()) {
            switch (parent.getContentType()) {
                case "region":
                    regionName = parent.getName();
                    break;
                case "district":
                    districtName = parent.getName();
                    break;
                case "city":
                    cityId = parent.getId();
                    cityName = parent.getName();
                    break;
                default:
                    break;
            }
        }

        return new ZipDetails(cityId, cityName, districtName, regionName);
    }
}
