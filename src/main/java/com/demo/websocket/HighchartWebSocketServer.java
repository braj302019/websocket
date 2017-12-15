package com.demo.websocket;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint("/actions/highchart")
public class HighchartWebSocketServer
{

   private final Set<Session> sessions = new HashSet<Session>();

   public HighchartWebSocketServer()
   {
      scheduleMessageOnFixedPeriod();
   }

   @OnOpen
   public void open(Session session)
   {
      System.out.println("open session:" + session.getId());
      addSession(session);
   }

   @OnClose
   public void close(Session session)
   {
      System.out.println("close session:" + session.getId());
      removeSession(session);
   }

   @OnError
   public void onError(Throwable error)
   {
      error.printStackTrace();
   }

   @OnMessage
   public void handleMessage(String message, Session session)
   {
      System.out.println("handle message from session:" + session.getId());
   }

   private void scheduleMessageOnFixedPeriod()
   {
      new Thread()
      {
         @Override
         public void run()
         {
            while (true)
            {
               try
               {
                  Thread.sleep(1000);
               }
               catch (InterruptedException e)
               {
                  e.printStackTrace();
               }
               if (sessions.size() > 0)
               {
                  JsonObject newMessage = createNewMessage();
                  sendToAllConnectedSessions(newMessage);
               }
            }
         }
      }.start();
   }

   private void addSession(Session session)
   {
      sessions.add(session);
      System.out.println("total session count:" + sessions.size());
   }

   private void removeSession(Session session)
   {
      sessions.remove(session);
      System.out.println("total session count:" + sessions.size());
   }

   private JsonObject createNewMessage()
   {
      JsonProvider provider = JsonProvider.provider();
      JsonObject addMessage = provider.createObjectBuilder().add("x", new Date().getTime()).add("y", Math.random() * 100).build();
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