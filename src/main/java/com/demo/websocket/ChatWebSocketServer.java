package com.demo.websocket;

import java.io.StringReader;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint("/actions/chat")
public class ChatWebSocketServer
{

   private final ChatHandler chatHandler;

   public ChatWebSocketServer()
   {
      this.chatHandler = CDI.current().select(ChatHandler.class).get();
   }

   @OnOpen
   public void open(Session session)
   {
      System.out.println("open session:" + session.getId());
      chatHandler.addSession(session);
   }

   @OnClose
   public void close(Session session)
   {
      System.out.println("close session:" + session.getId());
      chatHandler.removeSession(session);
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
      try (JsonReader reader = Json.createReader(new StringReader(message)))
      {
         JsonObject jsonMessage = reader.readObject();

         System.out.println("message:" + jsonMessage);
         if ("add".equals(jsonMessage.getString("action")))
         {
            Message chat = new Message();
            chat.setWhen(System.currentTimeMillis());
            chat.setMessage(jsonMessage.getString("message"));
            chatHandler.addMessage(chat);
         }
      }

   }

}