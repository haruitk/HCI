import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.amazonaws.mturk.addon.HITQuestion;
import com.amazonaws.mturk.addon.QAPValidator;
import com.amazonaws.mturk.dataschema.QuestionFormAnswers;
import com.amazonaws.mturk.dataschema.QuestionFormAnswersType;
import com.amazonaws.mturk.requester.Assignment;
import com.amazonaws.mturk.requester.HIT;
import com.amazonaws.mturk.service.axis.RequesterService;
import com.amazonaws.mturk.service.exception.ServiceException;
import com.amazonaws.mturk.service.exception.ValidationException;
import com.amazonaws.mturk.util.PropertiesClientConfig;

public class MTurkService {
	
	  static final String QUESTION1_IDENTIFIER="1";
	  static final String QUESTION2_IDENTIFIER="2";
	  private RequesterService service;
	  // Defining the attributes of the HIT to be created
	  private String title = "Answer these question after clicking on the given website link";
	  private int numAssignments = 1;
	  private double reward = 0.05;

	  public MTurkService() {
		  this.service = new RequesterService(new PropertiesClientConfig("mturk.properties"));
	  }

	  public boolean hasEnoughFund(int size) {
	    double balance = this.getService().getAccountBalance();
	    System.out.println("Got account balance: " + RequesterService.formatCurrency(balance));
	    return balance > (reward * size);
	  }

	  public String createHit(DBQuestion dbQuestion) throws ServiceException, ValidationException,IOException {
		String questionXML = generateQuestionXML(dbQuestion);
		QAPValidator.validate(questionXML);
		String description= dbQuestion.getInstructions()+ " "+ dbQuestion.getUrl();
		HIT hit = this.getService().createHIT( title, description, reward, questionXML, numAssignments);
		String hitId = hit.getHITId();
		System.out.println("Created HIT: " + hitId);

		System.out.println("You may see your HIT with HITTypeId '" + hit.getHITTypeId() + "' here: ");
		System.out.println(this.getService().getWebsiteURL() + "/mturk/preview?groupId=" + hit.getHITTypeId());
		return hitId;
	  }
	  
	  public DBQuestion getHitResponses(String hitId) {
		  	
		  	HIT hit = this.getService().getHIT(hitId);
		  	System.out.println("--HIT data--: "+hit.getDescription()+"---C: "+hit.getNumberOfAssignmentsCompleted()+"---S: "+hit.getHITStatus());
		  
	    	Assignment[] assignments = this.getService().getAllAssignmentsForHIT(hitId);
	        System.out.println("--[Getting HITs]----------");
	        System.out.println("  HIT Id: " + hitId+ "  Assignment count: "+assignments.length);
	        
	        List<String> respose = new ArrayList<String>();
	        DBQuestion dbQuestion = null;
	        for (Assignment assignment : assignments) 
	        {

	            //By default, answers are specified in XML
	            String answerXML = assignment.getAnswer();

	            //Calling a convenience method that will parse the answer XML and extract out the question/answer pairs.
	            QuestionFormAnswers qfa = RequesterService.parseAnswers(answerXML);
	            List<QuestionFormAnswersType.AnswerType> answers =  (List<QuestionFormAnswersType.AnswerType>) qfa.getAnswer();
	            dbQuestion = new DBQuestion();
	            dbQuestion.setHitId(hitId);
	            for (QuestionFormAnswersType.AnswerType answer : answers) 
	            {
	            	String questionIdentifier = answer.getQuestionIdentifier();
	            	String assignmentId = assignment.getAssignmentId();
  	              	String answerValue = RequesterService.getAnswerValue(assignmentId, answer);
  	              	if (answerValue != null) 
  	              	{
  	              		System.out.println("Got an answer \"" + answerValue + "\" from worker " + assignment.getWorkerId() + ".");
  	              		if(QUESTION1_IDENTIFIER.equals(questionIdentifier))
  	              		{
  	              			dbQuestion.setMcResult(answerValue);
  	              		}
  	              		else if(QUESTION2_IDENTIFIER.equals(questionIdentifier))
  	              		{
  	              			dbQuestion.setResult2(answerValue);
  	              		}
  	              	}
	            }
	          }
	        
	        return dbQuestion;
	  }
	  
