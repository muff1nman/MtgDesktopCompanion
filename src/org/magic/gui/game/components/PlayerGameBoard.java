package org.magic.gui.game.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.magic.api.beans.MagicDeck;
import org.magic.game.model.GameManager;
import org.magic.game.model.Player;
import org.magic.gui.components.dialog.JDeckChooserDialog;
import org.magic.services.CockatriceTokenProvider;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class PlayerGameBoard extends JPanel implements Observer {
	
	
	private JSpinner spinLife;
	private JSpinner spinPoison;
	private BattleFieldPanel panelBattleField;
	private ManaPoolPanel manaPoolPanel ;
	private JLabel lblPlayer;
	public  Player player;
	private JLabel lblHandCount;
	private JLabel lblLibraryCount;
	private static PlayerGameBoard instance;
	private CockatriceTokenProvider tokenGenerator;
	
	public static PlayerGameBoard getInstance()
	{
		if (instance==null)
			instance = new PlayerGameBoard();
		
		return instance;
	}
	
	public static PlayerGameBoard newInstance()
	{
		return new PlayerGameBoard();
	}
	
	
	public BattleFieldPanel getPanelBattleField() {
		return panelBattleField;
	}

	public void initGame()
	{
		player.init();
	}
	
	public void setPlayer(Player p1) {
		player=p1;
		lblPlayer.setText(p1.getName());
		player.addObserver(this);
		spinLife.setValue(p1.getLife());
		spinPoison.setValue(p1.getPoisonCounter());
		manaPoolPanel.setPlayer(p1);
		panelBattleField.setPlayer(p1);
	}
	
	
	public CockatriceTokenProvider getTokenGenerator() {
		return tokenGenerator;
	}



	public PlayerGameBoard() {
		setBorder(new LineBorder(Color.RED, 2));
		
		setLayout(new BorderLayout(0, 0));
		
		tokenGenerator= new CockatriceTokenProvider();
														
														JPanel panelInfo = new JPanel();
														add(panelInfo, BorderLayout.WEST);
														panelInfo.setLayout(new BorderLayout(0, 0));
														
														JPanel lifePanel = new JPanel();
														lifePanel.setAlignmentY(Component.TOP_ALIGNMENT);
														panelInfo.add(lifePanel, BorderLayout.NORTH);
														lifePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
														
														lblPlayer = new JLabel("");
														lblPlayer.setIcon(new ImageIcon(PlayerGameBoard.class.getResource("/res/planeswalker.png")));
														lifePanel.add(lblPlayer);
														
														JPanel panelPoolandDescribes = new JPanel();
														panelInfo.add(panelPoolandDescribes, BorderLayout.CENTER);
														
														panelPoolandDescribes.setLayout(new BorderLayout(0, 0));
														
														JPanel panelPoolandHandsLib = new JPanel();
														panelPoolandDescribes.add(panelPoolandHandsLib, BorderLayout.NORTH);
														panelPoolandHandsLib.setLayout(new BorderLayout(0, 0));
														
														manaPoolPanel = new ManaPoolPanel();
														panelPoolandHandsLib.add(manaPoolPanel);
														
														JPanel panelHandLib = new JPanel();
														panelPoolandHandsLib.add(panelHandLib, BorderLayout.EAST);
														panelHandLib.setLayout(new GridLayout(2, 1, 0, 0));
														
														lblHandCount = new JLabel("0");
														lblHandCount.setFont(new Font("Tahoma", Font.BOLD, 18));
														lblHandCount.setHorizontalTextPosition(JLabel.CENTER);
														lblHandCount.setIcon(new ImageIcon(PlayerGameBoard.class.getResource("/res/hand.png")));
														panelHandLib.add(lblHandCount);
														
														lblLibraryCount = new JLabel("");
														lblLibraryCount.setHorizontalTextPosition(SwingConstants.CENTER);
														lblLibraryCount.setHorizontalAlignment(SwingConstants.CENTER);
														lblLibraryCount.setFont(new Font("Tahoma", Font.BOLD, 18));
														lblLibraryCount.setIcon(new ImageIcon(PlayerGameBoard.class.getResource("/res/librarysize.png")));
														panelHandLib.add(lblLibraryCount);
														
														JPanel panel = new JPanel();
														panelPoolandHandsLib.add(panel, BorderLayout.WEST);
														panel.setLayout(new GridLayout(2, 2, 0, 0));
														
																
																JLabel lblLife = new JLabel("");
																panel.add(lblLife);
																lblLife.setHorizontalAlignment(SwingConstants.CENTER);
																lblLife.setIcon(new ImageIcon(PlayerGameBoard.class.getResource("/res/heart.png")));
																
																spinLife = new JSpinner();
																panel.add(spinLife);
																spinLife.setFont(new Font("Tahoma", Font.BOLD, 17));
																
																JLabel lblPoison = new JLabel("");
																panel.add(lblPoison);
																lblPoison.setHorizontalAlignment(SwingConstants.CENTER);
																lblPoison.setIcon(new ImageIcon(PlayerGameBoard.class.getResource("/res/poison.png")));
																
																spinPoison = new JSpinner();
																panel.add(spinPoison);
																spinPoison.setFont(new Font("Tahoma", Font.BOLD, 15));
																
																panelBattleField = new BattleFieldPanel();
																add(panelBattleField, BorderLayout.CENTER);
																panelBattleField.setLayout(null);
																
																spinPoison.addChangeListener(new ChangeListener() {
																	
																	public void stateChanged(ChangeEvent e) {
																		if(player !=null)
																			player.setPoisonCounter((int)spinPoison.getValue());
																		
																	}
																});
																spinLife.addChangeListener(new ChangeListener() {
																	
																	public void stateChanged(ChangeEvent e) {
																		if(player !=null) 
																			player.setLife((int)spinLife.getValue());
																		
																	}
																});
	}
	public JSpinner getSpinLife() {
		return spinLife;
	}
	public JSpinner getSpinPoison() {
		return spinPoison;
	}

	
	@Override
	public void update(Observable o, Object arg) {
		String act = player.getName() +" " + arg.toString();
		lblHandCount.setText(String.valueOf(player.getHand().size()));
		lblLibraryCount.setText(String.valueOf(player.getLibrary().size()));
	}

	

	public Player getPlayer() {
		return player;
	}

}