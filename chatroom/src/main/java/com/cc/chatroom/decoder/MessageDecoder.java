package com.cc.chatroom.decoder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;

import com.cc.chatroom.message.ChatMessage;
import com.cc.chatroom.message.InfoMessage;
import com.cc.chatroom.message.Message;

public class MessageDecoder implements Decoder.Text<Message>{
	
	private HashMap<String, String> map;

	public void destroy() {
		
	}

	public void init(EndpointConfig arg0) {
		
	}

	public Message decode(String arg0) throws DecodeException {
		if(willDecode(arg0)){
			
			if(map.get("msgType").equals("chat")){
				ChatMessage chatMessage = new ChatMessage();
				try {
					chatMessage.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(map.get("date")));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				chatMessage.setUserId(map.get("userId"));
				chatMessage.setUserName(map.get("userName"));
				chatMessage.setRoomId(map.get("roomId"));
				chatMessage.setMessage(map.get("message"));
				chatMessage.setTo(map.get("to"));
				chatMessage.setMsgType(map.get("msgType"));
				return chatMessage;
				
			}else if(map.get("msgType").equals("login")){
				InfoMessage infoMessage = new InfoMessage();
				try {
					infoMessage.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(map.get("date")));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				infoMessage.setMsgType("login");
				infoMessage.setUserId(map.get("userId"));
				infoMessage.setUserName(map.get("userName"));
				infoMessage.setRoomId(map.get("roomId"));
				infoMessage.setNewRoomId(map.get("newRoomId"));
				infoMessage.setMsgType(map.get("msgType"));
				return infoMessage;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}

	public boolean willDecode(String arg0) {
		// TODO Auto-generated method stub
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			map = (HashMap<String, String>) objectMapper.readValue(arg0, Map.class);
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
