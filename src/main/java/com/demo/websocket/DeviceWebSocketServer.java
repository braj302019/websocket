package com.demo.websocket;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;
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

   private int deviceId = 0;
   private final Set<Session> sessions = new HashSet<Session>();
   private final Set<Device> devices = new HashSet<Device>();

   @OnOpen
   public void open(Session session)
   {
      System.out.println("server id:" + this.hashCode());
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
            addDevice(device);
         }

         if ("remove".equals(jsonMessage.getString("action")))
         {
            int id = jsonMessage.getInt("id");
            removeDevice(id);
         }

         if ("toggle".equals(jsonMessage.getString("action")))
         {
            int id = jsonMessage.getInt("id");
            toggleDevice(id);
         }
      }

   }

   private void addSession(Session session)
   {
      sessions.add(session);
      for (Device device : devices)
      {
         JsonObject addMessage = createAddMessage(device);
         sendToSession(session, addMessage);
      }
      System.out.println("total session count:" + sessions.size());
   }

   private void removeSession(Session session)
   {
      sessions.remove(session);
      for (Device device : devices)
      {
         JsonObject addMessage = createAddMessage(device);
         sendToSession(session, addMessage);
      }
      System.out.println("total session count:" + sessions.size());
   }

   private void addDevice(Device device)
   {
      device.setId(deviceId);
      devices.add(device);
      deviceId++;
      JsonObject addMessage = createAddMessage(device);
      sendToAllConnectedSessions(addMessage);
      System.out.println("total device count:" + devices.size());
   }

   private void removeDevice(int id)
   {
      Device device = getDeviceById(id);
      if (device != null)
      {
         devices.remove(device);
         JsonProvider provider = JsonProvider.provider();
         JsonObject removeMessage = provider.createObjectBuilder().add("action", "remove").add("id", id).build();
         sendToAllConnectedSessions(removeMessage);
      }
      System.out.println("total device count:" + devices.size());
   }

   private void toggleDevice(int id)
   {
      JsonProvider provider = JsonProvider.provider();
      Device device = getDeviceById(id);
      if (device != null)
      {
         if ("On".equals(device.getStatus()))
         {
            device.setStatus("Off");
         }
         else
         {
            device.setStatus("On");
         }
         JsonObject updateDevMessage = provider
            .createObjectBuilder()
            .add("action", "toggle")
            .add("id", device.getId())
            .add("status", device.getStatus())
            .build();
         sendToAllConnectedSessions(updateDevMessage);
      }
   }

   private Device getDeviceById(int id)
   {
      for (Device device : devices)
      {
         if (device.getId() == id)
         {
            return device;
         }
      }
      return null;
   }

   private JsonObject createAddMessage(Device device)
   {
      JsonProvider provider = JsonProvider.provider();
      JsonObject addMessage = provider
         .createObjectBuilder()
         .add("action", "add")
         .add("id", device.getId())
         .add("name", device.getName())
         .add("type", device.getType())
         .add("status", device.getStatus())
         .add("description", device.getDescription())
         .build();
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