package com.ttn.translation.connector.core.impl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.adobe.granite.translation.api.TranslationConfig;
import com.adobe.granite.translation.api.TranslationConstants.TranslationMethod;
import com.adobe.granite.translation.api.TranslationException;
import com.adobe.granite.translation.api.TranslationService;
import com.adobe.granite.translation.api.TranslationServiceFactory;
import com.adobe.granite.translation.core.TranslationCloudConfigUtil;
import com.ttn.translation.connector.core.IntellimeetTranslationCloudConfig;

@Service
@Component(label = "Intellimeet Translation Connector Factory", metatype = true, immediate = true)
@Properties(value = { @Property(name = "service.description", value = "Intellimeet translation service"),
		@Property(name = IntellimeetConstants.TEMP_FOLDER_PATH, value = "/tmp", label = "Temporary Folder Path", description = "The Folder Path under which all templorary files like xlf,xml or html are stored temporarly") })

public class IntellimeetTranslationServiceFactoryImpl implements TranslationServiceFactory {

	protected String factoryName;
	protected String tempFolderPath;
	private List<TranslationMethod> supportedTranslationMethods;

	@Reference
	TranslationCloudConfigUtil cloudConfigUtil;

	@Reference
	TranslationConfig translationConfig;

	@Reference
	CryptoSupport cryptoSupport;

	public IntellimeetTranslationServiceFactoryImpl() {
		log.trace("IntellimeetTranslationServiceFactoryImpl.");

		supportedTranslationMethods = new ArrayList<TranslationMethod>();
		supportedTranslationMethods.add(TranslationMethod.HUMAN_TRANSLATION);
		supportedTranslationMethods.add(TranslationMethod.MACHINE_TRANSLATION);
	}

	private static final Logger log = LoggerFactory.getLogger(IntellimeetTranslationServiceFactoryImpl.class);

	@Override
	public TranslationService createTranslationService(TranslationMethod translationMethod, String cloudConfigPath)
			throws TranslationException {
		log.trace("IntellimeetTranslationServiceFactoryImpl.createTranslationService");

		IntellimeetTranslationCloudConfig intellimeetCloudConfg = (IntellimeetTranslationCloudConfig) cloudConfigUtil
				.getCloudConfigObjectFromPath(IntellimeetTranslationCloudConfig.class, cloudConfigPath);

		String baseurl = "";
		String apikey = "";

		if (intellimeetCloudConfg != null) {
			baseurl = intellimeetCloudConfg.getBaseUrl();
			apikey = intellimeetCloudConfg.getApiKey();
		}

		if (cryptoSupport != null) {
			try {
				if (cryptoSupport.isProtected(apikey)) {
					apikey = cryptoSupport.unprotect(apikey);
				} else {
					log.trace("API Key is not protected");
				}
			} catch (CryptoException e) {
				log.error("Error while decrypting the client secret {}", e);
			}
		}

		Map<String, String> availableLanguageMap = new HashMap<String, String>();
		Map<String, String> availableCategoryMap = new HashMap<String, String>();
		return new IntellimeetTranslationServiceImpl(availableLanguageMap, availableCategoryMap, factoryName, baseurl,
				apikey, tempFolderPath, translationConfig);
	}

	@Override
	public List<TranslationMethod> getSupportedTranslationMethods() {
		log.trace("IntellimeetTranslationServiceFactoryImpl.getSupportedTranslationMethods");
		return supportedTranslationMethods;
	}

	@Override
	public Class<?> getServiceCloudConfigClass() {
		log.trace("IntellimeetTranslationServiceFactoryImpl.getServiceCloudConfigClass");
		return IntellimeetTranslationCloudConfig.class;
	}

	protected void activate(final ComponentContext ctx) {
		log.trace("Starting function: activate");
		final Dictionary<?, ?> properties = ctx.getProperties();

		factoryName = IntellimeetConstants.TRANSLATION_FACTORY_NAME;
		tempFolderPath = PropertiesUtil.toString(properties.get(IntellimeetConstants.TEMP_FOLDER_PATH), "/tmp").trim();
		if (tempFolderPath.equals("")) {
			tempFolderPath = System.getProperty("java.io.tmpdir");
		}
		log.trace("Activated TSF with the following:");
		log.trace("Factory Name: {}", factoryName);
		log.trace("Temporary Folder Path: {}",tempFolderPath);
	}

	@Override
	public String getServiceFactoryName() {
		log.trace("Starting function: getServiceFactoryName");
		return factoryName;
	}
}
