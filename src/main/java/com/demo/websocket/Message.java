package com.demo.websocket;

public class Message
{
   private long when;
   private String message;

   public long getWhen()
   {
      return when;
   }

   public void setWhen(long when)
   {
      this.when = when;
   }

   public String getMessage()
   {
      return message;
   }

   public void setMessage(String message)
   {
      this.message = message;
   }

}
