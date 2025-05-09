package com.wheel.wheelPicker.service;

import com.wheel.wheelPicker.model.WheelConfiguration;
import com.wheel.wheelPicker.repository.WheelConfigurationRepository;
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
