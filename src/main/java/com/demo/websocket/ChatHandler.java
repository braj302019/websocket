package com.demo.websocket;

import java.io.IOException;
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
public class ChatHandler
{

   private final Set<Session> sessions = new HashSet<Session>();
   private final Set<Message> messages = new HashSet<Message>();

   public ChatHandler()
   {
      System.out.println("Created chat handler");
   }

   public void init(@Observes @Initialized(ApplicationScoped.class) Object init)
   {
      System.out.println("Initialized chat handler");
   }

   public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object init)
   {
      System.out.println("Destroyed chat handler");
      sessions.clear();
      messages.clear();
   }

   public void addSession(Session session)
   {
      sessions.add(session);
      for (Message message : messages)
      {
         JsonObject addMessage = createAddMessage(message);
         sendToSession(session, addMessage);
      }
      System.out.println("total session count:" + sessions.size());
   }

   public void removeSession(Session session)
   {
      sessions.remove(session);
      System.out.println("total session count:" + sessions.size());
   }

   public void addMessage(Message message)
   {
      messages.add(message);
      JsonObject addMessage = createAddMessage(message);
      sendToAllConnectedSessions(addMessage);
      System.out.println("total messages count:" + messages.size());
   }

   private JsonObject createAddMessage(Message message)
   {
      JsonProvider provider = JsonProvider.provider();
      JsonObject addMessage = provider.createObjectBuilder().add("action", "add").add("when", message.getWhen()).add("message", message.getMessage()).build();
      return addMessage;
   }

   private void sendToAllConnectedSessions(JsonObject message)
   {
      for (Session session : sessions)
      {
         sendToSession(session, message);
      }
   }

   private void sendToSession(Session session, JsonObject message)
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