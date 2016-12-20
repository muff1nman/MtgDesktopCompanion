package org.magic.api.pictures.impl;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.magic.api.beans.MagicCard;
import org.magic.api.beans.MagicEdition;
import org.magic.api.interfaces.abstracts.AbstractPicturesProvider;
import org.magic.api.providers.impl.PrivateMTGSetProvider;

public class PersonalSetPicturesProvider extends AbstractPicturesProvider {

	BufferedImage back;
//	PicturesCache cache = new MemoryCache();
	
	
	public void savePicture(BufferedImage bi,MagicCard mc,MagicEdition ed) throws IOException
	{
		File mainDir = new File(props.getProperty("PICS_DIR"));
		File edDir = new File(mainDir,ed.getId());
		
		
		if(!edDir.exists())
			edDir.mkdir();
		
		
		ImageIO.write(bi,props.getProperty("FORMAT"),new File(edDir,"/"+mc.getId()+"."+props.getProperty("FORMAT").toLowerCase()));
	}
	
	
	public PersonalSetPicturesProvider() {
		super();
		
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("PICS_DIR", PrivateMTGSetProvider.confdir+"/privatePics");
			props.put("FORMAT", "PNG");
			save();
		}
		
		if(!new File(props.getProperty("PICS_DIR")).exists())
			new File(props.getProperty("PICS_DIR")).mkdir();
		
	}
	
	
	@Override
	public BufferedImage getBackPicture() throws Exception
	{
		URL url = new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=132667&type=card");
		return ImageIO.read(url);
	}
	
	@Override
	public BufferedImage extractPicture(MagicCard mc) throws Exception
	{
		return null;
	}
	
	
	
	@Override
	public BufferedImage getPicture(MagicCard mc,MagicEdition ed) throws Exception{
		File mainDir = new File(props.getProperty("PICS_DIR"));
		File edDir = new File(mainDir,ed.getId());
		
		if(edDir.exists())
			return ImageIO.read(new File(edDir,mc.getId()+"."+props.getProperty("FORMAT")));
		else
			return null;
	}
	
	private BufferedImage getPicture(String multiverseid) throws Exception{
		return null;
	}
	
	

	@Override
	public BufferedImage getSetLogo(String set, String rarity) throws Exception {
		return null;
	}

	@Override
	public String getName() {
		return "Personal Set Pictures";
	}


}
