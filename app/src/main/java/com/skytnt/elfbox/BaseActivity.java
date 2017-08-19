package com.skytnt.elfbox;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;

import android.support.v7.widget.Toolbar;

public class BaseActivity extends Activity
{
    RelativeLayout mainlayout;
    Toolbar tb;
    Activity self=this;
    int width,height,sbheight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	mainlayout=new RelativeLayout(this);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	WindowManager wm =(WindowManager)getSystemService(Context.WINDOW_SERVICE);
	width = wm.getDefaultDisplay().getWidth();
	height = wm.getDefaultDisplay().getHeight();
	int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
	if (resourceId > 0) {
	    sbheight=getResources().getDimensionPixelSize(resourceId);
	    height-=sbheight;
	}
	setContentView(mainlayout);
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	    mainlayout.setFitsSystemWindows(true);
	    ViewGroup contentLayout = (ViewGroup)findViewById(android.R.id.content);
	    View statusBarView = new View(this);
	    contentLayout.addView(statusBarView,width,sbheight);
	    statusBarView.setBackgroundColor(0xff1e88e5);
        }


	tb=new Toolbar(this);
	tb.setTitle(R.string.app_name);
	tb.setTitleTextColor(Color.WHITE);
	tb.setBackgroundColor(0xff1e88e5);
	mainlayout.addView(tb,width,height/10);
    }
}
