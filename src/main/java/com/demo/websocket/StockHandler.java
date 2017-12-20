package com.demo.websocket;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

@ApplicationScoped
public class StockHandler
{

   private final Set<Session> sessions = new HashSet<Session>();
   private final List<Stock> stocks = new ArrayList<Stock>();
   private final DecimalFormat decimalFormatter = new DecimalFormat("#.##");

   public StockHandler()
   {
      System.out.println("Created chat handler");
      initStocks();
   }

   private void initStocks()
   {
      stocks.add(new Stock(0, "STC", 2.44, 3.62));
      stocks.add(new Stock(1, "Telenor", 12.84, 19.44));
      stocks.add(new Stock(2, "Maxis", 5.69, 8.67));
      stocks.add(new Stock(3, "Verizon", 3.19, 4.15));
      stocks.add(new Stock(4, "Vodafone", 1.83, 2.77));
   }

   public void init(@Observes @Initialized(ApplicationScoped.class) Object init)
   {
      System.out.println("Initialized stock handler");
   }

   public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object init)
   {
      System.out.println("Destroyed stock handler");
      sessions.clear();
      stocks.clear();
   }

   public void addSession(Session session)
   {
      sessions.add(session);
      for (Stock stock : stocks)
      {
         JsonObject addMessage = createAddMessage(stock);
         sendToSession(session, addMessage);
      }
      System.out.println("total session count:" + sessions.size());
   }

   public void removeSession(Session session)
   {
      sessions.remove(session);
      System.out.println("total session count:" + sessions.size());
   }

   public void updateStock(Stock stock)
   {
      stocks.set(stock.getId(), stock);
      JsonObject addMessage = createAddMessage(stock);
      sendToAllConnectedSessions(addMessage);
      System.out.println("total messages count:" + stocks.size());
   }

   public void sendNewStock()
   {
      int stockId = (int) (Math.random() * stocks.size());

      Stock stock = stocks.get(stockId);
      double price = stock.getMin() + (int) (Math.random() * stock.getMax());
      price = Double.valueOf(decimalFormatter.format(price));
      double change = (stock.getPrice() - price) / price;
      change = Double.valueOf(decimalFormatter.format(change));
      stock.setPrice(price);
      stock.setChange(change);
      stock.setTime(System.currentTimeMillis());
      updateStock(stock);
   }

   private JsonObject createAddMessage(Stock stock)
   {
      JsonProvider provider = JsonProvider.provider();
      JsonObject addMessage = provider
         .createObjectBuilder()
         .add("action", "change")
         .add("id", stock.getId())
         .add("name", stock.getName())
         .add("min", stock.getMin())
         .add("max", stock.getMax())
         .add("time", stock.getTime())
         .add("price", stock.getPrice())
         .add("change", stock.getChange())
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