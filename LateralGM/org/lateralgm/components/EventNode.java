package org.lateralgm.components;

import javax.swing.tree.DefaultMutableTreeNode;

import org.lateralgm.messages.Messages;

public class EventNode extends DefaultMutableTreeNode
	{
	private static final long serialVersionUID = 1L;

	public EventNode(String text, int mainId)
		{
		this(text,mainId,0);
		}

	public EventNode(String text, int mainId, int eventId)
		{
		setUserObject(text);
		}

	public void add(int mainId, int eventId)
		{
		add(new EventNode(Messages.getString("Event.EVENT" + mainId + "_" + eventId),mainId,eventId));
		}

	public void add(int mainId)
		{
		add(new EventNode(Messages.getString("MainEvent.EVENT" + mainId),mainId));
		}
	}
