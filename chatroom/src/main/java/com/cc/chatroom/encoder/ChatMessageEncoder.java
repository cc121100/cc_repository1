package com.cc.chatroom.encoder;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.cc.chatroom.message.ChatMessage;

public class ChatMessageEncoder implements Encoder.Text<ChatMessage>{

	public void destroy() {
		
	}

	public void init(EndpointConfig arg0) {
		
	}

	public String encode(ChatMessage arg0) throws EncodeException {
		ObjectMapper om = new ObjectMapper();
		om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		String result = "";
		try {
			result = om.writeValueAsString(arg0);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
