package de.jeha.demo.springboot.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author jenshadlich@googlemail.com
 */
@RestController
public class LongRunningTaskResource {

    private static final Logger LOG = LoggerFactory.getLogger(LongRunningTaskResource.class);

    @RequestMapping(value = "/longRunningTask")
    public String longRunningTask() {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(10));
        } catch (InterruptedException e) {
            LOG.info("Got interrupted.", e);
        }
        LOG.info("Done.");
        return "OK";
    }

}
