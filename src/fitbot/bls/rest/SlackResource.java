package fitbot.bls.rest;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
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

@LocalBean
@Stateless
public class SlackResource {
	
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    
	UrlInfo urlInfo;
	
	String storageServiceURL;
	String URLGetUserId = "%s/user-id/%s";
	
	public SlackResource(UriInfo uriInfo, Request request) {
        this.uriInfo = uriInfo;
        this.request = request;
		urlInfo = new UrlInfo();
		storageServiceURL = urlInfo.getStorageServicesURL();
	}
	
	@GET
    @Path("{slackUserId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUserBySlackId(@PathParam("slackUserId") String slackUserId){
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(String.format(URLGetUserId, storageServiceURL, slackUserId));
		Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
		
		Response res = builder.get();
		
		String pp = res.readEntity(String.class);
			
		if (res.getStatus() != 200) {
			String message = "Problem at Storage Services.";
			if(!pp.isEmpty()){
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

}