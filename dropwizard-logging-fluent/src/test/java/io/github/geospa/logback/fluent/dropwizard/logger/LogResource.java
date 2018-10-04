package io.github.geospa.logback.fluent.dropwizard.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public final class LogResource {

   private final Logger log;

   LogResource(String name) {log = LoggerFactory.getLogger(name);}

   @GET
   @Path("/log/{log-msg}")
   public String log(
      @PathParam("log-msg") String logMsg
   ) {
      log.info(logMsg);
      return "Ok";
   }

   @GET
   @Path("/error/{error-msg}")
   public String err(
      @PathParam("error-msg") String errorMsg
   ) {
      log.error(errorMsg, new RuntimeException(errorMsg));
      return "Ok";
   }
}
