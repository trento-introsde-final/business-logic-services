package fitbot.bls.rest.output;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class GoalsStatusResponse extends BasicResponse{

	@JsonInclude(Include.NON_NULL)
	private List<GoalStatusObject> goalsStatus;
	
	public GoalsStatusResponse(){
		super();
	}

	public List<GoalStatusObject> getGoalsStatus() {
		return goalsStatus;
	}

	public void setGoalsStatus(List<GoalStatusObject> goalsStatus) {
		this.goalsStatus = goalsStatus;
	}

}
