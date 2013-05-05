package test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.mturk.dataschema.QuestionFormAnswers;
import com.amazonaws.mturk.dataschema.QuestionFormAnswersType;
import com.amazonaws.mturk.requester.Assignment;
import com.amazonaws.mturk.requester.HIT;
import com.amazonaws.mturk.service.axis.RequesterService;
import com.amazonaws.mturk.service.exception.ServiceException;
import com.amazonaws.mturk.util.PropertiesClientConfig;
import com.mashape.client.authentication.Authentication;
import com.mashape.client.authentication.MashapeAuthentication;
import com.mashape.client.http.ContentType;
import com.mashape.client.http.HttpClient;
import com.mashape.client.http.HttpMethod;
import com.mashape.client.http.MashapeResponse;
import com.mashape.client.http.ResponseType;
public class MTurkService {
	
	  private static final String CREATE_ACTION = "C";
	  private static final String GENERATE_ACTION = "G";
	  //HIT DEV
	  static final String HIT_ID = "2A4OFXRDS4IFTZ53AEAFP11AAKGXU4";
	  //HIT1
	  //static final String HIT_ID ="2MQB727M0IGIC2KRTF6S4F4VIX2YD0";
	  //HIT2
	  //static final String HIT_ID ="2TEA6X3YOCCISXWV7EGTRNIIX7OQP3";
	  
	  private RequesterService service;

	  // Defining the attributes of the HIT to be created
	  private String title = "Answer a question";
	  private String description = "This is a HIT created by the Mechanical Turk SDK.  Please answer the provided question.";
	  private int numAssignments = 5;
	  private double reward = 0.01;

	  /**
	   * Constructor
	   * 
	   */
	  public MTurkService() {
	    service = new RequesterService(new PropertiesClientConfig("mturk.properties"));
	  }

	  /**
	   * Check if there are enough funds in your account in order to create the HIT
	   * on Mechanical Turk
	   * 
	   * @return true if there are sufficient funds. False if not.
	   */
	  public boolean hasEnoughFund() {
	    double balance = service.getAccountBalance();
	    System.out.println("Got account balance: " + RequesterService.formatCurrency(balance));
	    return balance > reward;
	  }

	  /**
	   * Creates the simple HIT.
	   * 
	   */
	  public void createHit() {
	    try {

	      // The createHIT method is called using a convenience static method of
	      // RequesterService.getBasicFreeTextQuestion that generates the QAP for
	      // the HIT.
	      HIT hit = service.createHIT(
	              title,
	              description,
	              reward,
	              RequesterService.getBasicFreeTextQuestion(
	                  "Visit this website http://cs.illinois.edu/ and provide three words that comes to your mind when you see this site?"),
	              numAssignments);
	      System.out.println("Created HIT: " + hit.getHITId());

	      System.out.println("You may see your HIT with HITTypeId '" 
	          + hit.getHITTypeId() + "' here: ");
	      System.out.println(service.getWebsiteURL() 
	          + "/mturk/preview?groupId=" + hit.getHITTypeId());

	    } catch (ServiceException e) {
	      System.err.println(e.getLocalizedMessage());
	    }
	  }
	  
