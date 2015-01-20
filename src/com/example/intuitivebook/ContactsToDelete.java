package com.example.intuitivebook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A list of all users currently in list with check box to they left.
 * Which ever Checkbox(s) are selected, these contacts will be removed from adapter and vector.
 * This activity will have a button called select(ions),
 * After pressing select(ions), the user can click one check box and anywhere up or down
 * From that Checkbox, the entire range of contacts will be selected for deletion
 * */

public class ContactsToDelete extends Activity
{
	private ListView contactstodelete;
	private Button selectMultiple;
	private DeleteContactsAdapter deletecontacts;
	private Button delete;
	private Contacts contacts;
	private FileOutputStream writeContacts;
	private OutputStreamWriter writeContactDetails;
	private boolean isDone = false;

	private TreeSet<Integer> selectedIndexesToDelete = new TreeSet<Integer>(new Comparator<Integer> (){
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
		setContentView(R.layout.deletecontacts);

		contacts = ((Contacts)getIntent().getExtras().getSerializable("bunchOfContacts"));
		contactstodelete = (ListView)findViewById(R.id.listView1);
		selectMultiple = (Button)findViewById(R.id.button1);
		delete = (Button)findViewById(R.id.button2);
		deletecontacts = new DeleteContactsAdapter(this,contacts.getVectorOfContacts());
		contactstodelete.setAdapter(deletecontacts);
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Object[] contactIndexes = selectedIndexesToDelete.toArray();
				for(int i = selectedIndexesToDelete.size() - 1; i > -1; i--) 
				{
					int currentIndexToDelete = ((Integer)contactIndexes[i]).intValue();
					contacts.removeContact(deletecontacts.getItem(currentIndexToDelete));
					deletecontacts.remove(deletecontacts.getItem(currentIndexToDelete)); //Delete all selected
					selectedIndexesToDelete.remove(currentIndexToDelete);
				}

				try {
					writeContacts = openFileOutput("intuitiveContacts.txt", MODE_PRIVATE);
				} catch (FileNotFoundException e1) {
					Log.i("Error Opening File","There was a problem opening the file : " + e1.getMessage());
					e1.printStackTrace();
				}
				writeContactDetails = new OutputStreamWriter(writeContacts);
				for(String n : contacts.getContacts().keySet())
				{
					VContact c = contacts.getContacts().get(n);
					String saveContact = c.getName() + "|" +
							c.getPhone() + "|" + c.getCell() + "|" + c.getEmail() + ">";
					try
					{
						writeContactDetails.write(saveContact); //Append current contact to end of file
					} catch (IOException e) {
						Log.i("Error Writing File","There was a problem writing to file : " + e.getMessage());
						e.getStackTrace();
					}
				}
				try {
					writeContactDetails.close();
				} catch (IOException e) {
					Log.i("Error Closing File","There was a problem closing the file : " + e.getMessage());
					e.printStackTrace();
				} //close writer after writing into file
				//Pass data back to previous activity
				Intent intent = new Intent(getApplicationContext(),ViewContactList.class);
				Contacts cs = new Contacts();
				cs.addContacts(contacts.getContacts());
				intent.putExtra("remainingContacts",cs);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		selectMultiple.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!deletecontacts.multiplesClicked)
					deletecontacts.multiplesClicked = true;
				else { deletecontacts.multiplesClicked = false; }
			}
		});
	}

	private class DeleteContactsAdapter extends ArrayAdapter<VContact>
	{
		private static final int layout_resource = R.layout.deletecontact;
		public boolean multiplesClicked = false;
		private int startSelection = -1;
		private int endSelection = -1;
		public DeleteContactsAdapter(Context context, List<VContact> objects) {
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
			CheckBox deleteSelected = (CheckBox)convertView.findViewById(R.id.checkBox1);

			deleteSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if(!isChecked && selectedIndexesToDelete.contains(Integer.valueOf(position))){
						selectedIndexesToDelete.remove(Integer.valueOf(position)); //Removed the index to delete
					}
				}
			});

			if(deleteSelected.isChecked() && !selectedIndexesToDelete.contains(Integer.valueOf(position)))
			{
				deleteSelected.setChecked(false);
			}
			else if(!deleteSelected.isChecked() && selectedIndexesToDelete.contains(Integer.valueOf(position)))
			{
				deleteSelected.setChecked(true);
			}

			name.setText(result.getName());
			phone.setText(result.getPhone());
			cell.setText(result.getCell());
			email.setText(result.getEmail());

			deleteSelected.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(((CheckBox) v).isChecked())
					{
						selectedIndexesToDelete.add(position);
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
											selectedIndexesToDelete.add(i);
											CheckBox c = (CheckBox) contactstodelete.getChildAt(i).findViewById(R.id.checkBox1);
											c.setChecked(true);
										}
									}
									else
									{
										for(int i = startSelection + 1; i < endSelection; i++)
										{
											selectedIndexesToDelete.add(i);
											CheckBox c = (CheckBox) contactstodelete.getChildAt(i).findViewById(R.id.checkBox1);
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
