package com.demo.websocket;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

@ApplicationScoped
public class HighchartHandler
{

   private final Set<Session> sessions = new HashSet<Session>();

   public HighchartHandler()
   {
      System.out.println("Created highchart handler");
   }

   public void init(@Observes @Initialized(ApplicationScoped.class) Object init)
   {
      System.out.println("Initialized highchart handler");
   }

   public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object init)
   {
      System.out.println("Destroyed highchart handler");
      sessions.clear();
   }

   public int getSessionCount()
   {
      return sessions.size();
   }

   public void addSession(Session session)
   {
      sessions.add(session);
      System.out.println("total session count:" + sessions.size());
   }

   public void removeSession(Session session)
   {
      sessions.remove(session);
      System.out.println("total session count:" + sessions.size());
   }

   public JsonObject createNewMessage(double value)
   {
      JsonProvider provider = JsonProvider.provider();
      JsonObject addMessage = provider.createObjectBuilder().add("x", new Date().getTime()).add("y", value).build();
      return addMessage;
   }

   public JsonObject createNewMessage()
   {
      JsonProvider provider = JsonProvider.provider();
      JsonObject addMessage = provider.createObjectBuilder().add("x", new Date().getTime()).add("y", Math.random() * 100).build();
      return addMessage;
   }

   public void sendToAllConnectedSessions(JsonObject message)
   {
      for (Session session : sessions)
      {
         sendToSession(session, message);
      }
   }

   public void sendToSession(Session session, JsonObject message)
   {
      try
      {
         session.getBasicRemote().sendText(message.toString());
      }
      catch (IOException ex)
      {
         sessions.remove(session);
         ex.printStackTrace();
      }
   }

}