package com.ttn.translation.connector.core.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This Utility class contains all the methods related to file operations like
 * zip creation, adding file to zip, to check if the InputStream has translation
 * content or not.
 * 
 * @author nupurjain
 * @see InputStream
 * @see File
 */
public final class FileUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

	// Constructor Definition
	private FileUtil() {
	}

	/**
	 * Check if inputstream has translation content, then return true, else
	 * false
	 * 
	 * @param xmlInputStream
	 *            the InputSteam of the translation Object XML
	 * @return true if xml has translation content otherwise false
	 * @exception IOException
	 * @exception SAXException
	 * @exception ParserConfigurationException
	 * @see InputStream
	 */
	public static boolean hasTranslationContent(InputStream xmlInputStream) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document xmlDocument = dBuilder.parse(xmlInputStream);
			if (xmlDocument != null)
				xmlDocument.getDocumentElement().normalize();
			NodeList propertyNodeList = xmlDocument.getElementsByTagName("property");
			if (propertyNodeList != null)
				return propertyNodeList.getLength() > 0;
		} catch (IOException e) {
			LOGGER.error("Exception occured: {}" + e);
		} catch (SAXException e) {
			LOGGER.error("Exception occured: {}" + e);
		} catch (ParserConfigurationException e) {
			LOGGER.error("Exception occured: {}" + e);
		}
		return false;
	}

	/**
	 * Writes the Input stream to a file
	 * 
	 * @param tempSourceFolderPath
	 *            the path to the file where the InputStream is written
	 * @param sourceInputStream
	 *            the input stream of the source to be written to file
	 * @return source file
	 * @exception IOException
	 * @exception FileNotFoundException
	 * @see InputStream
	 */
	public static File writeInputStreamToFile(String tempSourceFolderPath, InputStream sourceInputStream) {
		LOGGER.trace("Writing src file {}", tempSourceFolderPath);
		FileOutputStream sourceOutputStream = null;
		File sourceFile = null;
		try {
			sourceFile = new File(tempSourceFolderPath);
			sourceOutputStream = new FileOutputStream(sourceFile);
			IOUtils.copy(sourceInputStream, sourceOutputStream);
		} catch (FileNotFoundException e) {
			LOGGER.error("Exception occured: {}" + e);
		} catch (IOException io) {
			LOGGER.error("Exception occured: {}" + io);
		} finally {
			if (sourceInputStream != null) {
				try {
					sourceInputStream.close();
				} catch (IOException io) {
					LOGGER.error("Exception occured: {}" + io);
				}
			}
		}
		return sourceFile;
	}

}
