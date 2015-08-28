package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class GroupList implements Serializable{

	private static final long serialVersionUID = -21746454545461L;
	public WeiYuanState mState;
	public List<Group> mGroupList;
	public PageInfo mPageInfo;

	public GroupList(){}
	public GroupList(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("state")){
				mState = new WeiYuanState(json.getJSONObject("state"));
			}
			
			if(mState != null && mState.code == 0){
				if(!json.isNull("data")){
					JSONArray array = json.getJSONArray("data");
					if(array != null && array.length() != 0){
						mGroupList = new ArrayList<Group>();
						for (int i = 0; i < array.length(); i++) {
							Group grop = new Group(array.getJSONObject(i));
							grop.id = i+1;
							mGroupList.add(grop);
						}
					}
				}
			}
			if(!json.isNull("pageInfo")){
				mPageInfo = new PageInfo(json.getJSONObject("pageInfo"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}