package com.ttn.translation.connector.core.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.comments.Comment;
import com.adobe.granite.comments.CommentCollection;
import com.adobe.granite.translation.api.TranslationConfig;
import com.adobe.granite.translation.api.TranslationConstants;
import com.adobe.granite.translation.api.TranslationConstants.TranslationMethod;
import com.adobe.granite.translation.api.TranslationConstants.TranslationStatus;
import com.adobe.granite.translation.api.TranslationException;
import com.adobe.granite.translation.api.TranslationMetadata;
import com.adobe.granite.translation.api.TranslationObject;
import com.adobe.granite.translation.api.TranslationResult;
import com.adobe.granite.translation.api.TranslationScope;
import com.adobe.granite.translation.api.TranslationService;
import com.adobe.granite.translation.api.TranslationState;
import com.adobe.granite.translation.core.common.AbstractTranslationService;
import com.adobe.granite.translation.core.common.TranslationResultImpl;
import com.cloudwords.api.client.CloudwordsCustomerClient;
import com.cloudwords.api.client.exception.CloudwordsClientException;
import com.cloudwords.api.client.resources.CloudwordsFile;
import com.cloudwords.api.client.resources.Project;
import com.cloudwords.api.client.resources.TranslatedDocument;
import com.ttn.translation.connector.core.impl.config.IntellimeetTranslationCloudConfigImpl;

