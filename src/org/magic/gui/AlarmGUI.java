package org.magic.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;

import org.magic.api.beans.MagicCardAlert;
import org.magic.api.beans.MagicPrice;
import org.magic.gui.components.MagicCardDetailPanel;
import org.magic.gui.components.MagicPriceComponent;
import org.magic.gui.models.CardAlertTableModel;
import org.magic.services.MagicFactory;

public class AlarmGUI extends JPanel {
	private JTable table;
	private CardAlertTableModel model;
	private MagicCardDetailPanel magicCardDetailPanel ;
	private DefaultListModel<MagicPrice> resultListModel;
	private JList<MagicPrice> list;
	private JSplitPane splitPanel;
	private JPanel panel;
	private JButton btnRefresh;
	private JButton btnDelete;
	
	public AlarmGUI() {
		setLayout(new BorderLayout());
		
		
		splitPanel = new JSplitPane();
		splitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPanel, BorderLayout.CENTER);
		
		JScrollPane scrollTable = new JScrollPane();
		scrollTable.setPreferredSize(new Dimension(2, 200));
		splitPanel.setLeftComponent(scrollTable);
		
		table = new JTable();
		model = new CardAlertTableModel();
		table.setModel(model);
		
		
		table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,boolean hasFocus, int row, int column) {
				Component comp=super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				comp.setForeground(Color.BLACK);
				
				if((Integer)value>0)
					comp.setBackground(Color.GREEN);
				else
					comp.setBackground(table.getBackground());
				return comp;
			}
			
		});
		
		
		
		scrollTable.setViewportView(table);
		
		magicCardDetailPanel = new MagicCardDetailPanel();
		splitPanel.setRightComponent(magicCardDetailPanel);
		magicCardDetailPanel.enableThumbnail(true);
		
		table.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
					
				resultListModel.removeAllElements();
				MagicCardAlert selected = (MagicCardAlert)table.getValueAt(table.getSelectedRow(), 0);
					magicCardDetailPanel.setMagicCard(selected.getCard());
					
					for(MagicPrice mp : selected.getOffers())
						resultListModel.addElement(mp);
				}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.EAST);
		
		resultListModel= new DefaultListModel<MagicPrice>();
			
		list = new JList<MagicPrice>(resultListModel);
		list.setCellRenderer(new ListCellRenderer<MagicPrice>() {
			@Override
			public Component getListCellRendererComponent(JList<? extends MagicPrice> list, MagicPrice value, int index,boolean isSelected, boolean cellHasFocus) {
				return new MagicPriceComponent(value);
			}
		});
		
		
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					
					if(e.getClickCount() == 2)
						if(list.getSelectedValue()!=null)
						{
							MagicPrice p = (MagicPrice)list.getSelectedValue();
							Desktop.getDesktop().browse(new URI(p.getUrl()));
						}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1,"ERROR",JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		scrollPane.setViewportView(list);
		
		panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
		btnRefresh = new JButton("");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				model.fireTableDataChanged();
			}
		});
		btnRefresh.setIcon(new ImageIcon(AlarmGUI.class.getResource("/res/refresh.png")));
		panel.add(btnRefresh);
		
		btnDelete = new JButton("");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row =table.getSelectedRow();
				if(row>0)
				{
					try {
						MagicCardAlert alert = (MagicCardAlert)model.getValueAt(row,0);
						MagicFactory.getInstance().getEnabledDAO().deleteAlert(alert);
						model.fireTableDataChanged();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1,"ERROR",JOptionPane.ERROR_MESSAGE);
					}
				}
				
				
			}
		});
		btnDelete.setIcon(new ImageIcon(AlarmGUI.class.getResource("/res/delete.png")));
		panel.add(btnDelete);
		addComponentListener(new ComponentAdapter() {
		      public void componentShown(ComponentEvent componentEvent) {
		    	  splitPanel.setDividerLocation(.5);
		    	  model.fireTableDataChanged();
		    	  removeComponentListener(this);
		      }

		    });
		
	}

}