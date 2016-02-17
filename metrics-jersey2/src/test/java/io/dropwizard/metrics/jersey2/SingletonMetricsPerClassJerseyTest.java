package io.dropwizard.metrics.jersey2;

import io.dropwizard.metrics.MetricRegistry;
import io.dropwizard.metrics.Timer;
import io.dropwizard.metrics.jersey2.resources.InstrumentedResourcePerClass;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Application;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.dropwizard.metrics.MetricRegistry.name;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests registering {@link InstrumentedResourceMethodApplicationListener} as a singleton
 * in a Jersey {@link ResourceConfig}
 */

public class SingletonMetricsPerClassJerseyTest extends JerseyTest {
    static {
        Logger.getLogger("org.glassfish.jersey").setLevel(Level.OFF);
    }

    private MetricRegistry registry;

    @Override
    protected Application configure() {
        this.registry = new MetricRegistry();

        ResourceConfig config = new ResourceConfig();
        config = config.register(new MetricsFeature(this.registry));
        config = config.register(InstrumentedResourcePerClass.class);

        return config;
    }

    @Test
    public void timedPerClassMethodsAreTimed() {
        assertThat(target("timedPerClass")
                .request()
                .get(String.class))
                .isEqualTo("yay");

        final Timer timer = registry.timer(name(InstrumentedResourcePerClass.class, "timedPerClass"));

        assertThat(timer.getCount()).isEqualTo(1);
    }
}
