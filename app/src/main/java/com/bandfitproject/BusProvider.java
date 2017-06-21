package com.bandfitproject;

import com.squareup.otto.Bus;

public final class BusProvider {
    private static final Bus ABUS =  new Bus();

    public static Bus getInstance() {
        return ABUS;
    }

    private BusProvider() {

    }
}
