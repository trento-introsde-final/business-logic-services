package fitbot.bls.rest.output;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="Goal_Measure_Type")
@NamedQuery(name="GoalType.findAll", query="SELECT gt FROM GoalType gt")
public class GoalType {

	@Id
	@Column(name="id", updatable=false)
	private String id;
	
	@Column(name="units")
	private String units;
	
	@Column(name="name", unique=true)
	private String name;
	
	public GoalType(){
		
	}

	public String getId() {
		return id;
	}

	public String getUnits() {
		return units;
	}

	public String getName() {
		return name;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
