/*
 * Copyright (C) 2007 IsmAvatar <cmagicj@nni.com>
 * 
 * This file is part of Lateral GM.
 * Lateral GM is free software and comes with ABSOLUTELY NO WARRANTY.
 * See LICENSE for details.
 */

package org.lateralgm.subframes;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.lateralgm.compare.ResourceComparator;
import org.lateralgm.components.IntegerField;
import org.lateralgm.components.impl.ResNode;
import org.lateralgm.main.LGM;
import org.lateralgm.messages.Messages;
import org.lateralgm.resources.Path;
import org.lateralgm.resources.sub.Point;

public class PathFrame extends ResourceFrame<Path> implements ActionListener,ListSelectionListener,
		DocumentListener
	{
	private static final long serialVersionUID = 1L;

	//prevents alternating recursive calls between list selection changes and field changes
	public static boolean manualUpdate = true;
	public JList list;
	public IntegerField tx, ty, tsp, tpr;
	public JButton add, insert, delete;
	public JCheckBox smooth, closed;

	public PathFrame(Path res, ResNode node)
		{
		super(res,node);

		setSize(560,400);
		setMinimumSize(new Dimension(560,400));
		setLayout(new BorderLayout());

		add(makeToolBar(),BorderLayout.NORTH);
		add(makeSide(res),BorderLayout.WEST);
		add(makePreview(),BorderLayout.CENTER);

		list.setSelectedIndex(0);
		}

	//TODO: add more buttons
	private JToolBar makeToolBar()
		{
		JToolBar tool = new JToolBar();
		tool.setFloatable(false);
		tool.add(save);
		tool.addSeparator();
		return tool;
		}

	private JPanel makeSide(Path res)
		{
		JPanel side1 = new JPanel(new FlowLayout());
		side1.setMinimumSize(new Dimension(180,350));
		side1.setMaximumSize(new Dimension(180,350));
		side1.setPreferredSize(new Dimension(180,350));

		JLabel lab = new JLabel(Messages.getString("PathFrame.NAME")); //$NON-NLS-1$
		lab.setPreferredSize(new Dimension(40,14));
		side1.add(lab);
		name.setPreferredSize(new Dimension(120,20));
		side1.add(name);

		list = new JList(res.points.toArray());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		list.setFont(new Font("Monospaced",Font.PLAIN,10)); //$NON-NLS-1$
		JScrollPane p = new JScrollPane(list);
		p.setPreferredSize(new Dimension(160,180));
		side1.add(p);

		lab = new JLabel(Messages.getString("PathFrame.X")); //$NON-NLS-1$
		lab.setPreferredSize(new Dimension(20,14));
		side1.add(lab);
		tx = new IntegerField(Integer.MIN_VALUE,Integer.MAX_VALUE,0);
		tx.setPreferredSize(new Dimension(60,16));
		tx.getDocument().addDocumentListener(this);
		side1.add(tx);
		add = new JButton(Messages.getString("PathFrame.ADD")); //$NON-NLS-1$
		add.setPreferredSize(new Dimension(70,16));
		add.addActionListener(this);
		side1.add(add);

		lab = new JLabel(Messages.getString("PathFrame.Y")); //$NON-NLS-1$
		lab.setPreferredSize(new Dimension(20,14));
		side1.add(lab);
		ty = new IntegerField(Integer.MIN_VALUE,Integer.MAX_VALUE,0);
		ty.setPreferredSize(new Dimension(60,16));
		ty.getDocument().addDocumentListener(this);
		side1.add(ty);
		insert = new JButton(Messages.getString("PathFrame.INSERT")); //$NON-NLS-1$
		insert.setPreferredSize(new Dimension(70,16));
		insert.addActionListener(this);
		side1.add(insert);

		lab = new JLabel(Messages.getString("PathFrame.SP")); //$NON-NLS-1$
		lab.setPreferredSize(new Dimension(20,14));
		side1.add(lab);
		tsp = new IntegerField(0,1000000,100);
		tsp.setPreferredSize(new Dimension(60,16));
		tsp.getDocument().addDocumentListener(this);
		side1.add(tsp);
		delete = new JButton(Messages.getString("PathFrame.DELETE")); //$NON-NLS-1$
		delete.setPreferredSize(new Dimension(70,16));
		delete.addActionListener(this);
		side1.add(delete);

		smooth = new JCheckBox(Messages.getString("PathFrame.SMOOTH"),res.smooth); //$NON-NLS-1$
		side1.add(smooth);
		closed = new JCheckBox(Messages.getString("PathFrame.CLOSED"),res.closed); //$NON-NLS-1$
		side1.add(closed);

		lab = new JLabel(Messages.getString("PathFrame.PRECISION")); //$NON-NLS-1$
		lab.setPreferredSize(new Dimension(60,14));
		side1.add(lab);
		tpr = new IntegerField(1,8,res.precision);
		tpr.setPreferredSize(new Dimension(40,16));
		side1.add(tpr);

		return side1;
		}

	//TODO: 1.7
	private JComponent makePreview()
		{
		JPanel pane = new JPanel();
		//include a status bar
		return pane;
		}

	@Override
	public boolean resourceChanged()
		{
		commitChanges();
		return !(new ResourceComparator().areEqual(res,resOriginal));
		}

	public void revertResource()
		{
		LGM.currentFile.paths.replace(res,resOriginal);
		}

	public void commitChanges()
		{
		res.setName(name.getText());
		res.precision = tpr.getIntValue();
		res.smooth = smooth.isSelected();
		res.closed = closed.isSelected();
		}

	//IntegerField was changed
	private void notifyList(DocumentEvent arg0)
		{
		if (!manualUpdate) return;
		Point p = (Point) list.getSelectedValue();
		if (p == null) return;
		p.x = tx.getIntValue();
		p.y = ty.getIntValue();
		p.speed = tsp.getIntValue();
		manualUpdate = false;
		list.updateUI();
		manualUpdate = true;
		}

	public void changedUpdate(DocumentEvent arg0)
		{
		notifyList(arg0);
		super.changedUpdate(arg0);
		}

	public void insertUpdate(DocumentEvent arg0)
		{
		notifyList(arg0);
		super.insertUpdate(arg0);
		}

	public void removeUpdate(DocumentEvent arg0)
		{
		notifyList(arg0);
		super.removeUpdate(arg0);
		}

	//Button was clicked
	public void actionPerformed(ActionEvent e)
		{
		if (e.getSource() == add)
			{
			res.points.add(new Point(tx.getIntValue(),ty.getIntValue(),tsp.getIntValue()));
			list.setListData(res.points.toArray());
			list.updateUI();
			list.setSelectedIndex(res.points.size() - 1);
			}
		if (e.getSource() == insert)
			{
			int i = list.getSelectedIndex();
			if (i == -1) return;
			res.points.add(i,new Point(tx.getIntValue(),ty.getIntValue(),tsp.getIntValue()));
			list.setListData(res.points.toArray());
			list.updateUI();
			list.setSelectedIndex(i);
			}
		if (e.getSource() == delete)
			{
			int i = list.getSelectedIndex();
			Object o = list.getSelectedValue();
			if (o == null) return;
			res.points.remove(o);
			list.setListData(res.points.toArray());
			list.updateUI();
			if (i >= res.points.size()) i = res.points.size() - 1;
			list.setSelectedIndex(i);
			}
		super.actionPerformed(e);
		}

	//List selection changed
	public void valueChanged(ListSelectionEvent e)
		{
		if (!manualUpdate || e.getValueIsAdjusting()) return;
		Point p = (Point) list.getSelectedValue();
		if (p == null) return;
		manualUpdate = false;
		tx.setIntValue(p.x);
		ty.setIntValue(p.y);
		tsp.setIntValue(p.speed);
		manualUpdate = true;
		}
	}
