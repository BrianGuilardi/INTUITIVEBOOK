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
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
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
	private FileOutputStream writeContacts;
	private OutputStreamWriter writeContactDetails;
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

				try {
					writeContacts = openFileOutput("intuitiveContacts.txt", MODE_APPEND);
				} catch (FileNotFoundException e1) {
					Log.i("Error Opening File","There was a problem opning the file : " + e1.getMessage());
					e1.printStackTrace();
				}
				writeContactDetails = new OutputStreamWriter(writeContacts);
				String saveContact = firstName.getText().toString() + " " +
						lastName.getText().toString() + "|" +
						parsePhone(phoneNumber.getText().toString()) + "|" +
						parseCell(cellNumber.getText().toString()) + "|" +
						parseEmail(Email.getText().toString()) + ">";
				try
				{
					writeContactDetails.write(saveContact); //Append current contact to end of file
					writeContactDetails.close(); //close writer after writing into file
					Toast.makeText(getBaseContext(), firstName.getText().toString() + " " +
							lastName.getText().toString() + " saved successfully!",Toast.LENGTH_SHORT).show();	
				} catch (IOException e) {
					Log.i("Error Writing File","There was a problem writing the file : " + e.getMessage());
					e.getStackTrace(); //Write file errors to a file whose location you know
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
			return phoneN;
		}
		else if(phoneN.length() == 10) //We have areaCode
		{
			phoneN = phoneN.substring(0, 10); //get phone number
			return phoneN;
		}
		else if(phoneN.length() == 11)
		{
			//Getting rid of 1
			phoneN = phoneN.substring(1,phoneN.length()).substring(0, phoneN.length()); //get phone number
			return phoneN;
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
			return phoneN;
		}
		else if(phoneN.length() == 10) //We have areaCode
		{
			phoneN = phoneN.substring(0, 10); //get phone number
			return phoneN;
		}
		else if(phoneN.length() == 11)
		{
			phoneN = phoneN.substring(1,phoneN.length()).substring(0, phoneN.length()); //get phone number
			return phoneN;
		}
		return "invalidFormat";
	}

	public String parseEmail(String email)
	{
		if(email.contains("@") && email.contains(".") && firstAtSymbolIndex == secondAtSymbolIndex) {
			return email.substring(0, email.indexOf("@") + 1) + email.substring(email.indexOf("@") + 1, email.indexOf(".") + 1) +
					email.substring(email.indexOf(".") + 1, email.length());
		}
		return "EmailCanUseOnly[1 @ symbol]";
	}
}