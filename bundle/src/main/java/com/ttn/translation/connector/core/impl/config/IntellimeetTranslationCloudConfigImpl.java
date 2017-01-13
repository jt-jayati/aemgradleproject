package com.ttn.translation.connector.core.impl.config;

import com.adobe.granite.translation.api.TranslationException;
import com.ttn.translation.connector.core.IntellimeetTranslationCloudConfig;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntellimeetTranslationCloudConfigImpl implements IntellimeetTranslationCloudConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntellimeetTranslationCloudConfigImpl.class);

	private String baseurl;
	private String apikey;

	/**
	 * Parameterized Constructor to retrieve cloud config properties for
	 * specified config resource
	 * 
	 * @param translationConfigResource
	 *            the resource of the cloud configuration
	 * @throws TranslationException
	 * @see Resource
	 */
	public IntellimeetTranslationCloudConfigImpl(Resource translationConfigResource) throws TranslationException {
		LOGGER.trace("Entered IntellimeetTranslationCloudConfigImpl() of CloudwordsTranslationCloudConfigImpl");

		Resource configContent;
		if (JcrConstants.JCR_CONTENT.equals(translationConfigResource.getName())) {
			configContent = translationConfigResource;
		} else {
			configContent = translationConfigResource.getChild(JcrConstants.JCR_CONTENT);
		}

		if (configContent != null) {
			ValueMap properties = configContent.adaptTo(ValueMap.class);

			this.baseurl = properties.get(PROPERTY_INTELLIMEET_BASE_URL, "");
			this.apikey = properties.get(PROPERTY_INTELLIMEET_API_KEY, "");
			LOGGER.trace("Created Intellimeet Connector Cloud Config with the following:");
			LOGGER.trace("BaseUrl: {}", baseurl);
			LOGGER.trace("ApiKey: {}", apikey);
		} else {
			throw new TranslationException("Error getting Cloud Config credentials",
					TranslationException.ErrorCode.MISSING_CREDENTIALS);
		}
	}

	/**
	 * fetch the base url set in the cloud configuration
	 * 
	 */
	@Override
	public String getBaseUrl() {
		LOGGER.trace("IntellimeetTranslationCloudConfigImpl.getBaseUrl()");
		return baseurl;
	}

	/**
	 * fetch the api key set in the cloud configuration
	 */
	@Override
	public String getApiKey() {
		LOGGER.trace("IntellimeetTranslationCloudConfigImpl.getApiKey()");
		return apikey;
	}

}