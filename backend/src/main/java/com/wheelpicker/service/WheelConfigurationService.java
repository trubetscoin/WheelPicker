package com.wheelpicker.service;

import com.wheelpicker.model.WheelConfiguration;
import com.wheelpicker.repository.WheelConfigurationRepository;
import org.springframework.stereotype.Service;

@Service
public class WheelConfigurationService {
    private final WheelConfigurationRepository wheelConfigurationRepository;

    public WheelConfigurationService(WheelConfigurationRepository wheelConfigurationRepository) {
        this.wheelConfigurationRepository = wheelConfigurationRepository;
    }

    public WheelConfiguration createWheelConfiguration(WheelConfiguration wheelConfiguration) {
        return wheelConfigurationRepository.save(wheelConfiguration);
    }
}
