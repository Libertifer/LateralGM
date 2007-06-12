/*
 * Copyright (C) 2007 Clam <ebordin@aapt.net.au>
 * 
 * This file is part of Lateral GM.
 * Lateral GM is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.subframes;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameEvent;

import org.lateralgm.components.NameDocument;
import org.lateralgm.components.ResNode;
import org.lateralgm.main.LGM;
import org.lateralgm.messages.Messages;
import org.lateralgm.resources.Resource;

// Provides common functionality and structure to Resource editing frames
public abstract class ResourceFrame<R extends Resource> extends JInternalFrame implements
		DocumentListener,ActionListener
	{
	private static final long serialVersionUID = 1L;
	/*
	 * The Resource's name - setup automatically to update the title of the frame and
	 * the ResNode's text
	 */
	public JTextField name;
	public JButton save; // automatically set up to save and close the frame
	public R res; // the resource this frame is editing
	public R resOriginal; // backup of res as it was before changes were made
	public String titlePrefix = ""; //$NON-NLS-1$
	public String titleSuffix = ""; //$NON-NLS-1$
	public ResNode node; // node this frame is linked to
	private static Container lastContainer;

	@SuppressWarnings("unchecked")
	public ResourceFrame(R res, ResNode node)
		{
		super("",true,true,true,true); //$NON-NLS-1$
		this.res = res;
		this.node = node;
		resOriginal = (R) res.copy(false,null);
		setTitle(res.getName());
		setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		name = new JTextField();
		name.setDocument(new NameDocument());
		name.setText(res.getName());
		name.getDocument().addDocumentListener(this);
		name.setCaretPosition(0);
		save = new JButton();
		save.addActionListener(this);
		}

	public abstract void updateResource();

	public abstract void revertResource();

	public abstract boolean resourceChanged();

	public void addGap(int w, int h)
		{
		addGap(this,w,h);
		}

	public static void addGap(Container c, int w, int h)
		{
		JLabel l = new JLabel();
		l.setPreferredSize(new Dimension(w,h));
		c.add(l);
		}

	public static Component addDim(Container container, Component comp, int width, int height)
		{
		lastContainer = container;
		comp.setPreferredSize(new Dimension(width,height));
		return container.add(comp);
		}

	public static Component addDim(Component comp, int width, int height)
		{
		return addDim(lastContainer,comp,width,height);
		}

	public void changedUpdate(DocumentEvent e)
		{
		// Not used
		}

	public void insertUpdate(DocumentEvent e)
		{
		if (e.getDocument() == name.getDocument())
			{
			res.setName(name.getText());
			setTitle(name.getText());
			node.setUserObject(name.getText());
			LGM.tree.updateUI();
			}
		}

	public void removeUpdate(DocumentEvent e)
		{
		if (e.getDocument() == name.getDocument())
			{
			res.setName(name.getText());
			setTitle(name.getText());
			node.setUserObject(name.getText());
			LGM.tree.updateUI();
			}
		}

	public void actionPerformed(ActionEvent e)
		{
		if (e.getSource() == save)
			{
			updateResource();
			dispose();
			}
		}

	public void setTitle(String title)
		{
		super.setTitle(titlePrefix + title + titleSuffix);
		}

	public void dispose()
		{
		super.dispose();
		node.frame = null; // allows a new frame to open
		}

	protected void fireInternalFrameEvent(int id)
		{
		if (id == InternalFrameEvent.INTERNAL_FRAME_CLOSING)
			{
			if (resourceChanged())
				{
				int ret = JOptionPane.showConfirmDialog(LGM.frame,String.format(
						Messages.getString("ResourceFrame.KEEPCHANGES"),res.getName()),Messages //$NON-NLS-1$
				.getString("ResourceFrame.KEEPCHANGES_TITLE"), //$NON-NLS-1$
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (ret == JOptionPane.YES_OPTION)
					{
					updateResource();
					node.setUserObject(res.getName());
					dispose();
					LGM.tree.updateUI();
					}
				else if (ret == JOptionPane.NO_OPTION)
					{
					revertResource();
					node.setUserObject(resOriginal.getName());
					dispose();
					LGM.tree.updateUI();
					}
				}
			else
				{
				updateResource();
				dispose();
				}
			}
		super.fireInternalFrameEvent(id);
		}
	}
