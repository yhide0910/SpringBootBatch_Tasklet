package com.hide1963.test01.config.props;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "batch.metadata.datasource")
public class BatchMetadataDataSourceProperties extends DataSourceProperties {

    private Hikari hikari = new Hikari();

    public Hikari getHikari() {
        return hikari;
    }
    public void setHikari(Hikari hikari) {
        this.hikari = hikari;
    }

    public static class Hikari {
        private Integer maximumPoolSize;
        private Integer minimumIdle;
        private String poolName;
        private Long idleTimeout;

        public Integer getMaximumPoolSize() { return maximumPoolSize; }
        public void setMaximumPoolSize(Integer maximumPoolSize) { this.maximumPoolSize = maximumPoolSize; }
        public Integer getMinimumIdle() { return minimumIdle; }
        public void setMinimumIdle(Integer minimumIdle) { this.minimumIdle = minimumIdle; }
        public String getPoolName() { return poolName; }
        public void setPoolName(String poolName) { this.poolName = poolName; }
        public Long getIdleTimeout() { return idleTimeout; }
        public void setIdleTimeout(Long idleTimeout) { this.idleTimeout = idleTimeout; }
    }
}
