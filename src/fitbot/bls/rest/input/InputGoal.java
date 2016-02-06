package fitbot.bls.rest.input;


public class InputGoal {

	private Float target;
	
	private String period;
	
	public InputGoal(){
		
	}

	public Float getTarget() {
		return target;
	}

	public String getPeriod() {
		return period;
	}

	public void setTarget(Float target) {
		this.target = target;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
	
}
