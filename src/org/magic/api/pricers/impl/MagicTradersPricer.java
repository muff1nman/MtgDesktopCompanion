package org.magic.api.pricers.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicPricesProvider;

public class MagicTradersPricer implements MagicPricesProvider {

	String url = "http://classic.magictraders.com/pricelists/current-magic-excel.txt";
	static final Logger logger = LogManager.getLogger(MagicTradersPricer.class.getName());
	Properties props;
	private boolean enable=false;
	
	public MagicTradersPricer() {
		props = new Properties();
		props.put("MAX", 5);
		props.put("URL", "http://classic.magictraders.com/pricelists/current-magic-excel.txt");
		props.put("WEBSITE", "http://classic.magictraders.com");
		props.put("KEYWORD", "");
	}
	
	
	
	public String toString()
	{
		return getName();
	}
	
	public List<MagicPrice> getPrice(MagicEdition me, MagicCard card) throws Exception {
		
		
		URL link = new URL(props.getProperty("URL"));
		InputStream is = link.openStream();
		BufferedReader read = new BufferedReader(new InputStreamReader(is));
		String line;
		List<MagicPrice> list = new ArrayList<MagicPrice>();
	
		while ((line = read.readLine()) != null) {
			String[] fields = line.split("\\|");
			if (fields.length < 8)
				continue;

			String name = fields[0].trim();
			String price = fields[1].trim();
			try {
				double f = Double.parseDouble(price);
				String cname = getCorrectName(card.getName());
				props.put("KEYWORD",cname);
				if(name.startsWith(cname))
				{
					logger.debug(getName() + " found "+ cname);
   				 MagicPrice mp = new MagicPrice();
							mp.setSeller(getName());
							mp.setUrl("http://store.eudogames.com/products/search?query="+URLEncoder.encode(card.getName(),"UTF-8"));
							mp.setSite(getName());
							mp.setValue(f);
							mp.setCurrency("$");
							list.add(mp);
							read.close();
							return list;
				}
			} catch (NumberFormatException e) {
				continue;
			} 
		}
	    
		return list;
	}

	
	private String getCorrectName(String cname)
	{
		if (cname.contains("AE")) {
			cname = cname.replaceAll("AE", "Æ");
		}
		int sl = cname.indexOf('/');
		if (sl >= 0) {
			cname = cname.replaceFirst("/", " // ");
			cname += " (" + cname.substring(0, sl) + ")";
		}
		return cname;
	}
	

	public String getName() {
		return "Magic Traders";
	}

	@Override
	public Properties getProperties() {
		return props;
	}
	@Override
	public void setProperties(String k, Object value) {
		props.put(k, value);
		
	}

	@Override
	public Object getProperty(String k) {
		return props.get(k);
	}


	@Override
	public boolean isEnable() {
		return enable;
	}

	@Override
	public void enable(boolean t) {
		this.enable=t;
		
	}
	

}
