package com.demo.websocket;

public class Stock
{
   private final int id;
   private final String name;
   private final double min;
   private final double max;

   private double price;
   private double change;
   private long time;

   public Stock(int id, String name, double min, double max)
   {
      this.id = id;
      this.name = name;
      this.min = min;
      this.max = max;
   }

   public int getId()
   {
      return id;
   }

   public String getName()
   {
      return name;
   }

   public double getPrice()
   {
      return price;
   }

   public void setPrice(double price)
   {
      this.price = price;
   }

   public double getMin()
   {
      return min;
   }

   public double getMax()
   {
      return max;
   }

   public double getChange()
   {
      return change;
   }

   public void setChange(double change)
   {
      this.change = change;
   }

   public long getTime()
   {
      return time;
   }

   public void setTime(long time)
   {
      this.time = time;
   }

}
