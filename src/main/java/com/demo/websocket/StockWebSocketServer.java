package com.demo.websocket;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint("/actions/stock")
public class StockWebSocketServer
{

   private final StockHandler stockHandler;

   public StockWebSocketServer()
   {
      this.stockHandler = CDI.current().select(StockHandler.class).get();
      scheduleMessageOnFixedPeriod();
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
               stockHandler.sendNewStock();
            }
         }
      }.start();
   }


   @OnOpen
   public void open(Session session)
   {
      System.out.println("open session:" + session.getId());
      stockHandler.addSession(session);
   }

   @OnClose
   public void close(Session session)
   {
      System.out.println("close session:" + session.getId());
      stockHandler.removeSession(session);
   }

   @OnError
   public void onError(Throwable error)
   {
      error.printStackTrace();
   }

   @OnMessage
   public void handleMessage(String message, Session session)
   {}

}