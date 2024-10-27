package com.wheel.wheelPicker.model;

import jakarta.persistence.*;

@Entity
public class WheelConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String json;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}
