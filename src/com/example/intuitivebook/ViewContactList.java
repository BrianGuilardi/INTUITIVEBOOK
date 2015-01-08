package com.example.intuitivebook;

//http://stackoverflow.com/questions/14238410/how-to-make-a-call-directly-in-android
//used this source http://docs.oracle.com/javase/7/docs/api/java/util/Scanner.html
//Scanner s = new Scanner(input).useDelimiter("\\s*fish\\s*");

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.*;
import java.util.Scanner;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;
import java.util.Vector;

public class ViewContactList extends Activity
{
	private ListView listview;
	private TextView nothingToday;
	private Vector<VContact> connections = new Vector<VContact>();
	private FileOutputStream checkContacts;
	private FileInputStream savecontacts;
	private Scanner readContact;
	private ContactsAdapter vcontacts;
	private String callThisNumber = ""; //Call default number or stays empty
	private Button addUser = null;
	private Button removeUser = null;
	private Button emergency = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contactlist);
		listview = (ListView)findViewById(R.id.listView1);
		nothingToday = (TextView)findViewById(R.id.emptyList);
		addUser = (Button) findViewById(R.id.button1);
		removeUser = (Button) findViewById(R.id.button2);
		emergency = (Button) findViewById(R.id.button3);
		
		try {
			checkContacts = openFileOutput("intuitiveContacts.txt", MODE_APPEND);
			try {
				checkContacts.close();
			} catch (IOException e) {
				Log.i("Error Closing File","There was a problem closing the file : " + e.getMessage());
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			Log.i("Error Creating File","There was a problem creating the file : " + e1.getMessage());
			e1.printStackTrace();
		}
		try { //read user information from file intuitiveContacts.txt
			savecontacts = openFileInput("intuitiveContacts.txt");
			readContact = new Scanner(savecontacts).useDelimiter(">");
			while(readContact.hasNext()) //Every parsed data is added as a VContact to connections
			{
				String[] contactDetails = readContact.next().split("\\|");
				connections.add(new VContact(contactDetails[0],contactDetails[1],contactDetails[2],contactDetails[3]));
			}
			readContact.close();
		} catch (FileNotFoundException e1) {
			Log.i("Reading Error","Trouble understanding file: " + e1.getMessage());
			e1.printStackTrace();
		}

		emergency.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {	//This is to Dial 911
				Uri number = Uri.parse("tel:911");
				Intent callEmergency = new Intent(Intent.ACTION_DIAL, number);
				startActivity(callEmergency);
			}
		});
		addUser.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) { //Non 911 call
				Intent makeContact = new Intent(ViewContactList.this, Contact.class);
				startActivity(makeContact);
			}
		});
		removeUser.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ContactsToDelete deletecontacts = new ContactsToDelete(connections);
				Intent deleteContacts = new Intent(getApplicationContext(),deletecontacts.getClass());
				startActivity(deleteContacts);
			}
		});

		vcontacts = new ContactsAdapter(this,connections); //connections should store all user
		listview.setAdapter(vcontacts);     //View contacts
	}

	private class ContactsAdapter extends ArrayAdapter<VContact>
	{
		private static final int layout_resource = R.layout.contact;
		public ContactsAdapter(Context context, List<VContact> objects) {
			super(context, layout_resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(layout_resource, null);
			}
			final VContact result = getItem(position);
			TextView name = (TextView) convertView.findViewById(R.id.textView1);
			TextView phone = (TextView) convertView.findViewById(R.id.textView2);
			TextView cell = (TextView) convertView.findViewById(R.id.textView3);
			TextView email = (TextView) convertView.findViewById(R.id.textView4);

			name.setText(result.getName());
			phone.setText(result.getPhone());
			cell.setText(result.getCell());
			email.setText(result.getEmail());
			ImageButton callButton = (ImageButton)convertView.findViewById(R.id.imageButton1);
			callButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Uri number = Uri.parse("tel:" + (callThisNumber = result.getDefaultNumber()));
					Intent callIntent = new Intent(Intent.ACTION_CALL, number);
					startActivity(callIntent);
				}
			});
			return convertView;
		}
	}
}