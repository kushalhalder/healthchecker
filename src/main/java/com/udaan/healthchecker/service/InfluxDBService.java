package com.udaan.healthchecker.service;

import com.udaan.healthchecker.security.jwt.JwtUtils;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InfluxDBService {

  private static final Logger logger = LoggerFactory.getLogger(InfluxDBService.class);

  @Value("${influxdb.url}")
  private String databaseURL;

  @Value("${influxdb.username}")
  private String userName;

  @Value("${influxdb.password}")
  private String password;


  public void pushData(String serviceName, String instanceName) {
    logger.info(databaseURL);
    InfluxDB influxDB = InfluxDBFactory.connect(databaseURL, userName, password);

    final Point point = Point.measurement("memory")
        .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
        .tag("serviceName", serviceName)
        .tag("instanceName", instanceName)
        .addField("instance", instanceName)
        .addField("ping", 1L)
        .build();

    influxDB.write(point);
  }
}
