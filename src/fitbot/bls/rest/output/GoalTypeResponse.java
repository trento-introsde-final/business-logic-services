package fitbot.bls.rest.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class GoalTypeResponse extends BasicResponse {

	@JsonInclude(Include.NON_NULL)
	private String id;

	@JsonInclude(Include.NON_NULL)
	private String name;
	
	@JsonInclude(Include.NON_NULL)
	private String units;
	
	public GoalTypeResponse(){
		super();
	}
	
	public GoalTypeResponse(String message){
		super(message);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUnits() {
		return units;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUnits(String units) {
		this.units = units;
	}
	
	
}
