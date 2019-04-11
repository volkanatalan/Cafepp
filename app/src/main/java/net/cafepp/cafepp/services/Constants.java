package net.cafepp.cafepp.services;

public class Constants {
  public interface ACTION {
    public static String MAIN_ACTION = "net.cafepp.cafepp.services.action.main";
    public static String START_ACTION = "net.cafepp.cafepp.services.action.start";
    public static String STOP_ACTION = "net.cafepp.cafepp.services.action.stop";
  }
  
  public interface NOTIFICATION_ID {
    public static int SERVER_SERVICE = 101;
    public static int CLIENT_SERVICE = 102;
  }
}
