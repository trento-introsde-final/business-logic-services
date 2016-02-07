package fitbot.bls.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONObject;

import fitbot.bls.exception.ServerCommunicationException;
import fitbot.bls.rest.output.BasicResponse;
import fitbot.bls.rest.output.GoalResponseObject;
import fitbot.bls.rest.output.GoalStatusObject;
import fitbot.bls.rest.output.GoalStatusResponse;
import fitbot.bls.rest.output.GoalsResponse;
import fitbot.bls.rest.output.RunResponseObject;
import fitbot.bls.rest.output.RunsResponse;
import fitbot.bls.util.UrlInfo;

public class UsersResource {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    
	UrlInfo urlInfo;
	
	String storageServiceURL;
	String URLUsers = "%s/users/%s";
	String URLUsersRuns = "%s/users/%s/runs?start_date=%d";
	String URLUsersGoals = "%s/users/%s/goals";
	
	String jsonResponse = "";
	
	public UsersResource(UriInfo uriInfo, Request request) {
        this.uriInfo = uriInfo;
        this.request = request;
		urlInfo = new UrlInfo();
		storageServiceURL = urlInfo.getStorageServicesURL();
	}
	
    /**
     * Create a new Person. 
     * On success returns status 201 and Location header set to the URI
     * of the newly created Person.
     * 
     * @param person
     * @return Returns the created Person
     * @throws IOException
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response newPerson(String inputUserJSON) {
    	Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(String.format(URLUsers, storageServiceURL, ""));
		Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
		
		Response res = builder.post(Entity.json(inputUserJSON));
		
		String pp = res.readEntity(String.class);
		
		if(res.getStatus() != 201){
			String message = "Problem at Storage Services. ";
			if(pp != null && pp.length()!=0){
				JSONObject jo;
				try{
					jo = new JSONObject(pp);
					if(jo.getString("status").equals("ERROR")){
						message += " Server replied: "+jo.getString("error");
					}
				} catch (Exception e){
					message += " Got code: "+res.getStatus();
				}
			}
			BasicResponse bResp = new BasicResponse(message);
			return Response.status(200).entity(bResp).build();
		}
		String[] pathParts = res.getLocation().getPath().split("/");
		String id = pathParts[pathParts.length-1];
		String newLocation = String.format(URLUsers, urlInfo.getBusinessLogicServicesURL(), id); 
		Response nRes = null;
		try{
			nRes = Response.created(new URI(newLocation)).entity(pp).build();
			
		} catch (URISyntaxException e){
			BasicResponse bResp = new BasicResponse("Error determining resource location URI.");
			return Response.serverError().entity(bResp).build();
		}
		return nRes;

    }
 
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("{userId}")
    public Response updatePerson(@PathParam("userId") int userId, String inputUserJSON){
    	Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(String.format(URLUsers, storageServiceURL, userId));
		Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
		
		Response res = builder.put(Entity.json(inputUserJSON));
		
		String pp = res.readEntity(String.class);
			
		if (res.getStatus() != 200) {
			String message = "Problem at Storage Services. ";
			if(pp != null && pp.length()!=0){
				JSONObject jo;
				try{
					jo = new JSONObject(pp);
					if(jo.getString("status").equals("ERROR")){
						message += " Server replied: "+jo.getString("error");
					}
				} catch (Exception e){
					message += " Got code: "+res.getStatus();
				}
			}
			BasicResponse bResp = new BasicResponse(message);
			return Response.status(200).entity(bResp).build();
		}
		Response nRes = Response.ok(pp).build();
        return nRes;
    }


    @GET
    @Path("{userId}/goal-status")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPersonalGoalStatus(@PathParam("userId") int userId) {
    	//GET goals
    	GoalsResponse goalsResponse;
    	try {
    		goalsResponse = getUserGoals(userId);
    	} catch (ServerCommunicationException e){
    		BasicResponse bResp = new BasicResponse(e.getMessage());
    		return Response.ok(bResp).build();
    	}
		List<GoalResponseObject> goals = goalsResponse.getGoals();
    	
    	//calculate date from which to fetch runs
    	Date today = new Date();
		long todayMillis = getMillisDayStart(today.getTime());
    	int fetchPeriod = 0;
    	for(GoalResponseObject g: goals){
    		if(g.getPeriod_days()>fetchPeriod){
    			fetchPeriod = g.getPeriod_days();
    		}
    	}
    	long startDate = todayMillis - fetchPeriod*86400000;//TODO: Subtract days using java's methods
    	
    	//GET runs
    	RunsResponse runsResponse;
    	try {
    		runsResponse = getUserRuns(userId, startDate);
    	} catch (ServerCommunicationException e){
    		BasicResponse bResp = new BasicResponse(e.getMessage());
    		return Response.ok(bResp).build();
    	}
    	List<RunResponseObject> runs = runsResponse.getRuns();
    	
    	//calculate amount done or missing for every goal
    	List<GoalStatusObject> gStatusList = new ArrayList<GoalStatusObject>();
    	for(GoalResponseObject g: goals){
    		GoalStatusObject gStatus = new GoalStatusObject();
    		gStatus.setType(g.getMeasure_type());
    		gStatus.setName(g.getName());
    		gStatus.setUnits(g.getUnits());
    		gStatus.setTarget(g.getTarget());
    		gStatus.setPeriod(g.getPeriod());
    		
    		//sum up relevant runs
    		long period = g.getPeriod_days()*86400000;
			long createdDay = getMillisDayStart(g.getCreated().getTime());
			long periodStart = ((todayMillis - createdDay)/period)*period+createdDay;
			long periodEnd =  periodStart+period;
			float accum = 0;
    		for(RunResponseObject r: runs){
    			//is run inside latest period for this goal?
    			if(r.getStart_date().getTime() >= periodStart){
    				switch(g.getMeasure_type()){
    				case "distance":
    					accum += r.getDistance();
    					break;
    				case "calories":
    					accum += r.getCalories();
    					break;
    				case "moving_time":
    					accum += Integer.valueOf(r.getMoving_time()).floatValue();
    					break;
    				case "elevation_gain":
    					accum += r.getElevation_gain();
    					break;
    				case "max_speed":
    					accum += r.getMax_speed();
    					break;
    				case "avg_speed":
    					accum += r.getAvg_speed();
    					break;
    				case "steps":
    					accum += Integer.valueOf(r.getSteps()).floatValue();
    					break;
    				}
    			}
    		}
    		gStatus.setPeriod_start(periodStart);
    		gStatus.setPeriod_end(periodEnd);
    		gStatus.setCount(accum);
    		gStatus.setGoal_met(accum >= g.getTarget());
    		
    		gStatusList.add(gStatus);
    	}
    	
    	GoalStatusResponse gStatusResp = new GoalStatusResponse();
    	gStatusResp.setGoal_status(gStatusList);
    	Response nRes = Response.ok(gStatusResp).build();
        return nRes;
    }
  
    private GoalsResponse getUserGoals(int userId) throws ServerCommunicationException{
    	Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(String.format(URLUsersGoals, storageServiceURL, userId));
		Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
		
		Response res = builder.get();
		
		if (res.getStatus() != 200){
			String message = "Error retrieving user's goals. Got ERROR status code: "+res.getStatus();
			throw new ServerCommunicationException(message);
		}
		GoalsResponse pp = res.readEntity(GoalsResponse.class);
		
		return pp;
    }
    
    private RunsResponse getUserRuns(int userId, long startDate) throws ServerCommunicationException{
    	Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(String.format(URLUsersRuns, storageServiceURL, userId, startDate));
		Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
		
		Response res = builder.get();
		
		if (res.getStatus() != 200){
			String message = "Error retrieving user's runs. Got ERROR status code: "+res.getStatus();
			throw new ServerCommunicationException(message);
		}
		RunsResponse pp = res.readEntity(RunsResponse.class);
		
		return pp;
    }
    
    private long getMillisDayStart(long timestamp){
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(timestamp);
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	return calendar.getTimeInMillis();
    }
    
 /* 
  *  I implemented it but we are not using it according to our API. Daniel, 6 Feb. 2016
  *   
  *   @GET
    @Path("{userId}/runs")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getRecentRuns(
    		@PathParam("userId") int userId, 
    		@DefaultValue("0") @QueryParam("start_date") long startDate) {
    	Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(String.format(URLUsersRuns, storageServiceURL, userId, startDate));
		Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
		
		Response res = builder.get();
		
		String pp = res.readEntity(String.class);
			
		if (res.getStatus() != 200) {
			String message = "Problem at Storage Services. ";
			if(pp != null){
				JSONObject jo = new JSONObject(pp);
				if(jo.getString("status").equals("ERROR")){
					message += " Server replied: "+jo.getString("error");
				}
			}
			BasicResponse bResp = new BasicResponse(message);
			return Response.status(200).entity(bResp).build();
		}
		Response nRes = Response.ok(pp).build();
        return nRes;
    }*/
    
}
