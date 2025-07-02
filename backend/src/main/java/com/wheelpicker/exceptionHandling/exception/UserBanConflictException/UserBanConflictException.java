package com.wheelpicker.exceptionHandling.exception.UserBanConflictException;

public class UserBanConflictException extends RuntimeException {
    private final BanUnbanType banUnbanType;

    public UserBanConflictException(BanUnbanType banUnbanType, String message) {
        super(message);
        this.banUnbanType = banUnbanType;
    }

    public BanUnbanType getBanType() {
        return banUnbanType;
    }

    public String getTitle() {
        if (banUnbanType == BanUnbanType.BAN) return "User cannot be banned";
        return "User cannot be unbanned";
    }
}
