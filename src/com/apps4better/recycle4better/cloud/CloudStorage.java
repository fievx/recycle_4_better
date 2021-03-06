package com.apps4better.recycle4better.cloud;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.content.Context;
import android.util.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.StorageObject;

/**
 * Simple wrapper around the Google Cloud Storage API
 */
public class CloudStorage {

	private static Properties properties;
	private static Storage storage;
	private CloudStorageListener parent;

	/**
	 * Static fields must be entered per application.
	 * to get the account id and private key, go to the developer console => API and auth => Credentials => Create new client ID.
	 */
	private static final String PROJECT_ID_PROPERTY = "snappy-storm-816";
	private static final String APPLICATION_NAME_PROPERTY = "Recycle4better";
	private static final String ACCOUNT_ID_PROPERTY = "330354908046-acis24og10mqrb47db4f8mkn0mbocbv5@developer.gserviceaccount.com";
	private static final String PRIVATE_KEY_PATH_PROPERTY = "Recycle4Better-af4dee8649bc.p12";

	
	public CloudStorage (CloudStorageListener parent){
		this.parent = parent;
	}
	
	/**
	 * Uploads a file to a bucket. Filename and content type will be based on
	 * the original file.
	 * 
	 * @param bucketName
	 *            Bucket where file will be uploaded
	 * @param filePath
	 *            Absolute path of the file to upload
	 * @throws Exception
	 */
	public void uploadFile(String bucketName, String filePath, Context context)
			throws Exception {
		
		Storage storage = getStorage(context);

		StorageObject object = new StorageObject();
		object.setBucket(bucketName);

		File file = new File(filePath);

		InputStream stream = new FileInputStream(file);
		try {
			String contentType = URLConnection
					.guessContentTypeFromStream(stream);
			InputStreamContent content = new InputStreamContent(contentType,
					stream);

			Storage.Objects.Insert insert = storage.objects().insert(
					bucketName, null, content);
			insert.setName(file.getName());
			insert.getMediaHttpUploader().setProgressListener(this.new CustomProgressListener());
			insert.execute();
		} finally {
			stream.close();
		}
	}
	
