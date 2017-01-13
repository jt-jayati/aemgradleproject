package com.ttn.translation.connector.core;

/**
 * This Interface is used to define cloud configuration properties in AEM
 */
public interface IntellimeetTranslationCloudConfig {

	// Properties used in Cloud Configuration
	public static final String PROPERTY_INTELLIMEET_BASE_URL = "baseurl";
	public static final String PROPERTY_INTELLIMEET_API_KEY = "apikey";
	public static final String RESOURCE_TYPE = "intellimeet-translation-connector/components/intellimeet-connector-cloudconfig";
	public static final String ROOT_PATH = "/etc/cloudservices/intellimeet-translation";

	/**
	 * Retrieve API End Point URL
	 * 
	 * @return Base URL
	 */
	public abstract String getBaseUrl();

	/**
	 * Retrieve API Access Key
	 * 
	 * @return Access Key
	 */
	public abstract String getApiKey();

}
