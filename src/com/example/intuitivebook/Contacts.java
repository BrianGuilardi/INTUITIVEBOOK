package com.example.intuitivebook;

import java.io.Serializable;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Vector;

class Contacts implements Serializable
{
	private static final long serialVersionUID = 1L;
	private class CompareContacts implements Comparator<String>, Serializable {
		private static final long serialVersionUID = 1L;

		public int compare(String n1, String n2) {
			return n1.compareToIgnoreCase(n2);
		}
	};
	
	private TreeMap<String, VContact> connections = new TreeMap<String,VContact>(new CompareContacts());
	
	public void addContact(String name , VContact contact)
	{
		connections.put(name,contact);
	}
	public void addContacts(TreeMap<String, VContact> connections)
	{
		for(String c: connections.keySet())
		{
			VContact vc = connections.get(c);
			this.connections.put(vc.getName(),vc);
		}
	}
	public void removeContact(VContact removeContact)
	{
		connections.remove(removeContact.getName());
	}
	public void emptyContacts(){
		connections.clear();
	}
	public void addContacts(Vector<VContact> connections)
	{
		for(VContact vc: connections)
		{
			this.connections.put(vc.getName(),vc);
		}
	}
	public TreeMap<String, VContact> getContacts(){
		return connections;
	}
	public Vector<VContact> getVectorOfContacts()
	{
		Vector<VContact> temp = new Vector<VContact>();
		for(String c: connections.keySet())
		{
			temp.add(connections.get(c));
		}
		return temp;
	}
}