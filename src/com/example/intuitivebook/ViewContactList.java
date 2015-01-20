package com.example.intuitivebook;

//http://stackoverflow.com/questions/14238410/how-to-make-a-call-directly-in-android
//used this source http://docs.oracle.com/javase/7/docs/api/java/util/Scanner.html

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
/**
 * An Intuitive addressbook for adding, removing, and texting as well as calling people.
 * Its also possible(In near future) to have group chat as well as group video.
 * It will be possible to leave group chat and get back to group chat.
 * */
public class ViewContactList extends Activity
{
	private ListView listview;
	private TextView nothingToday;
	private Contacts myContacts = new Contacts();
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
		
		refreshContactsFromFile();
		vcontacts = new ContactsAdapter(this,myContacts.getVectorOfContacts()); //connections should store all user
		listview.setAdapter(vcontacts);     //View contacts
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
				Intent deleteContacts = new Intent(getApplicationContext(),ContactsToDelete.class);
				deleteContacts.putExtra("bunchOfContacts", myContacts);
				startActivity(deleteContacts);
			}
		});
	}
	
	/**
	 * Refreshes viewed contacts
	 * */
	@Override
	public void onResume(){
		super.onResume();
		myContacts.emptyContacts();
		refreshContactsFromFile();
		vcontacts.clear();
		vcontacts.addAll(myContacts.getVectorOfContacts());
	}
	
	/**
	 * Reads contact information from file
	 * */
	public void refreshContactsFromFile()
	{
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
				VContact vc = new VContact(contactDetails[0],contactDetails[1],contactDetails[2],contactDetails[3]);
				myContacts.addContact(vc.getName(),vc);
			}
			readContact.close();
		} catch (FileNotFoundException e1) {
			Log.i("Reading Error","Trouble understanding file: " + e1.getMessage());
			e1.printStackTrace();
		}
	}
	
	/**
	 * Retrieves the remaining non deleted contacts from ContactsToDelete activity
	 * */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if(resultCode == RESULT_OK){
				myContacts = (Contacts) data.getSerializableExtra("remainingContacts");
			}
		}
	}
	/**
	 * This class displays contacts and provides the ability to call or text contacts
	 * */
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
			final TextView phone = (TextView) convertView.findViewById(R.id.textView2);
			final TextView cell = (TextView) convertView.findViewById(R.id.textView3);
			TextView email = (TextView) convertView.findViewById(R.id.textView4);
			callThisNumber = "";
			phone.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					callThisNumber = phone.getText().toString();
				}
			});

			cell.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					callThisNumber = cell.getText().toString();
				}
			});

			name.setText(result.getName());
			phone.setText(result.getPhone());
			cell.setText(result.getCell());
			email.setText(result.getEmail());
			ImageButton callButton = (ImageButton)convertView.findViewById(R.id.imageButton1);
			ImageButton messageButton = (ImageButton)convertView.findViewById(R.id.imageButton);
			callButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Uri number = Uri.parse("tel:" + ((callThisNumber.equals(""))?(callThisNumber = result.getDefaultNumber()):callThisNumber));
					Intent callIntent = new Intent(Intent.ACTION_CALL, number);
					startActivity(callIntent);
				}
			});

			messageButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:" + (callThisNumber = result.getDefaultNumber())));
					//getExtras on other side
					startActivity(sendIntent);
				}
			});
			return convertView;
		}
	}
}