package com.example.intuitivebook;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
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
	private Vector<VContact> contacts;
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
        contacts = ((Contacts)getIntent().getExtras().getSerializable("bunchOfContacts")).getContacts();
        contactstodelete = (ListView)findViewById(R.id.listView1);
        selectMultiple = (Button)findViewById(R.id.button1);
        delete = (Button)findViewById(R.id.button2);
        deletecontacts = new DeleteContactsAdapter(this, contacts);
        contactstodelete.setAdapter(deletecontacts);
        
        delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Object[] contactIndexes = selectedIndexesToDelete.toArray();
				for(int i = selectedIndexesToDelete.size() - 1; i > -1; i--) 
				{
					int currentIndexToDelete = ((Integer)contactIndexes[i]).intValue();
					deletecontacts.remove(deletecontacts.getItem(currentIndexToDelete)); //Delete all selected
					selectedIndexesToDelete.remove(currentIndexToDelete);
				}
				for(int i = 0; i < deletecontacts.getCount(); i++)
				{
					((CheckBox)contactstodelete.getChildAt(i).findViewById(R.id.checkBox1)).setChecked(false);
				}
				deletecontacts.multiplesClicked = true;
			}
		});
        selectMultiple.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!deletecontacts.multiplesClicked)
					deletecontacts.multiplesClicked = true;
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
			
			name.setText(result.getName());
			phone.setText(result.getPhone());
			cell.setText(result.getCell());
			email.setText(result.getEmail());
			deleteSelected.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(((CheckBox) v).isChecked())
					{
						System.out.println("multiplesCLicked 2 = " + multiplesClicked);
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
									int range = endSelection - startSelection;
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
								return;
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
						selectedIndexesToDelete.remove(Integer.valueOf(position)); //Removed the index to delete
					}
				}
			});
			return convertView;
		}
	}
}
