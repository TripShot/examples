package com.tripshot.example.embed;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.api.client.util.Preconditions;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URL;

@Resource
@Path("v1")
public class EmbedWidgetResource {

  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  public static class LoginRequest {
    @Key
    private final String username;
    @Key
    private final String password;
    @Key("transient")
    private final boolean transient_;

    public LoginRequest(String username, String password, boolean transient_) {
      this.username = username;
      this.password = password;
      this.transient_ = transient_;
    }
  }

  public static class AccessTokenResponse {
    @SuppressWarnings("unused")
    @Key
    private String token;
  }

  private final String baseUrl;

  private final String username;

  private final String password;

  private HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(request -> request.setParser(new JsonObjectParser(JSON_FACTORY)));

  public EmbedWidgetResource(String baseUrl, String username, String password) {
    this.baseUrl = Preconditions.checkNotNull(baseUrl);
    this.username = Preconditions.checkNotNull(username);
    this.password = Preconditions.checkNotNull(password);
  }

  @GET
  @Path("token")
  @Produces("application/json")
  public Response getTripshotToken() throws IOException {

    HttpRequest accessTokenRequest =
      requestFactory.buildPostRequest(new GenericUrl(baseUrl + "/v1/login"), new JsonHttpContent(JSON_FACTORY, new LoginRequest(username, password, true)));
    AccessTokenResponse accessTokenResponse = accessTokenRequest.execute().parseAs(AccessTokenResponse.class);

    return Response.ok(accessTokenResponse.token).build();
  }

  @GET
  @Path("top.html")
  @Produces("text/html")
  public Response getTop() throws IOException {

    URL resource = EmbedWidgetResource.class.getClassLoader().getResource("top.html");
    return Response.ok(resource.getContent()).build();
  }
}