	  public Map<String, DBQuestion> getAnswers(List<String> hitIds)
	  {
		  //map of hitId to question1&2 result
		  Map<String, DBQuestion> hitIdbyResults = new HashMap<String, DBQuestion>();
		  
		  for(String hitId: hitIds)
		  {
			  DBQuestion DBQuestion = this.getHitResponses(hitId);
			  hitIdbyResults.put(hitId, DBQuestion);
		  }
		  return hitIdbyResults;
		  
	  }
	  
	  public void postTasks(Map<String, DBQuestion> nonPostedQuestionsFromDB)
	  {
		  for(String questionId : nonPostedQuestionsFromDB.keySet())
		  {
			DBQuestion question = nonPostedQuestionsFromDB.get(questionId);
			String hitId = null;
			try {
				hitId = this.createHit(question);
			} catch (ServiceException e) {
				e.printStackTrace();
			} catch (ValidationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			  
			question.setHitId(hitId);
		  }
		  
	  }
	  
	  public RequesterService getService() {
		return this.service;
	}
	  
	public static String generateQuestionXML(DBQuestion question) {
		
		String q = "";
		q += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		q += "<QuestionForm xmlns=\"http://mechanicalturk.amazonaws.com/AWSMechanicalTurkDataSchemas/2005-10-01/QuestionForm.xsd\">";
		
		q += "  <Question>";
		q += "    <QuestionIdentifier>" + QUESTION1_IDENTIFIER + "</QuestionIdentifier>";
		q += "    <IsRequired>true</IsRequired>";
		q += "    </QuestionContent>";
		q += "      <Text>" + question.getMcQuestion() + "</Text>";
		q += "    <AnswerSpecification>";
		q += "    	<SelectionAnswer>";
		q += "    		<MinSelectionCount>1</MinSelectionCount>";
		q += "    		<MaxSelectionCount>1</MaxSelectionCount>";
		q += "        	<StyleSuggestion>radiobutton</StyleSuggestion>";
		q += "    			<Selections>";
		q += "    				<Selection>";
		q += "       				<SelectionIdentifier>a</SelectionIdentifier>";
		q += "        				<Text>" + question.getAnswerA() + "</Text>";
		q += "    				<Selection>";
		q += "    				<Selection>";
		q += "       				<SelectionIdentifier>b</SelectionIdentifier>";
		q += "        				<Text>" + question.getAnswerB() + "</Text>";
		q += "    				<Selection>";
		q += "    				<Selection>";
		q += "       				<SelectionIdentifier>c</SelectionIdentifier>";
		q += "        				<Text>" + question.getAnswerC() + "</Text>";
		q += "    				<Selection>";
		q += "    				<Selection>";
		q += "       				<SelectionIdentifier>D</SelectionIdentifier>";
		q += "        				<Text>" + question.getAnswerD() + "</Text>";
		q += "    				<Selection>";
		q += "    <AnswerSpecification>";
		q += "    <AnswerSpecification>";
		q += "    <AnswerSpecification>";
		q += "    			<Selections>";
		q += "    	<SelectionAnswer>";
		q += "    </AnswerSpecification>";
		q += "  </Question>";
		
		
		q += "  <Question>";
		q += "    <QuestionIdentifier>" + QUESTION2_IDENTIFIER + "</QuestionIdentifier>";
		q += "    <IsRequired>true</IsRequired>";
		q += "    <QuestionContent>";
		q += "      <Text>" + question.getQuestion2() + "</Text>";
		q += "    </QuestionContent>";
		q += "    <AnswerSpecification>";
		q += "      <FreeTextAnswer/>";
		q += "    </AnswerSpecification>";
		q += "  </Question>";
		
		q += "</QuestionForm>";
		
		return q;
	}

}
