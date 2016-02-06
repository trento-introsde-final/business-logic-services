package fitbot.bls.rest.output;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class GoalTypesResponse extends BasicResponse{
	
	@JsonInclude(Include.NON_NULL)
	private List<GoalType> results;
	
	public GoalTypesResponse(){
		super();
	}
	
	public GoalTypesResponse(String message){
		super(message);
	}

	public List<GoalType> getResults() {
		return results;
	}

	public void setResults(List<GoalType> results) {
		this.results = results;
	}

}
