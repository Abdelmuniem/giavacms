package org.giavacms.chalet.utils;

/**
 * Created by fiorenzo on 03/07/15.
 */
public class MsgUtils
{
   public static String paradeSms(String fullName, String chaletName, String licenseNumber, String position)
   {
      String msg =
               "caro " + fullName + " il tuo chalet " + chaletName + " conc. " + licenseNumber + " e' ora in posizione "
                        + position;
      return msg;
   }

   public static String ticketSms(String fullName, String chaletName, String licenseNumber, String ticketName,
            String expireDate)
   {
      String msg =
               "caro " + fullName + " hai vinto " + ticketName + " presso lo chalet " + chaletName + " conc. "
                        + licenseNumber + " da consumarsi entro il "
                        + expireDate;
      return msg;
   }
}
