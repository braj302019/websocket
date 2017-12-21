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
@ServerEndpoint("/actions/highchart")
public class HighchartWebSocketServer
{

   private final HighchartHandler highchartHandler;

   public HighchartWebSocketServer()
   {
      this.highchartHandler = CDI.current().select(HighchartHandler.class).get();
   }

   @OnOpen
   public void open(Session session)
   {
      System.out.println("open session:" + session.getId());
      highchartHandler.addSession(session);
   }

   @OnClose
   public void close(Session session)
   {
      System.out.println("close session:" + session.getId());
      highchartHandler.removeSession(session);
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

}