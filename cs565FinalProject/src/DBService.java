import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.amazonaws.mturk.service.axis.RequesterService;
import com.amazonaws.mturk.util.PropertiesClientConfig;

public class DBService {

	String dbUrl = "jdbc:mysql://66.147.244.101:3306/catchmtd_cs565";
	String dbClass = "com.mysql.jdbc.Driver";
	private Connection connection;

	 public DBService() {
		 try {
				Class.forName(dbClass);
				this.connection = DriverManager.getConnection(dbUrl,"catchmtd_cs565", "cs565bailey!");
				this.connection.isValid(0);
			} 
		 catch (Exception e) {
				e.printStackTrace();
			}

		  }

	public Connection getConnection() {
		return this.connection;
	}

	private ResultSet executeSelectQuery(String query) throws SQLException {
			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			return rs;

	}
	
	private void executeUpdateQuery(String query) throws SQLException {
		
		Connection con = this.getConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate(query);
	}

	public void closeConnection() {
		try {
			this.getConnection().close();
		} 
		catch (SQLException e) {
			this.connection = null;
			e.printStackTrace();
			
		}

	}
	
	public Map<String, DBQuestion> getNonPostedQuestionsFromDB() throws SQLException
	{
		Map<String, DBQuestion> questionsById = new HashMap<String, DBQuestion>();
		String selectQuery =  "select q.* from hitmapping h, questions q where q.id=h.qid and h.posted!='Y'";
		ResultSet rs = this.executeSelectQuery(selectQuery);
		while (rs.next()) {
			String id = rs.getString("id");
			String tid = rs.getString("tid");
			String questionInstructions = rs.getString("questionInstructions");
			String questionURL = rs.getString("questionURL");
			String mcQuestion = rs.getString("mcQuestion");
			String answerA = rs.getString("answerA");
			String answerB = rs.getString("answerB");
			String answerC = rs.getString("answerC");
			String answerD = rs.getString("answerD");
			String correctAnswer = rs.getString("correctAnswer");
			String question2 = rs.getString("question2");
			String correctAnswer2 = rs.getString("correctAnswer2");
			
			DBQuestion dbQuestion = new DBQuestion();
			dbQuestion.setqId(id);
			dbQuestion.setTaskId(tid);
			dbQuestion.setInstructions(questionInstructions);
			dbQuestion.setUrl(questionURL);
			dbQuestion.setMcQuestion(mcQuestion);
			dbQuestion.setAnswerA(answerA);
			dbQuestion.setAnswerB(answerB);
			dbQuestion.setAnswerC(answerC);
			dbQuestion.setAnswerD(answerD);
			dbQuestion.setCorrectAnswer(correctAnswer);
			dbQuestion.setQuestion2(question2);
			dbQuestion.setCorrectAnswer2(correctAnswer2);
			
			questionsById.put(id, dbQuestion);
		}
		return questionsById;
		
		
	}
	
	public List<String> getPostedHitIdsFromDB() throws SQLException
	{
		List<String> hitsIds = new ArrayList<String>();
		String selectQuery = "select id from hitmapping where POSTED='Y' and results!='Y'";
		ResultSet rs = this.executeSelectQuery(selectQuery);
		while (rs.next()) {
			String hitId = rs.getString("hitId");
			hitsIds.add(hitId);
		} 
		return hitsIds;
		
	}
	
	public void updateAnswersToDB( Map<String, DBQuestion> dbQuestionByhitId) throws SQLException
	{
		Set<String> hitsIds = dbQuestionByhitId.keySet();
		String selectQuery = "Select id, quid from hitmapping where id in ="+" ";
		ResultSet rs = this.executeSelectQuery(selectQuery);
		Map<String, String> hitByQuestionId = new HashMap<String, String>();
		while (rs.next()) {
			String id = rs.getString("id");
			String qid = rs.getString("qid");
			hitByQuestionId.put(id, qid);
		} 
		
		
		for(String hitId: hitsIds)
		{
			DBQuestion dbQuestion = dbQuestionByhitId.get(hitId);
			if( dbQuestion!=null)
			{
				String mcresult = dbQuestion.getMcResult();
				String result2 = dbQuestion.getMcResult();
				if(mcresult!=null && result2!=null )
				{
			
					String updateQuery1 ="update results set mcresult='"+ mcresult +
										"' ,result2= "+result2 +
											" where qid= "+hitByQuestionId.get(hitId);
					this.executeUpdateQuery(updateQuery1);
					
					String updateQuery2 = "update hitmapping set results='Y' where id="+hitId;
					this.executeUpdateQuery(updateQuery2);
				}
			}
		}
	}
	
	public void addPostsToDB(Map<String, DBQuestion> questionsById) throws SQLException
	{
		Set<Entry<String, DBQuestion>> entrySet = questionsById.entrySet();
		for(Entry<String, DBQuestion> entry: entrySet)
		{
			DBQuestion dbQuestion = entry.getValue();
			String hitId = dbQuestion.getHitId();
			if(hitId!=null)
			{
				String updateQuery= "update hitmapping set id = '"+hitId+"', set posted='Y' where qid = "+dbQuestion.getqId();
				this.executeUpdateQuery(updateQuery);
			}
		}
	}
	
}


