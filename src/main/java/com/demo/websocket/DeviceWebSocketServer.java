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
@ServerEndpoint("/actions/device")
public class DeviceWebSocketServer
{

   private final DeviceHandler deviceHandler;

   public DeviceWebSocketServer()
   {
      this.deviceHandler = CDI.current().select(DeviceHandler.class).get();
   }

   @OnOpen
   public void open(Session session)
   {
      System.out.println("open session:" + session.getId());
      deviceHandler.addSession(session);
   }

   @OnClose
   public void close(Session session)
   {
      System.out.println("close session:" + session.getId());
      deviceHandler.removeSession(session);
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
            Device device = new Device();
            device.setName(jsonMessage.getString("name"));
            device.setDescription(jsonMessage.getString("description"));
            device.setType(jsonMessage.getString("type"));
            device.setStatus("Off");
            deviceHandler.addDevice(device);
         }

         if ("remove".equals(jsonMessage.getString("action")))
         {
            int id = jsonMessage.getInt("id");
            deviceHandler.removeDevice(id);
         }

         if ("toggle".equals(jsonMessage.getString("action")))
         {
            int id = jsonMessage.getInt("id");
            deviceHandler.toggleDevice(id);
         }
      }

   }

}