package com.github.adetiamarhadi.xyz_auth_service.util;

import com.github.f4b6a3.uuid.UuidCreator;

public class UUID {

    public static String generate() {
        return UuidCreator.getTimeOrderedEpoch().toString();
    }
}
