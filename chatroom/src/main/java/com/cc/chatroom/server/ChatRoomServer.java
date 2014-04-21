package com.cc.chatroom.server;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import com.cc.chatroom.decoder.MessageDecoder;
import com.cc.chatroom.encoder.ChatMessageEncoder;
import com.cc.chatroom.encoder.InfoMessageEncoder;
import com.cc.chatroom.message.ChatMessage;
import com.cc.chatroom.message.InfoMessage;
import com.cc.chatroom.message.Message;

@ServerEndpoint(value = "/cr/{roomId}",
				encoders = {ChatMessageEncoder.class,InfoMessageEncoder.class},
				decoders = {MessageDecoder.class})  
public class ChatRoomServer {
	
	//private static final Logger logger = Logger.getLogger("ChatRoomServer");
	
	//private static Queue<Session> queue = new ConcurrentLinkedQueue<Session>();
	
	private static Map<InfoMessage, Session> maps = new HashMap<InfoMessage, Session>();
	
    @OnMessage  
    public void inMessage(Session session,Message message) {  
    	
    	synchronized (this) {
    		if(message instanceof InfoMessage){
        		
        		InfoMessage infoMessage = (InfoMessage) message;
        		if(infoMessage.getMsgType().equals("login")){
        			infoMessage.setSessionId(session.getId());
        			maps.put(infoMessage, session);
        			try {
        				
        				//send to client a success info
    					session.getBasicRemote().sendText("success");
    					
    					//send to other clients in the same room ,a user is enter the room
    					for(Entry<InfoMessage, Session> entry : maps.entrySet()){
            				InfoMessage info = entry.getKey();
            				if(info.getRoomId().equals(infoMessage.getRoomId()) 
            					&& !info.getUserId().equals(infoMessage.getUserId())){
            					try {
            						entry.getValue().getBasicRemote().sendObject(infoMessage);
            					} catch (IOException e) {
            						e.printStackTrace();
            					} catch (EncodeException e) {
            						e.printStackTrace();
            					}
            				}
            			}
    					
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
        		}
        		
        	}
        	
        	else if(message instanceof ChatMessage){
        		
        		ChatMessage chatMessage = (ChatMessage) message;
        		
        		//to someone chat
        		if(chatMessage.getTo() != null && chatMessage.getTo().length() > 1){
        			for(Entry<InfoMessage, Session> entry : maps.entrySet()){
        				if(chatMessage.getTo().equals(entry.getKey().getUserId())){
        					try {
    							entry.getValue().getBasicRemote().sendObject(chatMessage);
    						} catch (IOException e) {
    							e.printStackTrace();
    						} catch (EncodeException e) {
    							e.printStackTrace();
    						}
        				}
        			}
        		}
        		
        		//group chat
        		else{
        			for(Entry<InfoMessage, Session> entry : maps.entrySet()){
        				InfoMessage infoMessage = entry.getKey();
        				if(infoMessage.getRoomId().equals(chatMessage.getRoomId()) 
        					&& !infoMessage.getUserId().equals(chatMessage.getUserId())){
        					try {
        						entry.getValue().getBasicRemote().sendObject(chatMessage);
        					} catch (IOException e) {
        						e.printStackTrace();
        					} catch (EncodeException e) {
        						e.printStackTrace();
        					}
        				}
        			}
        			
        		}
        		
        	}
		}
    	
    }  
    
	@OnOpen  
    public void open(Session session) {  
		 System.out.println("Client connected:" + session.getId());
    }  
      
    @OnClose  
    public void end(Session session) {  
    	synchronized (this) {
    		InfoMessage info  = null;
    		
    		//send a exit info to others users in the same room
    		for(Entry<InfoMessage, Session> entry : maps.entrySet()){
    			InfoMessage infoMessage = entry.getKey();
    			if(infoMessage.getSessionId().equals(session.getId())){
    				info = infoMessage;
    				break;
    			}
    		}
    		
    		
    		if(info != null){
    			info.setMsgType("exit");
    			info.setDate(new Date());
    			//send a exit info to others users in the same room
    			for(Entry<InfoMessage, Session> entry : maps.entrySet()){
    				InfoMessage infoMessage = entry.getKey();
    				
    				if(infoMessage.getRoomId().equals(info.getRoomId())
						&& !infoMessage.getSessionId().equals(info.getSessionId())){
    					try {
    						entry.getValue().getBasicRemote().sendObject(info);
    					} catch (IOException e) {
    						e.printStackTrace();
    					} catch (EncodeException e) {
    						e.printStackTrace();
    					}
    				}
    			}
    		}
    		
    		//remove the session from the maps
    		Iterator<Entry<InfoMessage, Session>> it = maps.entrySet().iterator();  
    		while(it.hasNext()){  
    			Entry<InfoMessage, Session> entry=it.next();  
    			InfoMessage infoMessage = entry.getKey();
    			if(infoMessage.getSessionId().equals(session.getId())){
    				it.remove();
    				break;
    			}
    		}
    		System.out.println("Connection closed:" + session.getId());
    		
    	}
    }  
}
