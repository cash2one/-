package com.chengxin.Entity;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

public class MovingContent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String content;
	public String typefile;
	
	
	
	public MovingContent(String content, String typefile) {
		super();
		this.content = content;
		this.typefile = typefile;
	}


	public MovingContent() {
		super();
	}

	public static MovingContent getInfo(String json) {
		try {
			return JSONObject.parseObject(json, MovingContent.class);//toJavaObject(JSONObject.parseObject(json),
					//Card.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getInfo(MovingContent info) {
		String json = JSONObject.toJSON(info).toString();
		return json;
	}

}
