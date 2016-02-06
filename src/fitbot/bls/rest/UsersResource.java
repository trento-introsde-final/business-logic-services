package fitbot.bls.rest;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import fitbot.bls.util.UrlInfo;

public class UsersResource {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    
	UrlInfo urlInfo;
	
	String storageServiceURL;
	String URLGetUserId = "%s/user-id/%s";
	
	String jsonResponse = "";
	
	public UsersResource(UriInfo uriInfo, Request request) {
        this.uriInfo = uriInfo;
        this.request = request;
		urlInfo = new UrlInfo();
		storageServiceURL = urlInfo.getStorageServicesURL();
	}
}
