package org.magic.servers.impl;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.magic.api.interfaces.abstracts.AbstractMTGServer;
import org.magic.game.Player;

public class MTGGameRoomServer extends AbstractMTGServer{
 static final Logger logger = LogManager.getLogger(MTGGameRoomServer.class.getName());
 private IoAcceptor acceptor;
 private IoHandlerAdapter adapter = new IoHandlerAdapter() {
 		
 		private List<Player> players= new ArrayList<Player>();
 		
 		@Override
 		public void sessionCreated(IoSession session) throws Exception {
 	 		//System.out.println("Session created");
 		}
 	 	
 	 	@Override
 		public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
 	 		//System.out.println( "IDLE " + session.getIdleCount( status ));
 		}
 	 
 	 	@Override
 	 	public void messageReceived(IoSession session, Object message) throws Exception {
 	 		if(message instanceof Player)
 	 		{
 	 			Player p = (Player)message;
 	 			players.add(p);
 	 			logger.info("Connexion : " + p.getName() );
 	 		}
 	 		if(message instanceof String)
 	 		{
 	 			logger.debug(message);
 	 			if(message.equals("LIST_PLAYER"))
 	 				session.write(players);
 	 		}
 	 		
 	 		
 	 	}
 	 	

 	  @Override
 	    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
 	    {
 	      logger.error(cause);
 	    }
	};
	
	public MTGGameRoomServer() throws IOException {
		
		super();
		if(!new File(confdir, getName()+".conf").exists()){
			props.put("SERVER-PORT", "18567");
			props.put("IDLE-TIME", "10");
			props.put("BUFFER-SIZE", "2048");
			props.put("AUTOSTART", "false");
			save();
		}
		
		
    	acceptor = new NioSocketAcceptor();
        acceptor.setHandler(adapter);
        acceptor.getFilterChain().addLast( "logger", new LoggingFilter() );
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        acceptor.getSessionConfig().setReadBufferSize( Integer.parseInt(props.getProperty("BUFFER-SIZE")) );
        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, Integer.parseInt(props.getProperty("IDLE-TIME")) );
        acceptor.getSessionConfig().setUseReadOperation(true);
	}
 	
 	
	
	 public static void main(String[] args) throws Exception {
		 new MTGGameRoomServer().start();
	 }



	@Override
	public void start() throws Exception {
		 acceptor.bind( new InetSocketAddress(Integer.parseInt(props.getProperty("SERVER-PORT"))) );
		 logger.info("Server started on port " + props.getProperty("SERVER-PORT") +" ...");
	}



	@Override
	public void stop() throws Exception {
		logger.info("Server closed");
		acceptor.unbind();
	}



	@Override
	public boolean isAlive() {
		return acceptor.isActive();
	}



	@Override
	public boolean isAutostart() {
		return props.getProperty("AUTOSTART").equals("true");
	}



	@Override
	public String getName() {
		return "MTG Game Server";
	}

}