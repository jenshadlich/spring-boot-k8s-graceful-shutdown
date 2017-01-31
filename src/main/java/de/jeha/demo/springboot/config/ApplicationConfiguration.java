package de.jeha.demo.springboot.config;

import de.jeha.demo.springboot.resources.LongRunningTaskResource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

/**
 * @author jenshadlich@googlemail.com
 */
@Configuration
public class ApplicationConfiguration {

    public static final long SHUTDOWN_WAIT_TIME_MILLIS = 10_000;

    private static final Logger LOG = LoggerFactory.getLogger(LongRunningTaskResource.class);
    private static volatile Server JETTY;
    private static volatile StatisticsHandler HANDLER;

    @Value("${server.port}")
    private String serverPort;

    @Bean
    public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory() {
        final JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory();

        factory.setPort(Integer.parseInt(serverPort));
        LOG.info("jetty configured on port: {}", serverPort);

        factory.addServerCustomizers(jetty -> {
            JETTY = jetty;

            HANDLER = new StatisticsHandler();
            HANDLER.setHandler(JETTY.getHandler());
            JETTY.setHandler(HANDLER);

            LOG.info("Configure jetty stop timeout: {} ms", SHUTDOWN_WAIT_TIME_MILLIS);
            JETTY.setStopTimeout(SHUTDOWN_WAIT_TIME_MILLIS);
            JETTY.setStopAtShutdown(false); // => handled by JettyGracefulShutdown
        });
        return factory;
    }

    @Bean
    public JettyGracefulShutdown jettyGracefulShutdown() {
        return new JettyGracefulShutdown();
    }

    private static class JettyGracefulShutdown implements ApplicationListener<ContextClosedEvent> {

        private static final Logger LOG = LoggerFactory.getLogger(JettyGracefulShutdown.class);

        @Override
        public void onApplicationEvent(ContextClosedEvent event) {
            if (JETTY != null) {
                LOG.info("Attempt to shutdown jetty gracefully ...");

                if (HANDLER != null) {
                    LOG.info("Active requests: {}, active dispatches: {}", HANDLER.getRequestsActive(), HANDLER.getDispatchedActive());
                }
                try {
                    final long begin = System.currentTimeMillis();
                    JETTY.stop();
                    LOG.info("Shutdown took: {} ms", System.currentTimeMillis() - begin);
                } catch (Exception e) {
                    LOG.error("Graceful shutdown of jetty failed", e);
                }
            }
        }

    }

}
