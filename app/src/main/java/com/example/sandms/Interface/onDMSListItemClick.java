package com.example.sandms.Interface;

import org.json.JSONArray;
import org.json.JSONObject;

public interface onDMSListItemClick {
    default void onClick(JSONObject item){};
    default void onClick(JSONObject item,Integer ParentPosition){};
    default void showUOM(JSONArray items,String BUOM,Integer parentPos){}
}