public class IntellimeetTranslationServiceImpl extends AbstractTranslationService implements TranslationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(IntellimeetTranslationServiceImpl.class);

	private static final String TAG_METADATA = "tag-metadata";
	private static final String ASSET_METADATA = "asset-metadata";
	private static final String I18NCOMPONENTSTRINGDICT = "i18n-dictionary";
	private static final String HAS_NO_TRANSLATION_CONTENT = "noTranslation";

	private String baseurl = "";
	private String apikey = "";
	private String tempFolderPath = "";
	private String exportFormat = IntellimeetConstants.EXPORT_FORMAT_XML;

	private HashMap<String, TranslationConstants.TranslationStatus> status = new HashMap<String, TranslationConstants.TranslationStatus>();
	{
		status.put("configured_project_name", TranslationConstants.TranslationStatus.COMMITTED_FOR_TRANSLATION);
		status.put("configured_project_details", TranslationConstants.TranslationStatus.COMMITTED_FOR_TRANSLATION);
		status.put("uploaded_source_materials", TranslationConstants.TranslationStatus.COMMITTED_FOR_TRANSLATION);
		status.put("configured_bid_options", TranslationConstants.TranslationStatus.COMMITTED_FOR_TRANSLATION);
		status.put("submitted_for_bids", TranslationConstants.TranslationStatus.COMMITTED_FOR_TRANSLATION);
		status.put("waiting_for_bid_selection", TranslationConstants.TranslationStatus.COMMITTED_FOR_TRANSLATION);
		status.put("bid_selection_expired", TranslationConstants.TranslationStatus.ERROR_UPDATE);
		status.put("in_translation", TranslationConstants.TranslationStatus.TRANSLATION_IN_PROGRESS);
		status.put("in_review", TranslationConstants.TranslationStatus.TRANSLATION_IN_PROGRESS);
		status.put("change_order_requested", TranslationConstants.TranslationStatus.REJECTED);
		status.put("all_languages_approved", TranslationConstants.TranslationStatus.APPROVED);
		status.put("in_cancellation_waiting_for_vendor", TranslationConstants.TranslationStatus.CANCEL);
		status.put("in_cancellation_waiting_for_customer", TranslationConstants.TranslationStatus.CANCEL);
		status.put("project_closed", TranslationConstants.TranslationStatus.APPROVED);
		status.put("cancelled", TranslationConstants.TranslationStatus.CANCEL);
	}

	private static HashMap<String, TranslationConstants.TranslationStatus> translationObjectStatus = new HashMap<String, TranslationConstants.TranslationStatus>();
	{
		translationObjectStatus.put("syncing", TranslationConstants.TranslationStatus.TRANSLATION_IN_PROGRESS);
		translationObjectStatus.put("in_translation", TranslationConstants.TranslationStatus.TRANSLATION_IN_PROGRESS);
		translationObjectStatus.put("not_ready", TranslationConstants.TranslationStatus.TRANSLATION_IN_PROGRESS);
		translationObjectStatus.put("in_review", TranslationConstants.TranslationStatus.TRANSLATION_IN_PROGRESS);
		translationObjectStatus.put("in_revision", TranslationConstants.TranslationStatus.TRANSLATION_IN_PROGRESS);
		translationObjectStatus.put("not_delivered", TranslationConstants.TranslationStatus.TRANSLATION_IN_PROGRESS);
		translationObjectStatus.put("approved", TranslationConstants.TranslationStatus.TRANSLATED);
	}

	private static HashSet<TranslationConstants.TranslationStatus> aemTranslationStatus = new HashSet<TranslationConstants.TranslationStatus>();
	{
		aemTranslationStatus.add(TranslationConstants.TranslationStatus.APPROVED);
		aemTranslationStatus.add(TranslationConstants.TranslationStatus.REJECTED);
		aemTranslationStatus.add(TranslationConstants.TranslationStatus.CANCEL);
	}

	// Constructor
	public IntellimeetTranslationServiceImpl(Map<String, String> availableLanguageMap,
			Map<String, String> availableCategoryMap, String name, String baseurl, String apikey, String tempFolderPath,
			TranslationConfig translationConfig) {
		super(availableLanguageMap, availableCategoryMap, name, IntellimeetConstants.SERVICE_LABEL,
				IntellimeetConstants.SERVICE_ATTRIBUTION, IntellimeetTranslationCloudConfigImpl.ROOT_PATH,
				TranslationMethod.MACHINE_TRANSLATION, translationConfig);

		LOGGER.trace("IntellimeetTranslationServiceImpl.");
		this.baseurl = baseurl;
		this.apikey = apikey;
		this.tempFolderPath = tempFolderPath;
	}

	@Override
	public Map<String, String> supportedLanguages() {
		LOGGER.trace("IntellimeetTranslationServiceImpl.supportedLanguages");

		return null;
	}

	@Override
	public boolean isDirectionSupported(String sourceLanguage, String targetLanguage) throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.isDirectionSupported");
		// It should return true, if translation provider provides translation
		// from sourceLanguage to targetLanguage
		// otherwise false
		 return true;

		/*return (CloudwordsTranslationUtil
				.getSourceLanguageCodes(CloudwordsTranslationUtil.getCloudwordsClient(baseurl, apikey))
				.contains(CloudwordsTranslationUtil.getCloudwordsLanguageCode(sourceLanguage)))
				&& (CloudwordsTranslationUtil
						.getTargetLanguageCodes(CloudwordsTranslationUtil.getCloudwordsClient(baseurl, apikey))
						.contains(CloudwordsTranslationUtil.getCloudwordsLanguageCode(targetLanguage)));*/
		 }

	@Override
	public String detectLanguage(String toDetectSource, TranslationConstants.ContentType contentType)
			throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.detectLanguage");

		// French language code
		return "fr";
	}

	@Override
	public TranslationResult translateString(String sourceString, String sourceLanguage, String targetLanguage,
			TranslationConstants.ContentType contentType, String contentCategory) throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.translateString");
		String translatedString = getTranslatedString(sourceString,sourceLanguage,targetLanguage);
		TranslationResult tlObject = new TranslationResultImpl(translatedString,sourceLanguage,targetLanguage,contentType,contentCategory,sourceString, 2, null);
		return tlObject;
	}

	@Override
	public TranslationResult[] translateArray(String[] sourceStringArr, String sourceLanguage, String targetLanguage,
			TranslationConstants.ContentType contentType, String contentCategory) throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.translateArray");

		TranslationResult arrResults[] = new TranslationResultImpl[sourceStringArr.length];
		for (int i = 0; i < sourceStringArr.length; i++) {
			arrResults[i] = translateString(sourceStringArr[i], sourceLanguage, targetLanguage, contentType,
					contentCategory);
		}
		return arrResults;
	}

	@Override
	public TranslationResult[] getAllStoredTranslations(String sourceString, String sourceLanguage,
			String targetLanguage, TranslationConstants.ContentType contentType, String contentCategory, String userId,
			int maxTranslations) throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.getAllStoredTranslations");

		throw new TranslationException("This function is not implemented",
				TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
	}

	@Override
	public void storeTranslation(String[] originalText, String sourceLanguage, String targetLanguage,
			String[] updatedTranslation, TranslationConstants.ContentType contentType, String contentCategory,
			String userId, int rating, String path) throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.storeTranslation");

		throw new TranslationException("This function is not implemented",
				TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
	}

	@Override
	public void storeTranslation(String originalText, String sourceLanguage, String targetLanguage,
			String updatedTranslation, TranslationConstants.ContentType contentType, String contentCategory,
			String userId, int rating, String path) throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.storeTranslation");

		throw new TranslationException("This function is not implemented",
				TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
	}

	@Override
	public String createTranslationJob(String projectName, String description, String strSourceLanguage,
			String strTargetLanguage, Date dueDate, TranslationState state, TranslationMetadata jobMetadata)
			throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.createTranslationJob");
		//String baseCloudwordsUrl = "https://app-sandbox.cloudwords.com";
		//String cloudwordsApiKey = "03c093c4dcf546cbad51e89b26b7965b7d335da7aaadcaa06fb8f0279410a8a7";
		CloudwordsCustomerClient customerClient =  CloudwordsTranslationUtil.getCloudwordsClient(baseurl,apikey);
		return CloudwordsTranslationUtil.createCloudwordsProject(projectName,strSourceLanguage,strTargetLanguage,"Project Description",dueDate,customerClient);
	}

	@Override
	public TranslationScope getFinalScope(String strTranslationJobID) throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.getFinalScope");

		return null;
	}

	@Override
	public TranslationStatus updateTranslationJobState(String strTranslationJobID, TranslationState state)
			throws TranslationException {
		LOGGER.info("IntellimeetTranslationServiceImpl.updateTranslationJobState" + strTranslationJobID + " : "
				+ state.getStatus().toString());

		if (state.getStatus().equals(TranslationStatus.SCOPE_REQUESTED)) {
			LOGGER.trace("Scope Requested", strTranslationJobID);
			throw new TranslationException("", TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
		}
		if (state.getStatus().equals(TranslationStatus.COMMITTED_FOR_TRANSLATION)) {
			LOGGER.trace("Uploaded all Translation Objects in job {}", strTranslationJobID);
		}
		return null;
	}

	@Override
	public TranslationStatus getTranslationJobStatus(String strTranslationJobID) throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.getTranslationJobStatus");
		Project project = null;
		try {
			project = CloudwordsTranslationUtil.getCloudwordsClient(baseurl, apikey)
					.getProject(CloudwordsTranslationUtil.getIntFromNullableString(strTranslationJobID));
		} catch (CloudwordsClientException e) {
			LOGGER.error("Exception occured: {}" + e);
		}
		String projectStatus = project.getStatus().getCode();
		return (TranslationConstants.TranslationStatus) status.get(projectStatus);
	}

	@Override
	public CommentCollection<Comment> getTranslationJobCommentCollection(String strTranslationJobID) {
		LOGGER.trace("IntellimeetTranslationServiceImpl.getTranslationJobCommentCollection");
		return null;
	}

	@Override
	public void addTranslationJobComment(String strTranslationJobID, Comment comment) throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.addTranslationJobComment");

		throw new TranslationException("This function is not implemented",
				TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
	}

	public InputStream getTranslatedObject(String strTranslationJobID, TranslationObject translationObject)
			throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.getTranslatedObject");
		CloudwordsCustomerClient cloudwordsClient = CloudwordsTranslationUtil.getCloudwordsClient(baseurl, apikey);
		TranslatedDocument translateddocument = CloudwordsTranslationUtil.getTranslatedDocument(strTranslationJobID,
				translationObject.getId(), cloudwordsClient);
		if (translateddocument != null) {
			CloudwordsFile file = translateddocument.getFile();

				try {
					return cloudwordsClient.downloadFileFromMetadata(file);
				} catch (IllegalStateException e) {
					LOGGER.error("IllegalStateException occured: {}" + e);
				} catch (CloudwordsClientException e) {
					LOGGER.error("CloudwordsClientException occured: {}" + e);
				} catch (IOException e) {
					LOGGER.error("IOException occured: {}" + e);
				}
			
		}
		return null;
	}

	@Override
	public String uploadTranslationObject(String strTranslationJobID, TranslationObject translationObject)
			throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.uploadTranslationObject");
		File sourceFile = getSourceDocumentFile(strTranslationJobID, translationObject);
		if (sourceFile == null) {
			LOGGER.trace("Source file empty : not uploaded to cloudwords");
			return HAS_NO_TRANSLATION_CONTENT;
		}
		int translationObjectId = CloudwordsTranslationUtil.uploadSourceDocument(strTranslationJobID, sourceFile,
				CloudwordsTranslationUtil.getCloudwordsClient(baseurl, apikey));
		return Integer.toString(translationObjectId);
	}

	@Override
	public TranslationStatus updateTranslationObjectState(String strTranslationJobID,
			TranslationObject translationObject, TranslationState state) throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.updateTranslationObjectState");

		return state.getStatus();
	}

	@Override
	public TranslationStatus getTranslationObjectStatus(String strTranslationJobID, TranslationObject translationObject)
			throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.getTranslationObjectStatus");
		TranslationConstants.TranslationStatus translationStatus = translationObject.getTranslationJobMetadata()
				.getTranslationState().getStatus();
		if (aemTranslationStatus.contains(translationStatus)) {
			return translationStatus;
		}
		if (translationObject.getId().equals("noTranslation")) {
			TranslationStatus projectTranslationStatus = getTranslationJobStatus(strTranslationJobID);
			if (projectTranslationStatus.equals(TranslationStatus.TRANSLATION_IN_PROGRESS))
				return TranslationStatus.READY_FOR_REVIEW;
			else
				return projectTranslationStatus;
		}
		TranslatedDocument translateddocument = CloudwordsTranslationUtil.getTranslatedDocument(strTranslationJobID,
				translationObject.getId(), CloudwordsTranslationUtil.getCloudwordsClient(baseurl, apikey));
		if (translateddocument != null) {
			String translatedDocumentStatus = translateddocument.getStatus().getCode();
			translationStatus = translationObjectStatus.get(translatedDocumentStatus);
			return translationStatus;
		}
		return getTranslationJobStatus(strTranslationJobID);
	}

	@Override
	public TranslationStatus[] updateTranslationObjectsState(String strTranslationJobID,
			TranslationObject[] translationObjects, TranslationState[] states) throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.updateTranslationObjectsState");

		TranslationStatus[] retStatus = new TranslationStatus[states.length];
		for (int index = 0; index < states.length; index++) {
			retStatus[index] = updateTranslationObjectState(strTranslationJobID, translationObjects[index],
					states[index]);
		}
		return retStatus;
	}

	@Override
	public TranslationStatus[] getTranslationObjectsStatus(String strTranslationJobID,
			TranslationObject[] translationObjects) throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.getTranslationObjectsStatus");

		TranslationStatus[] retStatus = new TranslationStatus[translationObjects.length];
		for (int index = 0; index < translationObjects.length; index++) {
			retStatus[index] = getTranslationObjectStatus(strTranslationJobID, translationObjects[index]);
		}
		return retStatus;
	}

	@Override
	public CommentCollection<Comment> getTranslationObjectCommentCollection(String strTranslationJobID,
			TranslationObject translationObject) throws TranslationException {
		LOGGER.trace("IntellimeeetTranslationServiceImpl.getTranslationObjectCommentCollection");

		throw new TranslationException("This function is not implemented",
				TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
	}

	@Override
	public void addTranslationObjectComment(String strTranslationJobID, TranslationObject translationObject,
			Comment comment) throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.addTranslationObjectComment");

		throw new TranslationException("This function is not implemented",
				TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
	}

	@Override
	public void updateTranslationJobMetadata(String strTranslationJobID, TranslationMetadata jobMetadata,
			TranslationMethod translationMethod) throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.updateTranslationJobMetadata");

		throw new TranslationException("This function is not implemented",
				TranslationException.ErrorCode.SERVICE_NOT_IMPLEMENTED);
	}

	public void updateDueDate(String strTranslationJobID, Date date) throws TranslationException {
		LOGGER.trace("IntellimeetTranslationServiceImpl.uploadTranslationObject");
	}

	private String getTempFilePath(TranslationObject translationObject) {
		String translationObjectPath = translationObject.getTranslationObjectSourcePath();
		String sourceFilePath = "";
		if ((translationObjectPath != null) && (!translationObjectPath.isEmpty())) {
			sourceFilePath = translationObjectPath.substring(translationObjectPath.lastIndexOf("/") + 1,
					translationObjectPath.length());
		} else {
			if (translationObject.getTitle().equals("TAGMETADATA"))
				sourceFilePath = TAG_METADATA;
			if (translationObject.getTitle().equals("ASSETMETADATA"))
				sourceFilePath = ASSET_METADATA;
			if (translationObject.getTitle().equals("I18NCOMPONENTSTRINGDICT"))
				sourceFilePath = I18NCOMPONENTSTRINGDICT;
		}
		LOGGER.trace("Source file : {}", sourceFilePath);
		return tempFolderPath + "/" + sourceFilePath;
	}

	private File getSourceDocumentFile(String strTranslationJobID, TranslationObject translationObject)
			throws TranslationException {
		String translationObjectMimeType = translationObject.getMimeType();
		InputStream translationInputStream = null;
		String tempSourceFilePath = getTempFilePath(translationObject);
		if (translationObjectMimeType.equals("text/html") || translationObjectMimeType.equals("text/xml")) {
			translationInputStream = translationObject.getTranslationObjectXMLInputStream();
			if (!FileUtil.hasTranslationContent(translationInputStream))
				return null;
			translationInputStream = translationObject.getTranslationObjectXMLInputStream();
			tempSourceFilePath += exportFormat;
		} else
			translationInputStream = translationObject.getTranslatedObjectInputStream();
		return FileUtil.writeInputStreamToFile(tempSourceFilePath, translationInputStream);
	}

	private String getTranslatedString(String string,String srcLang,String tarLang) {
		return string + "_translated_from_"+srcLang+"_To_"+tarLang;
	}

}
