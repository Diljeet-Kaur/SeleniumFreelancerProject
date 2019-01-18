package com.test.mvn.proj.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.test.mvn.proj.exception.EmergartSeleniumException;
import com.test.mvn.proj.util.TestUtility;

/**
 * The Class OAuthGoogleFilesDownload.
 */
public class OAuthGoogleFilesDownload {

	/** The Constant LOG. */
	private final static Logger LOG = Logger.getLogger(OAuthGoogleFilesDownload.class);

	/** The http transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/** The Constant DATA_STORE_DIR. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
			".store/emergart");

	/** The Constant JSON_FACTORY. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** The data store factory. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** The Constant SCOPES. */
	private static final Set<String> SCOPES = Collections.singleton("https://www.googleapis.com/auth/drive");

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (GeneralSecurityException e) {
			LOG.error("GeneralSecurityException while accessing Google Drive API", e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			LOG.error("IOException while accessing Google Drive API", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Download files.
	 *
	 * @param driver
	 *            the driver
	 * @param props
	 *            the props
	 * @throws EmergartSeleniumException 
	 */
	public void downloadFiles(WebDriver driver, Properties props) throws EmergartSeleniumException {
		LOG.debug("downloadFiles : start");
		String inpFileDownloadPath = TestUtility.getDownloadFolderPath().append(props.get("inputFileName"))
				.append(".xlsx").toString();
		File inputfile = new File(inpFileDownloadPath);
		String outputFileDownloadPath = TestUtility.getDownloadFolderPath().append(props.get("outputFileName"))
				.append(".xlsx").toString();
		File outputfile = new File(outputFileDownloadPath);
		String inputfileId = props.getProperty("oauthInputFileId");
		try {
			OutputStream inpFileoutputStream = new FileOutputStream(inputfile);
			Drive driveService = getDriveService();
			driveService.files()
					.export(inputfileId, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
					.executeMediaAndDownloadTo(inpFileoutputStream);
			OutputStream outputFileStream = new FileOutputStream(outputfile);
			String outputfileId = getLatestOutputFile(driveService, (String) props.get("outputFileName"));
			driveService.files()
					.export(outputfileId, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
					.executeMediaAndDownloadTo(outputFileStream);
		} catch (FileNotFoundException e) {
			LOG.error("FileNotFounfException for input file : " + inpFileDownloadPath + " or output file : "
					+ outputFileDownloadPath);
			throw new EmergartSeleniumException(e);
		} catch (IOException e) {
			LOG.error("IOException while accessing input file : " + inpFileDownloadPath + " or output file : "
					+ outputFileDownloadPath);
			throw new EmergartSeleniumException(e);
		}
		LOG.debug("downloadFiles : end");
	}

	/**
	 * Gets the latest output file.
	 *
	 * @param driveService the drive service
	 * @param fileName the file name
	 * @return the latest output file
	 * @throws IOException 
	 */
	private String getLatestOutputFile(Drive driveService, String fileName) throws IOException {
		String fileId = StringUtils.EMPTY;
		FileList fileList = driveService.files().list().setQ("name='"+fileName+"'")
				.setOrderBy("modifiedTime desc").execute();
		List<com.google.api.services.drive.model.File> retFileList = fileList.getFiles();
		
		if(CollectionUtils.isNotEmpty(retFileList)){
			com.google.api.services.drive.model.File file = retFileList.get(0);
			fileId = file.getId();
		}
		System.out.println(fileId);
		return fileId;
	}

	/**
	 * Upload output file.
	 *
	 * @param driver
	 *            the driver
	 * @param props
	 *            the props
	 * @throws EmergartSeleniumException 
	 */
	public void uploadOutputFile(WebDriver driver, Properties props) throws EmergartSeleniumException {
		LOG.debug("uploadOutputFile : start");
		String outputFileDownloadPath = TestUtility.getDownloadFolderPath().append(props.get("outputFileName"))
				.append(".xlsx").toString();
		File outputfile = new File(outputFileDownloadPath);

		try {
			Drive driveService = getDriveService();
			FileContent mediaContent = new FileContent(
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", outputfile);
			com.google.api.services.drive.model.File file = new com.google.api.services.drive.model.File();
			file.setName((String) props.get("outputFileName"));
			file.setMimeType("application/vnd.google-apps.spreadsheet");
			com.google.api.services.drive.model.File uploadedFile = driveService.files().create(file, mediaContent)
					.setFields("id").execute();
			LOG.info("Uploaded File id : " + uploadedFile.getId());
		} catch (IOException e) {
			LOG.error("IOException while uploading output file : "+outputFileDownloadPath);
			throw new EmergartSeleniumException(e);
		}
		LOG.debug("uploadOutputFile : end");
	}

	/**
	 * Gets the drive service.
	 *
	 * @return the drive service
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Drive getDriveService() throws IOException {
		Credential credential = authorize();
		return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("GDriveProject").build();
	}

	/**
	 * Authorize.
	 *
	 * @return the credential
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static Credential authorize() throws IOException {
		// Load client secrets.
		InputStream secrets = OAuthGoogleFilesDownload.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(secrets));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
		;
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		LOG.info("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}
}