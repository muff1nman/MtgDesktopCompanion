package org.magic.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicCollection;
import org.magic.api.beans.MagicEdition;
import org.magic.api.beans.MagicPrice;
import org.magic.api.interfaces.MagicPricesProvider;
import org.magic.db.MagicDAO;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;

public class MagicWebSiteGenerator extends Observable{
	
	Template template ;
	Configuration cfg ;
	MagicDAO dao;
	private String dest;
	private List<MagicPricesProvider> pricesProvider;
	
	public MagicWebSiteGenerator(MagicDAO dao,String template,String dest) throws IOException, ClassNotFoundException, SQLException {
		cfg = new Configuration(Configuration.VERSION_2_3_23);
		cfg.setDirectoryForTemplateLoading(new File("./templates"+"/"+template));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setNumberFormat("#");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER );
		cfg.setObjectWrapper( new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_23).build());
		this.dao=dao;
		this.dest = dest;
		FileUtils.copyDirectory(new File("./templates/"+template), new File(dest),new FileFilter() {
			public boolean accept(File pathname) {
				if(pathname.isDirectory())
					return true;
				
				if(pathname.getName().endsWith(".html"))
					return false;
				
				return true;
			}
		});
	
	}
	
	
	public void generate(List<MagicCollection> cols,List<MagicPricesProvider> providers) throws TemplateException, IOException, SQLException
	{
		
		this.pricesProvider=providers;
		
		Map<String,List<MagicCard>> root = new HashMap<String,List<MagicCard>>();
		
	 	for(MagicCollection col :cols)
	 		root.put(col.getName(), dao.getCardsFromCollection(col));
		
	 	template = cfg.getTemplate("index.html");
		Writer out = new FileWriter(new File(dest+"\\index.htm"));
		template.process(root, out);
		
		template = cfg.getTemplate("page-col.html");
		
		for(String colName : root.keySet())
		{
			out = new FileWriter(new File(dest+"\\page-col-"+colName+".htm"));
			
			
			Set<MagicEdition> editions = new LinkedHashSet<>();
			for(MagicCard mc : root.get(colName))
			{
				editions.add(mc.getEditions().get(0));
			}
		
			Map rootEd = new HashMap<>();
				rootEd.put("colName", colName);
				rootEd.put("cols",  cols);
				rootEd.put("cards", root.get(colName));
				rootEd.put("editions",editions);
				generateCardsTemplate(root.get(colName),cols);
				template.process(rootEd, out);
		}
		
		
		
		
	}

	int i=0;
	private void generateCardsTemplate(List<MagicCard> list, List<MagicCollection> cols) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		Template cardTemplate = cfg.getTemplate("page-card.html");
		for(MagicCard mc : list){
				Map rootEd = new HashMap<>();
				rootEd.put("card", mc);
				rootEd.put("cols", cols);
				
				List<MagicPrice> prices= new ArrayList<MagicPrice>();
				if(pricesProvider.size()>0)
				{
					for(MagicPricesProvider prov : pricesProvider)
					{
						try 
						{
							prices.addAll(prov.getPrice(mc.getEditions().get(0), mc));
						} 
						catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
				}
				rootEd.put("prices", prices);
				FileWriter out = new FileWriter(new File(dest+"\\page-card-"+mc.getId()+".htm"));
				cardTemplate.process(rootEd, out);
				
				setChanged();
				notifyObservers(i++);
		}
		
	}
}
