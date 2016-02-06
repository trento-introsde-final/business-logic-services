package fitbot.bls.rest.output;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


public class GoalsResponse extends BasicResponse{

	@JsonInclude(Include.NON_NULL)
	private List<GoalResponseObject> goals;
	
	public GoalsResponse(){
		super();
	}

	public List<GoalResponseObject> getGoals() {
		return goals;
	}


	
	
}
