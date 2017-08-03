package com.skytnt.elfbox.views;
import android.graphics.*;
import android.text.*;
import android.text.style.*;
import android.view.*;
import android.widget.*;
import com.skytnt.elfbox.*;
import com.skytnt.elfbox.elf.*;
import com.skytnt.elfbox.views.codeview.*;
import java.util.*;
import java.util.regex.*;

public class AIDAView extends TabView
{
	AIDAActivity aac;
	static String regnames="(a[1234]|r(0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15)|sl|fp|ip|sp|lr|pc|WR|SB|SL|FP|IP|SP|LR|PC)";
	HashMap<Integer,CodeView> tvs=new HashMap<Integer,CodeView>();
	
	public AIDAView(AIDAActivity aac,int width,int height){
		super(aac,width,height,height/20);
		this.aac=aac;
	}
	public void addNewText(final int id,final String text){
	    
	    CodeView tv=new CodeView(con);
		addTab(id,"符号"+id,tv);
		tvs.put(id,tv);
	    tv.setCode(text);
	    tv.setHeight(height-height/20);
	    tv.setTextSize((height-height/20)/30);
	}
}
