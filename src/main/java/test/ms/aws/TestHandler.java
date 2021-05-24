package test.ms.aws;



import static org.folio.edge.core.Constants.APPLICATION_JSON;
import static org.folio.edge.core.Constants.MSG_INTERNAL_SERVER_ERROR;


import java.util.Map;
import java.util.concurrent.TimeoutException;
import org.apache.log4j.Logger;
import org.folio.edge.core.Handler;
import org.folio.edge.core.security.SecureStore;
import org.folio.edge.core.utils.OkapiClient;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;


public class TestHandler extends Handler {
	

		  private static final Logger logger = Logger.getLogger(TestHandler.class);
		  
		  
		  public TestHandler(SecureStore secureStore, TestClientFactory ocf) {
		    super(secureStore, ocf);
		  }
		  
		  @Override
		  protected void handleCommon(RoutingContext ctx, String[] requiredParams, String[] optionalParams,
				  TwoParamVoidFunction<OkapiClient, Map<String, String>> action) {
			  
			      super.handleCommon(ctx, requiredParams, optionalParams, (client, params) -> {
			          final TestClient testClient = new TestClient(client);
				      action.apply(testClient, params);
			    });
		  }
		  
		  
		  protected void handleConfigCheck(RoutingContext ctx) {
			  
			    handleCommon(ctx,
			        new String[] {},
			        new String[] {},
			        (client, params) -> {
			          logger.info("sending GET request to NcipOkapiClient");
			          ((TestClient) client).ncipConfigCheck(
			              ctx.getBodyAsString(),
			              ctx.request().headers(),
			              resp -> handleProxyResponse(ctx, resp),
			              t -> handleProxyException(ctx, t));
			        });
		  }
		  
		  protected void handleHealthCheck(RoutingContext ctx) {
			  
			    handleCommon(ctx,
			        new String[] {},
			        new String[] {},
			        (client, params) -> {
			          logger.info("sending GET request to NcipOkapiClient - health check");
			          ((TestClient) client).ncipHealthCheck(
			              ctx.getBodyAsString(),
			              ctx.request().headers(),
			              resp -> handleProxyResponse(ctx, resp),
			              t -> handleProxyException(ctx, t));
			        });
		  }
		  
		  protected void helloWorld(RoutingContext ctx) {
		    ctx.response()
		      .setStatusCode(200)
		      .putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
		      .end(getStructuredErrorMessage(200, "hello world"));
		  }
		  
		  

		  protected void handle(RoutingContext ctx) {
			  
			    handleCommon(ctx,
			        new String[] {},
			        new String[] {},
			        (client, params) -> {
			          logger.info("sending POST request to NcipOkapiClient");
			          ((TestClient) client).callNcip(
			              ctx.getBodyAsString(),
			              ctx.request().headers(),
			              resp -> handleProxyResponse(ctx, resp),
			              t -> handleProxyException(ctx, t));
			        });
		  }
		  
		  @Override
		  protected void handleProxyException(RoutingContext ctx, Throwable t) {
		    logger.error("Exception calling OKAPI", t);
		    if (t instanceof TimeoutException) {
		      requestTimeout(ctx, t.getMessage());
		    } else {
		      internalServerError(ctx, t.getMessage());
		    }
		  }
		  
		  @Override
		  protected void internalServerError(RoutingContext ctx, String msg) {
		    if (!ctx.response().ended()) {
		      ctx.response()
		        .setStatusCode(500)
		        .putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
		        .end(getStructuredErrorMessage(500, MSG_INTERNAL_SERVER_ERROR));
		    }
		  }
		  
		  private String getStructuredErrorMessage(int statusCode, String message){
			    String finalMsg;
			    try{
			      ErrorMessage error = new ErrorMessage(statusCode, message);
			      finalMsg = error.toJson();
			    }
			    catch(JsonProcessingException ex){
			      finalMsg = "{ code : \"\", message : \"" + message + "\" }";
			    }
			    return finalMsg;
		}

		  protected void handleProxyResponse(RoutingContext ctx, HttpClientResponse resp) {
		      
		      HttpServerResponse serverResponse = ctx.response();
		      
		      final StringBuilder body = new StringBuilder();
		      resp.handler(buf -> {  
		    	  body.append(buf);
		      }).endHandler(v -> {
		    	  
	    	  int statusCode = resp.statusCode();
	    	  serverResponse.setStatusCode(statusCode);
	    	  
	    	  String respBody = body.toString();
	    	  
	    	  String contentType = resp.getHeader(HttpHeaders.CONTENT_TYPE);
	    	  setContentType(serverResponse,contentType);
	          serverResponse.end(respBody);

		      });
		   }

		  
		  private void setContentType(HttpServerResponse response, String contentType){
			    if (contentType != null && !contentType.equals("")) {
			        response.putHeader(HttpHeaders.CONTENT_TYPE, contentType);
			    }
		  }
		
	}
		
