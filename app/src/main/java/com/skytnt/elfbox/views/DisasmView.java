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
import android.content.*;

public class DisasmView extends TabView
{
	HashMap<Integer,CodeView> tvs=new HashMap<Integer,CodeView>();
	
	public DisasmView(Context c,int width,int height){
		super(c,width,height,height/20);
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
