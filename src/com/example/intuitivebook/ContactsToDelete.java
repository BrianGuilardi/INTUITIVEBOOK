package com.example.intuitivebook;
import java.util.List;
import java.util.Vector;

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

/*
 * A list of all users currently
 * in list with check box to they left.
 * which ever checkbox(s) are selected,
 * these contacts will be removed from adapter and vector
 * This activity will have a button called select consecutive,
 * after pressing, the user can click one check box and anywhere up or down
 * from that checkbox, the entire range of contacts will be selected for deletion
 * */

public class ContactsToDelete extends Activity
{
	private ListView contactstodelete;
	private Button selectMultiple;
	private DeleteContactsAdapter deletecontacts;
	private Button delete;
	private boolean multiplesClicked = false;
	private int startSelection = -1;
	private int endSelection = -1;
	private int checkRange = 0;
	private Vector<VContact> contacts;
	private Vector<Integer> selectedIndexesToDelete = new Vector<Integer>();
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deletecontacts);
        contacts = ((Contacts)getIntent().getExtras().getSerializable("bunchOfContacts")).getContacts();
        contactstodelete = (ListView)findViewById(R.id.listView1);
        selectMultiple = (Button)findViewById(R.id.button1);
        delete = (Button)findViewById(R.id.button2);
        contacts.addAll(contacts);
        deletecontacts = new DeleteContactsAdapter(this, contacts);
        contactstodelete.setAdapter(deletecontacts);
        
        delete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for(Integer contactIndextoDelete: selectedIndexesToDelete)
				{
					contacts.remove(contacts.get(contactIndextoDelete)); //Delete all selected
				}
				deletecontacts.clear();
				deletecontacts.addAll(contacts);
			}
		});
        selectMultiple.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(multiplesClicked) multiplesClicked = false;
				else multiplesClicked = true;
				
				if(startSelection > -1 && endSelection > -1)
				{
					int range = endSelection - startSelection;
					if(range < 0)
					{
						checkRange = range * (-1);
						for(int i = endSelection + 1; i < startSelection; i++)
						{
							selectedIndexesToDelete.add(i);
						}
						contactstodelete.invalidateViews();
					}
					else
					{
						checkRange = range;
						for(int i = startSelection + 1; i < endSelection; i++)
						{
							selectedIndexesToDelete.add(i);
						}
						contactstodelete.invalidateViews();
					}
				}
			}
		});
    }
	
	private class DeleteContactsAdapter extends ArrayAdapter<VContact>
	{
		private static final int layout_resource = R.layout.deletecontact;
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
			
			//The code below checks every box in consequtive range
			if(multiplesClicked && checkRange > 0 && selectedIndexesToDelete.contains(position))
			{
				deleteSelected.setChecked(true);
				checkRange--;
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
								startSelection = position;
							if(endSelection == -1)
							{
								endSelection = position;
								multiplesClicked = false;
							}	
						}
					}
				}
			});
			deleteSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if(!isChecked){
						selectedIndexesToDelete.remove(position); //Removed the index to delete
					}
				}
			});
			return convertView;
		}
	}
}
