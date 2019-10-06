package com.capitalone.weathertracker.service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.capitalone.weathertracker.dao.WeatherRepository;
import com.capitalone.weathertracker.measurements.*;
import com.capitalone.weathertracker.statistics.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WeatherTrackerService implements MeasurementQueryService, MeasurementStore, MeasurementAggregator {
	private WeatherRepository weatherRepository = WeatherRepository.getInstance();
	
	/**
	 * Add a measurement to Internal Database, an Singleton Hashmap
	 */
	@Override
	public void add(Measurement measurement) {
		ZonedDateTime utcOffset = ZonedDateTime.of(measurement.getTimestamp().toLocalDateTime(), ZoneOffset.UTC);
		this.weatherRepository.addWeatherData(utcOffset, measurement);
	}
	
	/**
	 * Fetch Measurement based on timestamp, from the internal database
	 */
	@Override
	public Measurement fetch(ZonedDateTime timestamp) {
		ZonedDateTime utcOffset = ZonedDateTime.of(timestamp.toLocalDateTime(), ZoneOffset.UTC);
		return this.weatherRepository.getWeatherData().get(utcOffset);
	}

	/**
	 * Get All measurements from the internal database based on the given to and from timestamp
	 */
	@Override
	public List<Measurement> queryDateRange(ZonedDateTime from, ZonedDateTime to) {
		List<Measurement> queryMeasurementList = new ArrayList<Measurement>();
		
		for(Map.Entry<ZonedDateTime, Measurement> entry: weatherRepository.getWeatherData().entrySet()) {
			ZonedDateTime timestamp = entry.getKey();
			if((timestamp.isEqual(from) || timestamp.isAfter(from)) &&
					timestamp.isBefore(to)) {
				queryMeasurementList.add(entry.getValue());
			}
		}
		return queryMeasurementList;
	}

	/**
	 * Gather all the measurement Info and loop through from the internal database
	 */
	@Override
	public List<AggregateResult> analyze(List<Measurement> measurements, 
			List<String> metrics,
			List<Statistic> stats) {
		List<AggregateResult> aggregateResultList = new ArrayList<AggregateResult>();
		
		for(String metric: metrics) {
			for(Statistic stat: stats) {
				if(stat.equals(Statistic.MIN)) {
					AggregateResult innerAggregationResult;
					Double value = Double.MAX_VALUE;
					for(Measurement measurement: measurements) {
						Double measurementValue = measurement.getMetric(metric);
						if(measurementValue == null) {
							continue;
						}
						if(measurement != null && measurementValue < value) {
							value = measurementValue;
						}
					}
					if(value != Double.MAX_VALUE) {
						innerAggregationResult = new AggregateResult(metric, Statistic.MIN, value);
						aggregateResultList.add(innerAggregationResult);
					}
				} else if(stat.equals(Statistic.MAX)) {
					AggregateResult innerAggregationResult;
					Double value = Double.MIN_VALUE;
					for(Measurement measurement: measurements) {
						Double measurementValue = measurement.getMetric(metric);
						if(measurementValue == null) {
							continue;
						}
						if(measurement != null && measurementValue > value) {
							value = measurementValue;
						}
					}
					if(value != Double.MIN_VALUE) {
						innerAggregationResult = new AggregateResult(metric, Statistic.MAX, value);
						aggregateResultList.add(innerAggregationResult);
					}
				} else if(stat.equals(Statistic.AVERAGE)) {
					AggregateResult innerAggregationResult;
					Double value = 0.0;
					int measurementSize = 0;
					for(Measurement measurement: measurements) {
						Double measurementValue = measurement.getMetric(metric);
						if(measurementValue == null) {
							continue;
						}
						if(measurementValue != null) {
							value = value + measurementValue;
							measurementSize++;
						}
					}
					if(value != 0.0) {
						Double averageValue = (value / measurementSize);
						int scale = (int) Math.pow(10, 1);
						Double rounderAverageValue = (double) (Math.round(averageValue * scale) / 10.0);
						innerAggregationResult = new AggregateResult(metric, Statistic.AVERAGE, rounderAverageValue);
						aggregateResultList.add(innerAggregationResult);
					}
				} else {
					continue;
				}
			}
		}
		return aggregateResultList;
	}
}
