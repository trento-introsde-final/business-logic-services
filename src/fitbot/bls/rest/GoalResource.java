package fitbot.bls.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONObject;

import fitbot.bls.rest.output.BasicResponse;
import fitbot.bls.util.UrlInfo;

public class GoalResource {
	
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    
	UrlInfo urlInfo;
	
	String storageServiceURL;
	String URLGoals = "%s/goal-types/%s";
	
	String jsonResponse = "";
	
	public GoalResource(UriInfo uriInfo, Request request) {
        this.uriInfo = uriInfo;
        this.request = request;
		urlInfo = new UrlInfo();
		storageServiceURL = urlInfo.getStorageServicesURL();
	}

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getGoalTypes(){
    	Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(String.format(URLGoals, storageServiceURL, ""));
		Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
		
		Response res = builder.get();
		
		String pp = res.readEntity(String.class);
			
		if (res.getStatus() != 200) {
			String message = "Problem at Storage Services. ";
			if(!pp.isEmpty()){
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
    }
    
    @GET
    @Path("{goalType}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getGoalType(@PathParam("goalType") String goalType) {
    	Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(String.format(URLGoals, storageServiceURL, goalType));
		Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
		
		Response res = builder.get();
		
		String pp = res.readEntity(String.class);
			
		if (res.getStatus() != 200) {
			String message = "Problem at Storage Services. ";
			if(!pp.isEmpty()){
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
    }

}
