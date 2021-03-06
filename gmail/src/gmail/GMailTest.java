package gmail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Thread;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import com.google.api.services.gmail.Gmail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
public class GMailTest {
	
    // we only need read-only permission to the inbox
    private static final String SCOPE = "https://www.googleapis.com/auth/gmail.readonly";

    // this is the name I gave my app when I authorized gmail
    private static final String APP_NAME = "GMail API Tests";

    // the username of the logged-in user.  "me" is your best bet
    private static final String USER = "me";

    // Path to the client_secret.json file
    private static final String CLIENT_SECRET_PATH = "./client_secret.json";
    
    public static List<Message> listMessagesMatchingQuery(Gmail service, String userId,
    		 String query) throws IOException {
    		 ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();

    		List<Message> messages = new ArrayList<Message>();
    		while (response.getMessages() != null) {
    		messages.addAll(response.getMessages());
    		if (response.getNextPageToken() != null) {
    		String pageToken = response.getNextPageToken();
    		 response = service.users().messages().list(userId).setQ(query)
    		.setPageToken(pageToken).execute();
    		 } else {
    		break;
    		}
    		}

    		 for (Message message : messages) {
    		 System.out.println(message.toPrettyString());
    	}

    		 return messages;
    		 }
    public static List<Message> listMessagesWithLabels(Gmail service, String userId,
    		 List<String> labelIds) throws IOException {
    		ListMessagesResponse response = service.users().messages().list(userId)
    		 .setLabelIds(labelIds).execute();

    		List<Message> messages = new ArrayList<Message>();
    		 while (response.getMessages() != null) {
    		 messages.addAll(response.getMessages());
    		if (response.getNextPageToken() != null) {
    		 String pageToken = response.getNextPageToken();
    		 response = service.users().messages().list(userId).setLabelIds(labelIds)
    		 .setPageToken(pageToken).execute();
    		} else {
    		break;
    		}
    		}

    		 for (Message message : messages) {
    		System.out.println(message.toPrettyString());
    		}

    		 return messages;
    		 }    
    public static MimeMessage getMimeMessage(Gmail service, String userId, String messageId)
    		throws IOException, MessagingException {
     Message message = service.users().messages().get(userId, messageId).setFormat("raw").execute();

     Base64 base64Url = new Base64(true);
     byte[] emailBytes = base64Url.decodeBase64(message.getRaw());

    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
    MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

    return email;
     }
    
    
    public static Message getMessage(Gmail service, String userId, String messageId)
     throws IOException {
     Message message = service.users().messages().get(userId, messageId).execute();

     System.out.println("Message snippet: " + message.getSnippet());

     	return message;
    }
    
    public static void getThread(Gmail service, String userId, String threadId) throws IOException {
    	 Thread thread = service.users().threads().get(userId, threadId).execute();
    	 System.out.println("Thread id: " + thread.getId());
    	 System.out.println("No. of messages in this thread: " + thread.getMessages().size());
    	
    	System.out.println(thread.toPrettyString());
    	System.out.println("**************************************************************");
    	
    	}  
    
    public static void main(String[] args) throws IOException, MessagingException {
        // set up an HTTP connection and a way to parse JSON
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        // load our secret
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(jsonFactory, new FileReader(CLIENT_SECRET_PATH));

        // do OAuth authentication
        GoogleAuthorizationCodeFlow flow =
            new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets, Arrays.asList(SCOPE))
            .setAccessType("online").setApprovalPrompt("auto").build();
        String url =
            flow.newAuthorizationUrl().setRedirectUri(GoogleOAuthConstants.OOB_REDIRECT_URI).build();
        System.out.println("Open this link, get authorized, then paste in the token:\n" + url);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String code = br.readLine();
        GoogleTokenResponse response =
            flow.newTokenRequest(code).setRedirectUri(GoogleOAuthConstants.OOB_REDIRECT_URI).execute();
        GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);

        // Create a new authorized Gmail API client
        Gmail service =
            new Gmail.Builder(httpTransport, jsonFactory, credential).setApplicationName(APP_NAME).build();

        // Get page of message threads
        ListThreadsResponse threadsResponse = service.users().threads().list(USER).execute();
        List<Thread> threads = threadsResponse.getThreads();
        
        ListMessagesResponse messagesResponse = service.users().messages().list(USER).execute();
        List<Message> messages = messagesResponse.getMessages();
        
        
        // Print thread ids
        for (Thread thread : threads) {
           
        	String id =thread.getId();
            getThread( service, USER, id);

        }
    }
}