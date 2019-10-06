package com.capitalone.weathertracker.dao;

import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import com.capitalone.weathertracker.measurements.Measurement;

/**
 * An Singleton Class, which contains the weatherDataInformation, and the data will
 * be cleared only when the server connection is stopped.
 */
public class WeatherRepository {
	
	private static WeatherRepository weatherRepository = new WeatherRepository();
	
	private Map<ZonedDateTime, Measurement> weatherData = new LinkedHashMap<>();

	private WeatherRepository() {}
	
	public static WeatherRepository getInstance() {
		return weatherRepository;
	}
	
	public Map<ZonedDateTime, Measurement> getWeatherData() {
		return weatherData;
	}
	
	public static void addWeatherData(ZonedDateTime timestamp, Measurement measurement) {
		getInstance().weatherData.put(timestamp, measurement);
	}
}
