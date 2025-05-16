package com.wheelpicker.repository;

import com.wheelpicker.model.WheelConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WheelConfigurationRepository extends JpaRepository<WheelConfiguration, UUID> {

}
