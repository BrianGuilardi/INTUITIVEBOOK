package com.example.intuitivebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Contact extends Activity
{
	private EditText firstName;	//EditText field for storing user input
	private EditText lastName;
	private EditText phoneNumber;
	private EditText cellNumber;
	private EditText Email;
	private Button submit;
	private Button goback;
	private File savecontacts;
	private BufferedWriter writer;
	private int firstAtSymbolIndex = -1;
	private int secondAtSymbolIndex = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contactform);

		firstName = (EditText)findViewById(R.id.editText1);
		lastName = (EditText)findViewById(R.id.editText2);
		phoneNumber = (EditText)findViewById(R.id.editText4);
		cellNumber = (EditText)findViewById(R.id.editText5);
		Email = (EditText)findViewById(R.id.editText3);
		submit = (Button)findViewById(R.id.button3);
		goback = (Button)findViewById(R.id.button4);

		//These hint the user what each field stands for
		firstName.setHint("First Name");
		lastName.setHint("Last Name");
		phoneNumber.setHint("Phone");
		cellNumber.setHint("Cell");
		Email.setHint("Email");
		
		Email.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				firstAtSymbolIndex = s.toString().indexOf("@");
			}
			@Override
			public void afterTextChanged(Editable s) {
				secondAtSymbolIndex = s.toString().indexOf("@", s.toString().indexOf("@"));
			}
		});
		
		submit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				savecontacts = new File(getApplicationContext().getDir(STORAGE_SERVICE,MODE_APPEND),"intuitiveContacts.txt");

				if(!savecontacts.exists())
				{
					try {
						savecontacts = File.createTempFile("intuitiveContacts", ".txt", getApplicationContext().getFilesDir());
					} catch (IOException e) {
						Log.i("Error Creating File","There was a problem creating the file : " + e.getMessage());
						e.printStackTrace();
					}
				}
				if(savecontacts.canWrite())
				{
					String saveContact = firstName.getText().toString() + " " +
							lastName.getText().toString() + "|" +
							parsePhone(phoneNumber.getText().toString()) + "|" +
							parseCell(cellNumber.getText().toString()) + "|" +
							parseEmail(Email.getText().toString()) + ">";
					try
					{
						writer = new BufferedWriter(new FileWriter(savecontacts, true));
						writer.append(saveContact); //Append current contact to end of file
						writer.close(); //close writer after writing into file
					} catch (IOException e) {
						Log.i("Error Writing File","There was a problem writing the file : " + e.getMessage());
						e.getStackTrace(); //Write file errors to a file whose location you know
					}
				}
				Intent back = new Intent(Contact.this, ViewContactList.class); //Go back to original screen
				startActivity(back);
			}
		});

		goback.setOnClickListener(new View.OnClickListener() { //Brings user back to previous screen

			@Override
			public void onClick(View v) {
				Intent back = new Intent(Contact.this, ViewContactList.class);
				startActivity(back);
			}
		});
	}

	public String parsePhone(String phoneN)
	{
		//Gets rid of dashes,(s,)s,.s(if any)
		phoneN = phoneN.replace("-", "").replace("(", "").replace(")","").replace(".", "");
		if(phoneN.length() == 7)
		{
			phoneN = phoneN.substring(0, 7);
		}
		else if(phoneN.length() == 10) //We have areaCode
		{
			phoneN = phoneN.substring(0, 10); //get phone number
		}
		else if(phoneN.length() == 11)
		{
			//Getting rid of 1
			phoneN = phoneN.substring(1,phoneN.length()).substring(0, phoneN.length()); //get phone number
		}
		return "invalidFormat";
	}

	public String parseCell(String phoneN)
	{
		//Gets rid of dashes,(s,)s,.s(if any)
		phoneN = phoneN.replace("-", "").replace("(", "").replace(")","").replace(".", "");
		if(phoneN.length() == 7)
		{
			phoneN = phoneN.substring(0, 7);
		}
		else if(phoneN.length() == 10) //We have areaCode
		{
			phoneN = phoneN.substring(0, 10); //get phone number
		}
		else if(phoneN.length() == 11)
		{
			phoneN = phoneN.substring(1,phoneN.length()).substring(0, phoneN.length()); //get phone number
		}
		return "invalidFormat";
	}

	public String parseEmail(String email)
	{
		if(email.contains("@") && email.contains(".") && firstAtSymbolIndex == secondAtSymbolIndex) {
			return email.substring(0, email.indexOf("@")) + email.substring(email.indexOf("@") + 1, email.indexOf(".")) +
					email.substring(email.indexOf(".") + 1, email.length());
		}
		return "EmailCanUseOnly[1 @ symbol]";
	}
}