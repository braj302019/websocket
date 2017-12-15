package com.demo.websocket;

public class Device
{
   private int id;
   private String status;
   private String name;
   private String type;
   private String description;

   public void setId(int deviceId)
   {
      this.id = deviceId;
   }

   public int getId()
   {
      return id;
   }

   public void setStatus(String status)
   {
      this.status = status;
   }

   public String getStatus()
   {
      return status;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public String getName()
   {
      return name;
   }

   public String getType()
   {
      return type;
   }

   public String getDescription()
   {
      return description;
   }

   public void setDescription(String description)
   {
      this.description = description;
   }

}