	public void uploadImageFromStream(String bucketName, ByteArrayOutputStream bStream, String fileName, Context context)
			throws Exception {
		
		Storage storage = getStorage(context);

		StorageObject object = new StorageObject();
		object.setBucket(bucketName);
		
		OutputStream outputStream = null;
		String filePath = context.getCacheDir()+"/"+ fileName;
		try {

			outputStream = new FileOutputStream (filePath); 
			bStream.writeTo(outputStream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			outputStream.close();
		}
		
		
		File file = new File(filePath);

		InputStream stream = new FileInputStream(file);
		try {
			String contentType = URLConnection
					.guessContentTypeFromStream(stream);
			InputStreamContent content = new InputStreamContent(contentType,
					stream);

			Storage.Objects.Insert insert = storage.objects().insert(
					bucketName, null, content);
			insert.setName(file.getName());
			insert.getMediaHttpUploader().setProgressListener( this.new CustomProgressListener());
			insert.execute();
		} finally {
			stream.close();
		}
	}
	
	public static void downloadFile(String bucketName, String fileName, String destinationDirectory, Context context) throws Exception {
		
		File directory = new File(destinationDirectory);
		if(!directory.isDirectory()) {
			throw new Exception("Provided destinationDirectory path is not a directory");
		}
		File file = new File(directory.getAbsolutePath() + "/" + fileName);
		
		Storage storage = getStorage(context);
		
		Storage.Objects.Get get = storage.objects().get(bucketName, fileName);
		FileOutputStream stream = new FileOutputStream(file);
		try {
			get.executeAndDownloadTo(stream);
		} finally {
			stream.close();
		}
	}

	/**
	 * Deletes a file within a bucket
	 * 
	 * @param bucketName
	 *            Name of bucket that contains the file
	 * @param fileName
	 *            The file to delete
	 * @throws Exception
	 */
	public static void deleteFile(String bucketName, String fileName, Context context)
			throws Exception {

		Storage storage = getStorage(context);
		
		storage.objects().delete(bucketName, fileName).execute();
	}

	/**
	 * Creates a bucket
	 * 
	 * @param bucketName
	 *            Name of bucket to create
	 * @throws Exception
	 */
	public static void createBucket(String bucketName, Context context) throws Exception {

		Storage storage = getStorage(context);

		Bucket bucket = new Bucket();
		bucket.setName(bucketName);

		storage.buckets().insert(PROJECT_ID_PROPERTY, bucket).execute();
	}
	
	/**
	 * Deletes a bucket
	 * 
	 * @param bucketName
	 *            Name of bucket to delete
	 * @throws Exception
	 */
	public static void deleteBucket(String bucketName, Context context) throws Exception {

		Storage storage = getStorage(context);
		
		storage.buckets().delete(bucketName).execute();
	}
	
	/**
	 * Lists the objects in a bucket
	 * 
	 * @param bucketName bucket name to list
	 * @return Array of object names
	 * @throws Exception
	 */
	public static List<String> listBucket(String bucketName, Context context) throws Exception {
		
		Storage storage = getStorage(context);
		
		List<String> list = new ArrayList<String>();
		
		List<StorageObject> objects = storage.objects().list(bucketName).execute().getItems();
		if(objects != null) {
			for(StorageObject o : objects) {
				list.add(o.getName());
			}
		}
		
		return list;
	}
	
	/**
	 * List the buckets with the project
	 * (Project is configured in properties)
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<String> listBuckets(Context context) throws Exception {
		
		Storage storage = getStorage(context);
		
		List<String> list = new ArrayList<String>();
		
		List<Bucket> buckets = storage.buckets().list(PROJECT_ID_PROPERTY).execute().getItems();
		if(buckets != null) {
			for(Bucket b : buckets) {
				list.add(b.getName());
			}
		}
		
		return list;
	}

//	private static Properties getProperties() throws Exception {
//
//		if (properties == null) {
//			properties = new Properties();
//			InputStream stream = CloudStorage.class
//					.getResourceAsStream("/cloudstorage.properties");
//			try {
//				properties.load(stream);
//			} catch (IOException e) {
//				throw new RuntimeException(
//						"cloudstorage.properties must be present in classpath",
//						e);
//			} finally {
//				stream.close();
//			}
//		}
//		return properties;
//	}

	private static Storage getStorage(Context context) throws Exception {

		if (storage == null) {

			HttpTransport httpTransport = new NetHttpTransport();
			JsonFactory jsonFactory = new JacksonFactory();

			List<String> scopes = new ArrayList<String>();
			scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);

			Credential credential = new GoogleCredential.Builder()
					.setTransport(httpTransport)
					.setJsonFactory(jsonFactory)
					.setServiceAccountId(ACCOUNT_ID_PROPERTY)
					.setServiceAccountPrivateKeyFromP12File(getTempPkc12File(context))
					.setServiceAccountScopes(scopes).build();

			storage = new Storage.Builder(httpTransport, jsonFactory,
					credential).setApplicationName(APPLICATION_NAME_PROPERTY)
					.build();
		}

		return storage;
	}
	
	private static File getTempPkc12File(Context context) throws IOException {
	    // xxx.p12 export from google API console
	    InputStream pkc12Stream = context.getAssets().open(PRIVATE_KEY_PATH_PROPERTY);
	    File tempPkc12File = File.createTempFile("temp_pkc12_file", "p12");
	    OutputStream tempFileStream = new FileOutputStream(tempPkc12File);

	    int read = 0;
	    byte[] bytes = new byte[1024];
	    while ((read = pkc12Stream.read(bytes)) != -1) {
	        tempFileStream.write(bytes, 0, read);
	    }
	    tempFileStream.close();
	    return tempPkc12File;
	}

	class CustomProgressListener implements MediaHttpUploaderProgressListener {
		  public void progressChanged(MediaHttpUploader uploader) throws IOException {
		    switch (uploader.getUploadState()) {
		      case INITIATION_STARTED:
		        System.out.println("Initiation has started!");
		        break;
		      case INITIATION_COMPLETE:
		        System.out.println("Initiation is complete!");
		        break;
		      case MEDIA_IN_PROGRESS:
		        System.out.println(uploader.getProgress());
		        break;
		      case MEDIA_COMPLETE:
		        System.out.println("Upload is complete!");
		        parent.update(CloudStorageListener.TAG_SUCCESS);
		        Log.d("CloudStorage", "upload finised and successfull");
		        break;
			case NOT_STARTED:
				break;
			default:
				break;
		    }
		  }
		}

}
