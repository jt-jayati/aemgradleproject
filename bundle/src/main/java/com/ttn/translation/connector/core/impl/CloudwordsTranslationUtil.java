package com.ttn.translation.connector.core.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudwords.api.client.CloudwordsCustomerClient;
import com.cloudwords.api.client.exception.CloudwordsClientException;
import com.cloudwords.api.client.resources.Department;
import com.cloudwords.api.client.resources.IntendedUse;
import com.cloudwords.api.client.resources.Language;
import com.cloudwords.api.client.resources.Project;
import com.cloudwords.api.client.resources.SourceDocument;
import com.cloudwords.api.client.resources.TranslatedDocument;
import com.cloudwords.org.apache.commons.lang3.StringUtils;

/**
 * This utility class implements all the methods required to communicate with
 * Cloudwords
 * 
 */

public final class CloudwordsTranslationUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(CloudwordsTranslationUtil.class);

	private static final String DFEAULT_INTENDED_USE = "Website";
	private static final String PROJECT_CONTENT_TYPE = "AEM Integration";

	private static final String API_VERSION = "1.21";

	// Constructor Definition
	private CloudwordsTranslationUtil() {
	}

	/**
	 * Creates the project in Cloudwords having attributes like name,
	 * department, source language, target language, due date, description.
	 * 
	 * @param projectName
	 *            the name of the project
	 * @param projectSourceLanguage
	 *            translation project source language
	 * @param projectTargetLanguage
	 *            translation project target language
	 * @param projectDescription
	 *            the description of the project
	 * @param projectDueDate
	 *            Due Date of the project
	 * @param department
	 *            department to which project belongs
	 * @param customerClient
	 *            Cloudwords client
	 * @return created project id
	 * @exception CloudwordsClientException
	 */
	public static String createCloudwordsProject(String projectName, String projectSourceLanguage,
			String projectTargetLanguage, String projectDescription, Date projectDueDate,
			CloudwordsCustomerClient customerClient) {
		LOGGER.trace("Entered createCloudwordsProject of CloudwordsTranslationUtil to create project with name : {}",
				projectName);
		Project cloudwordsProject = new Project();
		cloudwordsProject.setName(projectName);
		try {
			cloudwordsProject.setIntendedUse(getIntendedUse(customerClient));
		} catch (CloudwordsClientException e) {
			LOGGER.error("Cloudwords Exception: {}", e);
		}
		cloudwordsProject.setSourceLanguage(new Language(getCloudwordsLanguageCode(projectSourceLanguage)));
		List<Language> targetLanguages = createListFromLanguage(getCloudwordsLanguageCode(projectTargetLanguage));
		cloudwordsProject.setTargetLanguages(targetLanguages);
		if (projectDueDate != null) {
			LOGGER.trace("Adding delivery due date : {}", projectDueDate);
			cloudwordsProject.setDeliveryDueDate(projectDueDate);
		}
		if (!projectDescription.isEmpty()) {
			cloudwordsProject.setDescription(projectDescription);
		}
		LOGGER.trace("Adding ContentType : {}", PROJECT_CONTENT_TYPE);
		cloudwordsProject.setProjectContentType(PROJECT_CONTENT_TYPE);
		cloudwordsProject.setUiFeatures(getUiFeatures());
		Project createdProject;
		Integer createdProjectId;
		try {
			createdProject = customerClient.createProject(cloudwordsProject);
			createdProjectId = createdProject.getId();
			LOGGER.trace("Cloudwords project created with id: {}", createdProjectId);
			return Integer.toString(createdProjectId);
		} catch (CloudwordsClientException ce) {
			LOGGER.error("createCloudwordsProject: {}", ce);
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Get the UI features options available in Cloudwords
	 * 
	 * @return UI features map
	 */
	private static Map<String, Boolean> getUiFeatures() {
		Map<String, Boolean> uiFeatures = new HashMap<String, Boolean>();
		uiFeatures.put("change_source_language", Boolean.FALSE);
		uiFeatures.put("change_target_languages", Boolean.FALSE);
		uiFeatures.put("change_source_material", Boolean.FALSE);
		uiFeatures.put("clone_project", Boolean.FALSE);
		LOGGER.trace("Setting UI Features : {}", uiFeatures);
		return uiFeatures;
	}

	/**
	 * Return Department Object for a specific Cloudwords project using
	 * department selected in cloud configuration
	 * 
	 * @param customerClient
	 *            Cloudwords client
	 * @param department
	 *            the department selected in cloud configuration to which
	 *            project belongs
	 * @return Department
	 * @exception CloudwordsClientException
	 * @see Department
	 * @see CloudwordsCustomerClient
	 */
	private static Department getDepartmentForProject(CloudwordsCustomerClient customerClient, String department) {
		List<Department> departments;
		try {
			departments = customerClient.getDepartments();
			int lastIndex = department.lastIndexOf("/");
			if (lastIndex > 0) {
				department = department.substring(lastIndex + 1, department.length());
				for (Department d : departments) {
					if (d.getId() == Integer.parseInt(department))
						return d;
				}
			} else if (departments.size() > 0)
				return departments.get(0);
		} catch (CloudwordsClientException ce) {
			LOGGER.error("Error createCloudwordsProject: {}", ce);
		}
		return null;
	}

	/**
	 * Return integer from string
	 * 
	 * @param value
	 *            the string value
	 * @return integer value
	 */
	public static int getIntFromNullableString(String value) {
		if ((value == null) || (value.trim().length() == 0)) {
			return -1;
		}
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			LOGGER.error("Exception occured: {}" + e);
		}
		return -1;
	}

	/**
	 * Return IntendedUse List from Cloudwords
	 * 
	 * @param client
	 *            Cloudwords client
	 * @return IntendedUse the indended use of the project
	 * @throws CloudwordsClientException
	 * @see IntendedUse
	 */
	private static IntendedUse getIntendedUse(CloudwordsCustomerClient client) throws CloudwordsClientException {
		List<IntendedUse> uses = client.getIntendedUses();
		IntendedUse thisIntendedUse = null;
		for (IntendedUse use : uses) {
			if (use.getName().equalsIgnoreCase(DFEAULT_INTENDED_USE)) {
				thisIntendedUse = use;
				break;
			}
		}
		LOGGER.trace("Adding Intended Use : {}", thisIntendedUse);
		return thisIntendedUse;
	}

	/**
	 * Returns list of languages
	 * 
	 * @param language
	 * @return Language List
	 */
	private static List<Language> createListFromLanguage(String language) {
		List<Language> languages = new ArrayList<Language>();
		languages.add(new Language(language));
		return languages;
	}

	/**
	 * Returns Project's source language code
	 * 
	 * @param strTranslationJobID
	 *            the translation project id
	 * @param client
	 *            Cloudwords client
	 * @return language code for project's source language
	 * @see CloudwordsCustomerClient
	 */
	public static String getProjectSourceLanguage(String strTranslationJobID, CloudwordsCustomerClient client) {
		LOGGER.trace("Entered getProjectSourceLanguage()");
		try {
			Project project = client.getProject(getIntFromNullableString(strTranslationJobID));
			if (project != null && project.getSourceLanguage() != null)
				return project.getSourceLanguage().getLanguageCode();
		} catch (NumberFormatException e) {
			LOGGER.error("Error getProjectSourceLanguage: {}", e);
		} catch (CloudwordsClientException e) {
			LOGGER.error("Error getProjectSourceLanguage: {}", e);
		}
		return null;
	}

	/**
	 * Returns Project's target language
	 * 
	 * @param strTranslationJobID
	 *            the translation project id
	 * @param client
	 *            Cloudwords client
	 * @return target language
	 * @see Language
	 * @see CloudwordsCustomerClient
	 */
	public static Language getProjectTargetLanguage(String strTranslationJobID, CloudwordsCustomerClient client) {
		LOGGER.trace("Entered getProjectTargetLanguage()");
		try {
			Project project = client.getProject(getIntFromNullableString(strTranslationJobID));
			if (project != null && !project.getTargetLanguages().isEmpty())
				return project.getTargetLanguages().get(0);
		} catch (NumberFormatException nfe) {
			LOGGER.error("NumberFormatException occured: {}" + nfe);
		} catch (CloudwordsClientException ce) {
			LOGGER.error("CloudwordsClientException occured: {}" + ce);
		}
		return null;
	}

	/**
	 * Returns set of Project's source language codes
	 * 
	 * @param client
	 *            Cloudwords client
	 * @return set of source language codes
	 * @see CloudwordsCustomerClient
	 */
	public static Set<String> getSourceLanguageCodes(CloudwordsCustomerClient client) {
		Set<String> sourceLanguageCodes = new HashSet<String>();
		try {
			List<Language> sourceLanguages = client.getSourceLanguages();
			for (Language language : sourceLanguages) {
				sourceLanguageCodes.add(language.getLanguageCode());
			}
		} catch (CloudwordsClientException ce) {
			LOGGER.error("Error checking languages: {}", ce);
		}
		LOGGER.trace("returning source language list : {}", sourceLanguageCodes);
		return sourceLanguageCodes;
	}

	/**
	 * Returns Project's language code to convert to Cloudwords acceptable
	 * format
	 * 
	 * @param strLanguage
	 *            language code to be converted
	 * @return Cloudwords acceptable language code
	 */
	public static String getCloudwordsLanguageCode(String strLanguage) {
		String languageCode = strLanguage.replaceAll("_", "-");
		return languageCode;
	}

	/**
	 * Returns set of Project's target language codes
	 * 
	 * @param client
	 *            Cloudwords client
	 * @return set of target language codes
	 * @see CloudwordsCustomerClient
	 */
	public static Set<String> getTargetLanguageCodes(CloudwordsCustomerClient client) {
		LOGGER.trace("Entered getTargetLanguageCodes()");
		Set<String> targetLanguageCodes = new HashSet<String>();
		try {
			List<Language> targetLanguages = client.getTargetLanguages();
			for (Language language : targetLanguages) {
				targetLanguageCodes.add(language.getLanguageCode());
			}
		} catch (CloudwordsClientException ce) {
			LOGGER.error("CloudwordsClientException: {}" + ce);
		}
		LOGGER.trace("returning target language list : {}", targetLanguageCodes);
		return targetLanguageCodes;
	}



	/**
	 * Uploads source document to Cloudwords created project
	 * 
	 * @param strTranslationJobID
	 *            the translation project id
	 * @param sourceFile
	 *            source file to be uploaded on Cloudwords for particular
	 *            project id
	 * @param client
	 *            Cloudwords client
	 * @return source id of the file uploaded
	 * @see CloudwordsCustomerClient
	 */
	public static int uploadSourceDocument(String strTranslationJobID, File sourceFile,
			CloudwordsCustomerClient client) {
		LOGGER.trace("Entered uploadSourceDocument");
		SourceDocument source = null;
		try {
			LOGGER.trace("uploading source file to translation project : {}", strTranslationJobID);
			source = client.addSourceDocument(getIntFromNullableString(strTranslationJobID), sourceFile);
			sourceFile.delete();
		} catch (CloudwordsClientException ce) {
			LOGGER.error("CloudwordsClientException: {}" + ce);
		}
		LOGGER.trace("Document uploaded with id : {}", source.getId());
		return source.getId();
	}

	/**
	 * Returns translated document from Cloudwords
	 * 
	 * @param strTranslationJobID
	 *            the translation project id
	 * @param translationObjectId
	 *            translation Object id
	 * @param cloudwordsClient
	 *            Cloudwords client object
	 * @return translated document for the paticular project id and translation
	 *         object id
	 * @see TranslatedDocument
	 * @see CloudwordsCustomerClient
	 */
	public static TranslatedDocument getTranslatedDocument(String strTranslationJobID, String translationObjectId,
			CloudwordsCustomerClient cloudwordsClient) {
		LOGGER.trace("Entered getTranslationDocument");
		Language targetLanguage = getProjectTargetLanguage(strTranslationJobID, cloudwordsClient);
		List<TranslatedDocument> translatedDocuments;
		try {
			translatedDocuments = cloudwordsClient.getTranslatedDocuments(getIntFromNullableString(strTranslationJobID),
					targetLanguage);
			if (translatedDocuments != null) {
				for (TranslatedDocument translatedDocument : translatedDocuments) {
					if (translatedDocument.getSourceDocumentId() == getIntFromNullableString(translationObjectId))
						return translatedDocument;
				}
			}
		} catch (CloudwordsClientException ce) {
		}
		return null;
	}

	/**
	 * Returns the Cloudwords client for given base URL and API key.
	 * 
	 * @param baseurl
	 *            the base URL of the Cloudwords
	 * @param apikey
	 *            the API key of the Client
	 * @return the Cloudwords client
	 */
	public static CloudwordsCustomerClient getCloudwordsClient(String baseurl, String apikey) {
		LOGGER.trace("Entered getCloudwordsClient");

		CloudwordsCustomerClient client = new CloudwordsCustomerClient(baseurl, API_VERSION, apikey);;
		return client;
	}
}
