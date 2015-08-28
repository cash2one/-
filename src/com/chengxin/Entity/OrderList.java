package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class OrderList implements Serializable{

	private static final long serialVersionUID = 1946436545154L;
	
	public List<Order> mOrderList;
	public WeiYuanState mState;
	public PageInfo mPageInfo;

	public OrderList(){}
	
	public OrderList(String reString){
		try {
			JSONObject json = new JSONObject(reString);
			if(!json.isNull("data")){
				JSONArray array = json.getJSONArray("data");
				if(array != null){
					mOrderList = new ArrayList<Order>();
					List<Order> tempList = Order.constructOrderList(array);
					if(tempList != null){
						mOrderList.addAll(tempList);
					}
				}
			}
			
			if(!json.isNull("state")){
				mState = new WeiYuanState(json.getJSONObject("state"));
			}
			
			if(!json.isNull("pageInfo")){
				mPageInfo = new PageInfo(json.getJSONObject("pageInfo"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
}
