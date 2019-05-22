package com.tripshot.example.embed;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Main {

  private static void usage() {
    System.err.println("usage : --config <configfile>");
    System.exit(1);
  }

  public static void main(String args[]) throws Exception {

    if ( args.length != 2 || !args[0].equals("--config") )
      usage();

    Path configFile = Paths.get(args[1]);

    Properties config = new Properties();
    try (InputStream is = Files.newInputStream(configFile)) {
      config.load(is);
    }

    ServletContextHandler context = new ServletContextHandler();
    context.setContextPath("/");

    ServletContainer servletContainer = new org.glassfish.jersey.servlet.ServletContainer(new App(config));
    ServletHolder servletHolder = new ServletHolder(servletContainer);
    context.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
    context.addServlet(servletHolder, "/*");

    Server jettyServer = new Server();
    jettyServer.setHandler(context);

    HttpConfiguration https = new HttpConfiguration();
    https.addCustomizer(new SecureRequestCustomizer());

    ServerConnector httpConnector = new ServerConnector(jettyServer, new HttpConnectionFactory(https));
    httpConnector.setPort(8080);
    jettyServer.addConnector(httpConnector);

    try {
      jettyServer.start();
      jettyServer.join();
    } finally {
      jettyServer.destroy();
    }
  }

}
