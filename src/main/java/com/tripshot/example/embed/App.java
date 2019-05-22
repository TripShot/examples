package com.tripshot.example.embed;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import java.util.Properties;

@ApplicationPath("/")
public class App extends ResourceConfig {

  public App(Properties config) {

    register(new AbstractBinder() {
      protected void configure() {
        bind(new EmbedWidgetResource(config.getProperty("baseUrl"), config.getProperty("username"), config.getProperty("password")));
      }
    });

    registerClasses(EmbedWidgetResource.class);
  }
}