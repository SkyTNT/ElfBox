package com.eemc.aida.views.codeview;

import android.content.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import java.util.regex.*;
import java.util.*;

public class CodeView extends RelativeLayout
{
    CodeTextView ctv;
    Context con;
    public CodeView(Context con){
	super(con);
	this.con=con;
	
	ScrollView sv=new ScrollView(con){
	    @Override
	    protected void onScrollChanged(int l, int t, int oldl, int oldt)
	    {
		super.onScrollChanged(l, t, oldl, oldt);
		if(Math.abs(ctv.showpos-t)>ctv.textsize){//只有当滑动距离大于一行的高度才更新;
		ctv.showpos=t;
		ctv.invalidate();
		}
	    }
	};
	HorizontalScrollView hsv=new HorizontalScrollView(con);
	ctv=new CodeTextView(con);
	hsv.addView(ctv);
	sv.addView(hsv);
	
	addView(sv);
	
	}
    
    public void setCode(String c){
	ctv.setCode(c);
    }
  
    public void setTextSize(int ts){
	ctv.setTextSize(ts);
    }
    public void setHeight(int height){
	ctv.wheight=height;
    }
}
