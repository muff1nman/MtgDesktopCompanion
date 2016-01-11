package org.magic.api.providers.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.MagicCardsProvider;

import com.google.gson.Gson;

public class DeckbrewProvider implements MagicCardsProvider {
	private final String urldeckbrewJSON = "https://api.deckbrew.com/mtg";
	private Gson gson;
	
	static final Logger logger = LogManager.getLogger(DeckbrewProvider.class.getName());

	public DeckbrewProvider() {
		gson = new Gson();
	}
	
	public void init() {
		
	}
	
	
	public MagicCard getCardById(String id) throws  IOException {
		String url = urldeckbrewJSON +"/cards/"+id;
		logger.debug("get Card ID " + url );
		Reader reader = new InputStreamReader(new URL(url).openStream(),"UTF-8");
		return gson.fromJson(reader, MagicCard.class);
				
	}
	
	
	
	
	public List<MagicCard> searchCardByCriteria(String att,String crit) throws IOException {
		String url = urldeckbrewJSON+"/cards";
		
		crit=att+"="+crit;
		/*if(crit!=null)*/
			url = urldeckbrewJSON +"/cards?"+crit;
		
		
		
		logger.debug("Connexion to " + url);
		
		Reader reader = new InputStreamReader(new URL(url).openStream(),"UTF-8");
		
		MagicCard[] res = gson.fromJson(reader, MagicCard[].class);
		
		List<MagicCard> retour=new ArrayList<MagicCard>();
		retour.addAll(Arrays.asList(res));
		
		int page=1;
		while(res.length==100)
		{
			String pagination;
			if(crit==null)
				pagination="?";	
			else
				pagination="&";
				
			
			reader = new InputStreamReader(new URL(url+pagination+"page="+page++).openStream(),"UTF-8");
			
			res = gson.fromJson(reader, MagicCard[].class);
			retour.addAll(Arrays.asList(res));
		}
	
		
		return retour;
	}
	
	public List<MagicEdition> searchSetByCriteria(String att,String crit) throws IOException  {
		
		String url = urldeckbrewJSON+"/sets";
		
		if(crit!=null)
			url = urldeckbrewJSON+"/sets?"+att+"="+crit;
		
		Reader reader = new InputStreamReader(new URL(url).openStream(),"UTF-8");
		List<MagicEdition> list =  Arrays.asList(gson.fromJson(reader, MagicEdition[].class));
		for(MagicEdition me : list)
				{
					me.setSet(me.getId());
				}
		
		return list;
	}

	public MagicEdition getSetById(String id) throws IOException   {

		String url = urldeckbrewJSON+"/sets/"+id;
		Reader reader = new InputStreamReader(new URL(url).openStream(),"UTF-8");
		logger.debug("Get Set By ID "  + url);
		return gson.fromJson(reader, MagicEdition.class);
	}


	@Override
	public List<String> getListType() throws IOException {
		String url = urldeckbrewJSON+"/types";
		
		Reader reader = new InputStreamReader(new URL(url).openStream(),"UTF-8");
		return Arrays.asList(gson.fromJson(reader, String[].class));
	}


	@Override
	public List<String> getListSubTypes() throws IOException {
		String url = urldeckbrewJSON+"/subtypes";
		
		Reader reader = new InputStreamReader(new URL(url).openStream(),"UTF-8");
		return Arrays.asList(gson.fromJson(reader, String[].class));
	}


	@Override
	public String[] getQueryableAttributs() {
		return new String[]{"name","type","subtype","supertype","oracle","set","rarity","color","multicolor","multiverseid","format","status"};
		
	}
	
	@Override
	public String toString() {
		return "DeckBrew Provider";
	}

	@Override
	public String[]  getLanguages() {
		return null;
	}

	@Override
	public List<MagicCard> openBooster(MagicEdition me) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MagicCard getCardByNumber(String id, MagicEdition me) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersion() {
		return "1";
	}

	
}