package org.giavacms.contest.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by fiorenzo on 17/07/15.
 */
@Entity
@Table(name = Account.TABLE_NAME)
public class Account implements Serializable
{

   private static final long serialVersionUID = -4581521841453347801L;
   public static final String TABLE_NAME = "Account";

   private String phone;
   private String name;
   private String surname;
   private String tocall;
   private Date created;
   private Date confirmed;
   private String userRoles;
   private String uuid;

   public Account()
   {
   }

   public Account(String name, String phone, String surname)
   {
      this.name = name;
      this.phone = phone;
      this.surname = surname;
   }

   @Id
   public String getPhone()
   {
      return phone;
   }

   public void setPhone(String phone)
   {
      this.phone = phone;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getSurname()
   {
      return surname;
   }

   public void setSurname(String surname)
   {
      this.surname = surname;
   }

   public Date getCreated()
   {
      return created;
   }

   public void setCreated(Date created)
   {
      this.created = created;
   }

   public Date getConfirmed()
   {
      return confirmed;
   }

   public void setConfirmed(Date confirmed)
   {
      this.confirmed = confirmed;
   }

   public String getUserRoles()
   {
      return userRoles;
   }

   public void setUserRoles(String userRoles)
   {
      this.userRoles = userRoles;
   }

   public String getTocall()
   {
      return tocall;
   }

   public void setTocall(String tocall)
   {
      this.tocall = tocall;
   }

   public String getUuid()
   {
      return uuid;
   }

   public void setUuid(String uuid)
   {
      this.uuid = uuid;
   }

   @Override public String toString()
   {
      return "Account{" +
               "confirmed=" + confirmed +
               ", phone='" + phone + '\'' +
               ", name='" + name + '\'' +
               ", surname='" + surname + '\'' +
               ", tocall='" + tocall + '\'' +
               ", created=" + created +
               ", userRoles='" + userRoles + '\'' +
               '}';
   }
}
