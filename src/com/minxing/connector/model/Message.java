package com.minxing.connector.model;

import com.minxing.connector.json.JSONArray;
import com.minxing.connector.json.JSONException;
import com.minxing.connector.json.JSONObject;

public class Message {
	
	long message_id;
	
	public Message(long _message_id) {
		this.message_id = _message_id;
	}
	
	public static Message fromJSON(JSONObject data) {
		try {
			 JSONArray message_item = data.getJSONArray("items");
			 JSONObject message = message_item.getJSONObject(0);
			 return new Message(message.getLong("id"));
		} catch (JSONException e) {
			throw new MxException(e);
		}
		
	}

}
