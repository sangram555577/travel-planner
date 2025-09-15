package com.TripFinder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.time.Duration;

@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "app")
public class CacheConfig {

    private RateLimit rateLimit = new RateLimit();
    private Api api = new Api();

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("amadeus-tokens", "flight-offers", "hotel-offers");
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(30000))
                .setReadTimeout(Duration.ofMillis(30000))
                .build();
    }

    // Getters and setters for configuration properties
    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(RateLimit rateLimit) {
        this.rateLimit = rateLimit;
    }

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public static class RateLimit {
        private Amadeus amadeus = new Amadeus();

        public Amadeus getAmadeus() {
            return amadeus;
        }

        public void setAmadeus(Amadeus amadeus) {
            this.amadeus = amadeus;
        }

        public static class Amadeus {
            private int requestsPerSecond = 10;
            private int burstCapacity = 20;

            public int getRequestsPerSecond() {
                return requestsPerSecond;
            }

            public void setRequestsPerSecond(int requestsPerSecond) {
                this.requestsPerSecond = requestsPerSecond;
            }

            public int getBurstCapacity() {
                return burstCapacity;
            }

            public void setBurstCapacity(int burstCapacity) {
                this.burstCapacity = burstCapacity;
            }
        }
    }

    public static class Api {
        private Fallback fallback = new Fallback();

        public Fallback getFallback() {
            return fallback;
        }

        public void setFallback(Fallback fallback) {
            this.fallback = fallback;
        }

        public static class Fallback {
            private boolean enabled = true;
            private int minResults = 5;

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public int getMinResults() {
                return minResults;
            }

            public void setMinResults(int minResults) {
                this.minResults = minResults;
            }
        }
    }
}