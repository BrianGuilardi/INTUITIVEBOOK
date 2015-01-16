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
	public Vector<VContact> getVectorOfContacts(){
		Vector<VContact> vcontacts = new Vector<VContact>();
		for(String key: connections.keySet())
		{
			vcontacts.add(connections.get(key));
		}
		return vcontacts;
	}
}