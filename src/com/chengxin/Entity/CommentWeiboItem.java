package com.chengxin.Entity;

import java.io.Serializable;

import com.chengxin.net.WeiYuanInfo;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class CommentWeiboItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int id;
	public String uid;
	public String name;
	public String headurl;
	public int sid;
	public String content;
	public long createtime;
	public CommentWeiboItem() {
		super();
	}
	public CommentWeiboItem(JSONObject json) {
		super();
		if(json == null || json.equals("")){
			return;
		}
		try {
			this.id = json.getInt("id");
			this.uid = json.getString("uid");
			this.sid = json.getInt("sid");
			this.content = json.getString("content");
			this.createtime = json.getLong("createtime");
			if(!json.isNull("name")){
				this.name = json.getString("name");
			}
			if(this.name == null || this.name.equals("")){
				this.name = "无名";
			}
			if(!json.isNull("headsmall")){
				String url = json.getString("headsmall");
				if(url!=null && !url.equals("")){
					this.headurl = WeiYuanInfo.HEAD_URL + url;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
