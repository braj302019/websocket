package com.demo.websocket;

import java.io.IOException;

import javax.enterprise.inject.spi.CDI;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/highchart/data")
public class HighchartDataCollector extends HttpServlet
{

   private static final long serialVersionUID = 1L;

   private final HighchartHandler highchartHandler;

   public HighchartDataCollector()
   {
      this.highchartHandler = CDI.current().select(HighchartHandler.class).get();
   }

   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      Double value = Double.valueOf(request.getParameter("value"));
      JsonObject message = highchartHandler.createNewMessage(value);
      highchartHandler.sendToAllConnectedSessions(message);
   }
}