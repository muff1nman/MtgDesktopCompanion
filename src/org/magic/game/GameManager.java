package org.magic.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.magic.api.beans.MagicDeck;
import org.magic.gui.game.GamePanel;
import org.magic.services.exports.MagicSerializer;

public class GameManager {

	private Player player;
	
	private static GameManager instance;
	private List<Turn> turns;

	private GameManager()
	{
		turns = new ArrayList<Turn>();
	}
	

	public List<Turn> getTurns() {
		return turns;
	}

	public Turn getActualTurn()
	{
		return turns.get(turns.size()-1);
	}
	
	
	public void nextTurn()
	{
		turns.add(new Turn());
		player.logAction("New turn : " + turns.size());
		
	}
	
	
	public static GameManager getInstance()
	{
		if(instance==null)
			instance = new GameManager();
		
		return instance;
	}
	
	
	public void setPlayer(Player p) {
		player=p;
		nextTurn();
	}
	

	public Player getPlayer() {
		return player;
	}

	

	public static void main(String[] args) throws Exception {
		//Player p1 = new Player(MagicSerializer.read(new File("C:/Users/Pihen/magicDeskCompanion/decks/Jund.deck"), MagicDeck.class));
		
		
		Player p1 = new Player(MagicSerializer.read(new File("C:/Users/Nicolas/magicDeskCompanion/decks/GW TOKENS.deck"), MagicDeck.class));
		
		
		GameManager.getInstance().setPlayer(p1);
		GameManager.getInstance().initGame();
		JFrame f = new JFrame(p1.getName() +"->" + p1.getDeck().getName());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GamePanel p = new GamePanel();
		
		p.setPlayer(p1);
		f.getContentPane().add(p);
		f.setVisible(true);
		f.setSize(1024, 800);
	}


	public void initGame() {
		player.init();
		
	}
	
}