package org.giavacms.paypalweb.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BillingAddress implements Serializable
{
   private static final long serialVersionUID = 1L;

   private Long id;
   private String firstName;
   private String lastName;
   private boolean company;
   private String vatCode;
   private String line1;
   private String line2;
   private String city;
   private String countryCode;
   private String zip;
   private String state;
   private String phone;
   private String email;

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   public String getLine1()
   {
      return line1;
   }

   public void setLine1(String line1)
   {
      this.line1 = line1;
   }

   public String getLine2()
   {
      return line2;
   }

   public void setLine2(String line2)
   {
      this.line2 = line2;
   }

   public String getCity()
   {
      return city;
   }

   public void setCity(String city)
   {
      this.city = city;
   }

   public String getCountryCode()
   {
      return countryCode;
   }

   public void setCountryCode(String countryCode)
   {
      this.countryCode = countryCode;
   }

   public String getState()
   {
      return state;
   }

   public void setState(String state)
   {
      this.state = state;
   }

   public String getPhone()
   {
      return phone;
   }

   public void setPhone(String phone)
   {
      this.phone = phone;
   }

   public String getFirstName()
   {
      return firstName;
   }

   public void setFirstName(String firstName)
   {
      this.firstName = firstName;
   }

   public String getLastName()
   {
      return lastName;
   }

   public void setLastName(String lastName)
   {
      this.lastName = lastName;
   }

   public boolean isCompany()
   {
      return company;
   }

   public void setCompany(boolean company)
   {
      this.company = company;
   }

   public String getVatCode()
   {
      return vatCode;
   }

   public void setVatCode(String vatCode)
   {
      this.vatCode = vatCode;
   }

   public String getZip()
   {
      return zip;
   }

   public void setZip(String zip)
   {
      this.zip = zip;
   }

   public String getEmail()
   {
      return email;
   }

   public void setEmail(String email)
   {
      this.email = email;
   }

   @Override
   public String toString()
   {
      return "BillingAddress [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", company="
               + company + ", vatCode=" + vatCode + ", line1=" + line1 + ", line2=" + line2 + ", city=" + city
               + ", countryCode=" + countryCode + ", zip=" + zip + ", state=" + state + ", phone=" + phone + ", email="
               + email + "]";
   }

}
