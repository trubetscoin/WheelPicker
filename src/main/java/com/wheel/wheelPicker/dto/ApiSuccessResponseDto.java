package com.wheel.wheelPicker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiSuccessResponseDto<T> {
    private int status;
    private String message;
    private T data;
}
