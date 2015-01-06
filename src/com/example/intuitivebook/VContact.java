package com.example.intuitivebook;

/**
 * Created by dmitridimov on 12/18/14.
 */

public class VContact
{
    private String name;
    private String phone;
    private String cell;
    private String email;
    private String defaultNumber = "";
    public VContact(String newName, String newPhone, String newCell, String newEmail)
    {
        name = newName;
        phone = newPhone;
        cell = newCell;
        email = newEmail;

        if(!cell.equals("")){
            defaultNumber = cell;
        }
        else if(cell.equals(""))
        {
            defaultNumber = phone;
        }
        else
        {
            defaultNumber = "";
        }
    }
    public String getName(){
        return name;
    }
    public String getPhone(){
        return phone;
    }
    public String getCell(){
        return cell;
    }
    public String getEmail(){
        return email;
    }
    public String getDefaultNumber(){
        return defaultNumber;
    }
}
