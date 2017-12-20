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
public class DeviceHandler
{

   private int deviceId = 0;
   private final Set<Session> sessions = new HashSet<Session>();
   private final Set<Device> devices = new HashSet<Device>();

   public DeviceHandler()
   {
      System.out.println("Created device handler");
   }

   public void init(@Observes @Initialized(ApplicationScoped.class) Object init)
   {
      System.out.println("Initialized device handler");
   }

   public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object init)
   {
      System.out.println("Destroyed device handler");
      sessions.clear();
      devices.clear();
   }

   public void addSession(Session session)
   {
      sessions.add(session);
      for (Device device : devices)
      {
         JsonObject addMessage = createAddMessage(device);
         sendToSession(session, addMessage);
      }
      System.out.println("total session count:" + sessions.size());
   }

   public void removeSession(Session session)
   {
      sessions.remove(session);
      System.out.println("total session count:" + sessions.size());
   }

   public void addDevice(Device device)
   {
      device.setId(deviceId);
      devices.add(device);
      deviceId++;
      JsonObject addMessage = createAddMessage(device);
      sendToAllConnectedSessions(addMessage);
      System.out.println("total device count:" + devices.size());
   }

   public void removeDevice(int id)
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

   public void toggleDevice(int id)
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