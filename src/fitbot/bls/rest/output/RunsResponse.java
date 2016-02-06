package fitbot.bls.rest.output;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


public class RunsResponse extends BasicResponse{

	@JsonInclude(Include.NON_NULL)
	private List<RunResponseObject> runs;
	
	public RunsResponse(){
		super();
	}

	public List<RunResponseObject> getRuns() {
		return runs;
	}

	public void setRuns(List<RunResponseObject> runs) {
		this.runs = runs;
	}
	
	
}
