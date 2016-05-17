package edu.oregonstate.mist.api

import com.codahale.metrics.health.HealthCheck

public class NullHealthCheck extends HealthCheck {
    @Override
    protected HealthCheck.Result check() throws Exception {
        return HealthCheck.Result.healthy()
    }
}
