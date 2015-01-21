package com.example.intuitivebook;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

public class GroupSMS extends Activity {
	private ListView contactstomessage;
	private Button selectMultiple;
	private MessageContactsAdapter messagecontacts;
	private Button message;
	private Button createGroup;
	private Contacts contacts;

	private TreeSet<Integer> selectedIndexesToMessage = new TreeSet<Integer>(new Comparator<Integer> (){
		@Override
		public int compare(Integer n1, Integer n2) {
			if(n1.intValue() > n2.intValue()){
				return 1;
			} else if(n1.intValue() == n2.intValue())
				return 0;
			else {
				return -1;
			}
		}
	});
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groupsms);

		contacts = ((Contacts)getIntent().getExtras().getSerializable("bunchOfContacts"));
		contactstomessage = (ListView)findViewById(R.id.listView1);
		selectMultiple = (Button)findViewById(R.id.button1);
		message = (Button)findViewById(R.id.button2);
		createGroup = (Button)findViewById(R.id.button3);
		messagecontacts = new MessageContactsAdapter(this,contacts.getVectorOfContacts());
		contactstomessage.setAdapter(messagecontacts);
		message.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String phones = "";
				for(Integer m: selectedIndexesToMessage)
				{
					phones += messagecontacts.getItem(m).getDefaultNumber() + ",";
				}
				phones = phones.substring(0, phones.length() - 1);
				//Sending group sms by sending phones to messaging client
				
				//The loop below can be used to sms the same group indivudually, its commented for now
				/*for(int i = selectedIndexesToMessage.size() - 1; i > -1; i--) 
				{
					
				}*/
				//Might need to bring back writing to file, because a contact can be a group
				finish();
			}
		});
		createGroup.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Make a group from all selectedIndexesToMessage
				//pass them to sms intent with comma delimited
			}
		});
		selectMultiple.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!messagecontacts.multiplesClicked)
					messagecontacts.multiplesClicked = true;
				else { messagecontacts.multiplesClicked = false; }
			}
		});
	}

	private class MessageContactsAdapter extends ArrayAdapter<VContact>
	{
		private static final int layout_resource = R.layout.deletecontact;
		public boolean multiplesClicked = false;
		private int startSelection = -1;
		private int endSelection = -1;
		public MessageContactsAdapter(Context context, List<VContact> objects) {
			super(context, layout_resource, objects);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
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
			CheckBox messageSelected = (CheckBox)convertView.findViewById(R.id.checkBox1);

			messageSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if(!isChecked && selectedIndexesToMessage.contains(Integer.valueOf(position))){
						selectedIndexesToMessage.remove(Integer.valueOf(position)); //Removed the index to delete
					}
				}
			});

			if(messageSelected.isChecked() && !selectedIndexesToMessage.contains(Integer.valueOf(position)))
			{
				messageSelected.setChecked(false);
			}
			else if(!messageSelected.isChecked() && selectedIndexesToMessage.contains(Integer.valueOf(position)))
			{
				messageSelected.setChecked(true);
			}

			name.setText(result.getName());
			phone.setText(result.getPhone());
			cell.setText(result.getCell());
			email.setText(result.getEmail());

			messageSelected.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(((CheckBox) v).isChecked())
					{
						selectedIndexesToMessage.add(position);
						if(multiplesClicked){
							if(startSelection == -1)
							{
								startSelection = position;
								return;
							}
							if(endSelection == -1)
							{
								endSelection = position;
								if(startSelection > -1 && endSelection > -1)
								{
									int range = endSelection - startSelection + 1;
									if(range < 0)
									{
										for(int i = endSelection + 1; i < startSelection; i++)
										{
											selectedIndexesToMessage.add(i);
											CheckBox c = (CheckBox) contactstomessage.getChildAt(i).findViewById(R.id.checkBox1);
											c.setChecked(true);
										}
									}
									else
									{
										for(int i = startSelection + 1; i < endSelection; i++)
										{
											selectedIndexesToMessage.add(i);
											CheckBox c = (CheckBox) contactstomessage.getChildAt(i).findViewById(R.id.checkBox1);
											c.setChecked(true);
										}
									}
								}
							}
							startSelection = -1;
							endSelection = -1;
							multiplesClicked = false;
							return;
						}
						else
						{
							startSelection = -1;
							endSelection = -1;
						}
					}
				}
			});
			return convertView;
		}
	}
}
