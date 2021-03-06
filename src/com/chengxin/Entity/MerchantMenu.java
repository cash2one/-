package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class MerchantMenu implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int id;
	public String logo;
	public String name;
	public int vieworder;
	private static List<MerchantMenu> menuList = new ArrayList<MerchantMenu>();
	
	public static boolean hasData() {
		return getMenuList().size() > 0;
	}
	
	public MerchantMenu(int id, String logo, String name) {
		super();
		this.id = id;
		this.logo = logo;
		this.name = name;
	}

	public MerchantMenu() {
		super();
	}

	public MerchantMenu(String reqString) {
		super();
		if(reqString == null || reqString.equals("")){
			return;
		}
		
		try {
			JSONObject obj = new JSONObject(reqString);
			
			if(!obj.isNull("state")){
				WeiYuanState state = new WeiYuanState(obj.getJSONObject("state"));
			
				if(state != null && state.code == 0){
					if(!obj.isNull("data")){
						String dataString = obj.getString("data");
						
						if((dataString != null && !dataString.equals("")) && dataString.startsWith("[")) {
							JSONArray array = obj.getJSONArray("data");
							menuList.clear();

							if(array != null && array.length() >0){
								for (int i = 0; i < array.length(); i++) {
									menuList.add(new MerchantMenu(array.getJSONObject(i)));
								}
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private MerchantMenu(JSONObject obj) throws JSONException{
		if(obj == null  || obj.equals("")){
			return;
		}
		id = obj.getInt("id");
		logo = obj.getString("logo");
		name = obj.getString("name");
		vieworder = obj.getInt("vieworder");
	}

	/**
	 * @return the menuList
	 */
	public static List<MerchantMenu> getMenuList() {
		return menuList;
	}
	
	public static String getNameById(int id) {
		if (menuList.size() > 0) {
			for (int i = 0; i < menuList.size(); i++) {
				MerchantMenu item = menuList.get(i);
				
				if (item.id == id) {
					return item.name;
				}
			}
		}
		
		return null;
	}

	public static MerchantMenu getItemById(int id) {
		if (menuList.size() > 0) {
			for (int i = 0; i < menuList.size(); i++) {
				MerchantMenu item = menuList.get(i);
				
				if (item.id == id) {
					return item;
				}
			}
		}
		
		return null;
	}

	public static int getIdByName(String name) {
		if (menuList.size() > 0) {
			for (int i = 0; i < menuList.size(); i++) {
				MerchantMenu item = menuList.get(i);
				
				if (item.name.equals(name)) {
					return item.id;
				}
			}
		}
		
		return 0;
	}

	public static MerchantMenu getItemByName(String name) {
		if (menuList.size() > 0) {
			for (int i = 0; i < menuList.size(); i++) {
				MerchantMenu item = menuList.get(i);
				
				if (item.name.equals(name)) {
					return item;
				}
			}
		}
		
		return null;
	}

	public static List<PopItem> getPopMenuList() {
		List<PopItem> popList = new ArrayList<PopItem>();

		PopItem pop = new PopItem(0, "全部商品", "category00");
		popList.add(pop);
		
		for (int i = 0; i < menuList.size(); i++) {
			MerchantMenu item = menuList.get(i);
			pop = new PopItem(item.id, item.name, String.format("category%02d", item.id));
			popList.add(pop);
		}
		
		return popList;
	}
}
