import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.amazonaws.mturk.service.exception.ServiceException;
import com.amazonaws.mturk.service.exception.ValidationException;


public class CS565Controller {

	public static void main(String[] args) {
		DBService dbService = new DBService();
		MTurkService mturkService = new MTurkService();
		String action = args[0];
	    
		if("GET".equalsIgnoreCase(action))
	    {
	    	List<String> postedHitIdsFromDB;
			try 
			{
				postedHitIdsFromDB = dbService.getPostedHitIdsFromDB();
				Map<String, DBQuestion> results = mturkService.getAnswers(postedHitIdsFromDB);
				dbService.updateAnswersToDB(results);
				System.out.println("GOT ANSWERS!");
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
	    	
	    }
	    else if("POST".equalsIgnoreCase(action))
	    {
			try 
			{
				
				Map<String, DBQuestion> questionsById = dbService.getNonPostedQuestionsFromDB();
				if(mturkService.hasEnoughFund(questionsById.size()))
				{
					mturkService.postTasks(questionsById);
					dbService.addPostsToDB(questionsById);
					System.out.println("POSTED SUCCESSFULLY! ");
				}
				else
				{
					System.out.println("NOT ENOUGH FUNDS!");
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
	    	
	    	
	    }
	    else
	    {
	    	System.out.println("NOTHING TO DO!");
	    }
		
		dbService.closeConnection();

	}

}
