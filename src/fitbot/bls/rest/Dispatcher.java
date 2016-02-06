package fitbot.bls.rest;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;


@Stateless
@LocalBean
@Path("/")
public class Dispatcher {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	/**
	 * Very important to say hello before requesting anything. Also to wake heroku up
	 * @return 'Hola'
	 */
	@GET
	@Path("hola")
	@Produces(MediaType.TEXT_PLAIN)
	public String getHola() {
		return "Hola";
	}

	@Path("goal-types")
	public GoalResource routeGoal() {
		return new GoalResource(uriInfo, request);
	}

	@Path("users")
	public UsersResource routeUserCollection() {
		return new UsersResource(uriInfo, request);
	}

	@Path("user-id")
	public SlackResource routeUserId() {
		return new SlackResource(uriInfo, request);
	}
}