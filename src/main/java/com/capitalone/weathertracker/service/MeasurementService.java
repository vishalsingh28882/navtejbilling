package com.capitalone.weathertracker.service;

import java.util.ArrayList;

import com.capitalone.weathertracker.model.Measurements;
import com.capitalone.weathertracker.model.Metrics;
import com.capitalone.weathertracker.model.StatsRequest;
import com.capitalone.weathertracker.model.StatsResponse;

public interface MeasurementService {
     void addMeasurement(String timestamp, Metrics metrics);
     ArrayList<Measurements> getMeasurement(String timestamp);
     Metrics deleteMeasurement(String timestamp);
     int updateMeasurement(String timestamp, Metrics metrics);
     int patchMeasurement(String timestamp, Metrics metrics);
     ArrayList<StatsResponse> getMeasurementStatistics(StatsRequest statsRequest);
}