	  public String getHitResponses(String hitId) {
		  	
		  	HIT hit = service.getHIT(HIT_ID);
		  	System.out.println("--HIT data--: "+hit.getDescription()+"---C: "+hit.getNumberOfAssignmentsCompleted()+"---S: "+hit.getHITStatus());
		  
	    	Assignment[] assignments = service.getAllAssignmentsForHIT(hitId);
	    	
	    	
	        System.out.println("--[Getting HITs]----------");
	        System.out.println("  HIT Id: " + hitId+ "  Assignment count: "+assignments.length);
	        
	        StringBuffer respose = new StringBuffer();

	        for (Assignment assignment : assignments) {


	            //By default, answers are specified in XML
	            String answerXML = assignment.getAnswer();

	            //Calling a convenience method that will parse the answer XML and extract out the question/answer pairs.
	            QuestionFormAnswers qfa = RequesterService.parseAnswers(answerXML);
	            List<QuestionFormAnswersType.AnswerType> answers =
	              (List<QuestionFormAnswersType.AnswerType>) qfa.getAnswer();

	            for (QuestionFormAnswersType.AnswerType answer : answers) {

	              String assignmentId = assignment.getAssignmentId();
	              String answerValue = RequesterService.getAnswerValue(assignmentId, answer);

	              if (answerValue != null) {
	                System.out.println("Got an answer \"" + answerValue
	                    + "\" from worker " + assignment.getWorkerId() + ".");
	                respose.append(answerValue);


	              }
	            }
	          }
	        
	        return respose.toString();
	  }
	  
	  public String generateWordCloud()
	  {
		  WordCloudMaker cloudMaker = new WordCloudMaker("YzhhaGlrbnJnaXVpa2lnanh1Y2ZydnJ0emdkdXF0OjhkOGJlZjllOTE5N2Y5ZGFiMmE5YTQxMTliMDM0YjdkNTQ2MzlkNTk=");
		  String hitResponses = this.getHitResponses(HIT_ID);
		  MashapeResponse<JSONObject> response = cloudMaker.makeWordCloud("800", hitResponses, "800");
		  String url = "";
			try {
				url = response.getBody().getString("url");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return url;
	  }
	  
	  

	  /**
	   * Main method
	   * 
	   * @param args
	   */
	  public static void main(String[] args) {

	    MTurkService app = new MTurkService();
	    
	    String action = args[0];
	    if(CREATE_ACTION.equalsIgnoreCase(action))
	    {
	    	if (app.hasEnoughFund()) {
	  	      app.createHit();
	  	      System.out.println("Success.");
	  	    } else {
	  	      System.out.println("You do not have enough funds to create the HIT.");
	  	    }
	    	
	    }
	    else if(GENERATE_ACTION.equalsIgnoreCase(action))
	    {
	    	String url = app.generateWordCloud();
	    	System.out.println("Word cloud url: "+url);
	    	
	    }
	    else
	    {
	    	System.out.println("Nothing to do!");
	    }
	    	

	    
	  }
	  
	  class WordCloudMaker {

			private final static String PUBLIC_DNS = "gatheringpoint-word-cloud-maker.p.mashape.com";
		    private List<Authentication> authenticationHandlers;

		    public WordCloudMaker (String publicKey) {
		        authenticationHandlers = new LinkedList<Authentication>();
		        authenticationHandlers.add(new MashapeAuthentication(publicKey));
		        
		    }
		    
		    /**
		     * Synchronous call with optional parameters.
		     */
		    public MashapeResponse<JSONObject> makeWordCloud(String height, String textblock, String width, String config) {
		        Map<String, Object> parameters = new HashMap<String, Object>();
		        
		        if (height != null && !height.equals("")) {
			parameters.put("height", height);
		        }
		        
		        
		        
		        if (textblock != null && !textblock.equals("")) {
			parameters.put("textblock", textblock);
		        }
		        
		        
		        
		        if (width != null && !width.equals("")) {
			parameters.put("width", width);
		        }
		        
		        
		        
		        if (config != null && !config.equals("")) {
			parameters.put("config", config);
		        }
		        
		        
		        
		        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
		                    HttpMethod.POST,
		                    "https://" + PUBLIC_DNS + "/index.php",
		                    parameters,
		                    ContentType.FORM,
		                    ResponseType.JSON,
		                    authenticationHandlers);
		    }

		    /**
		     * Synchronous call without optional parameters.
		     */
		    public MashapeResponse<JSONObject> makeWordCloud(String height, String textblock, String width) {
		        return makeWordCloud(height, textblock, width, "");
		    }


		    

		}
}
