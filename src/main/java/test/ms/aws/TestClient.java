package test.ms.aws;
import org.folio.edge.core.utils.OkapiClient;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpHeaders;

import static org.folio.edge.core.Constants.XML_OR_TEXT;


public class TestClient extends OkapiClient {
	
	
	  protected TestClient(Vertx vertx, String okapiURL, String tenant, long timeout) {
		    super(vertx, okapiURL, tenant, timeout);
	  }

	  public TestClient(OkapiClient client) {
	    super(client);
	  }

	  @Override
	  protected void initDefaultHeaders() {
	    super.initDefaultHeaders();
	    defaultHeaders.set(HttpHeaders.ACCEPT, XML_OR_TEXT);
	    defaultHeaders.set(HttpHeaders.CONTENT_TYPE, "application/xml"); 
	
	  }
	  
	  
	  public void ncipConfigCheck(String payload, MultiMap headers,
		      Handler<HttpClientResponse> responseHandler,
		      Handler<Throwable> exceptionHandler) {
		      System.out.println("in send get request in the client");
			    get(
			        String.format("%s/ncipconfigcheck", okapiURL),
			        tenant,
			        defaultHeaders,
			        responseHandler,
			        exceptionHandler);
			  }
	  
	  
	  public void ncipHealthCheck(String payload, MultiMap headers,
		      Handler<HttpClientResponse> responseHandler,
		      Handler<Throwable> exceptionHandler) {
		      System.out.println("in send get request in the client");
			    get(
			        String.format("%s/nciphealthcheck", okapiURL),
			        tenant,
			        defaultHeaders,
			        responseHandler,
			        exceptionHandler);
	}
	  
	  
	  public void callNcip(String payload, MultiMap headers,
		      Handler<HttpClientResponse> responseHandler,
		      Handler<Throwable> exceptionHandler) {
		      System.out.println(String.format("%s/ncip", okapiURL));
			    post(
			        String.format("%s/ncip", okapiURL),
			        tenant,
			        payload,
			        defaultHeaders,
			        responseHandler,
			        exceptionHandler);
			  }
		    
}