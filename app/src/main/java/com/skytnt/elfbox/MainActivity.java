package com.skytnt.elfbox;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.io.*;
import org.json.*;

import android.support.v7.widget.Toolbar;
import com.skytnt.elfbox.views.*;

public class MainActivity extends Activity
{
	RelativeLayout mainlayout;
	LinearLayout plist;
	JSONObject projects;
	Activity self=this;
	int width,height,sbheight;
    @Override
    public void onCreate(Bundle savedInstanceState)
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
		
		try
		{
			initFiles();
		}
		catch (Exception e)
		{
			try
			{
				projects = new JSONObject("{\"num\":0}");
			}
			catch (JSONException e2)
			{}
			Toast.makeText(this, "" + e, Toast.LENGTH_LONG).show();
		}
		Toolbar tb=new Toolbar(this);
		tb.setTitle(R.string.app_name);
		tb.setSubtitle("工程");
		tb.setTitleTextColor(Color.WHITE);
		tb.setBackgroundColor(0xff1e88e5);
		mainlayout.addView(tb,width,height/10);
		
		ScrollView plv=new ScrollView(this);
		plv.setY(height/10);
		mainlayout.addView(plv,width,height-height/10);
		plist=new LinearLayout(this);
		plist.setOrientation(1);
		
		plv.addView(plist);
		try
		{
		    JSONArray ns=projects.names();
			for (int i=0;i<ns.length();i++)
			{
			    if(((String)ns.get(i)).equals("num")){
				continue;
			    }
			    ProjectButton pj=new ProjectButton(self,projects,Integer.valueOf((String)ns.get(i)),width,height/10);
			    plist.addView(pj,width,height/10);
			}
		}
		catch (Exception e)
		{
		    Toast.makeText(this, "" + e, Toast.LENGTH_LONG).show();
		}
		final FloatingActionButton newpj=new FloatingActionButton(this);
		newpj.setImageResource(R.drawable.newproject);
	    newpj.setBackgroundTintList(ColorStateList.valueOf(0xff1e88e5));
		newpj.setX(width-height/10-10);
		newpj.setY(height-height/10-10);
		newpj.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					final FileChooser fc=new FileChooser(self,"/sdcard");
					fc.setOnFiniEve(new Runnable(){
							@Override
							public void run()
							{
								try
								{
								    JSONArray ns=projects.names();
									for (int i=0;i<ns.length();i++)
									{
									    if(((String)ns.get(i)).equals("num")){
										continue;
									    }
										if(projects.getString((String)ns.get(i)).equals(fc.chose.getPath())){
										    Snackbar.make(mainlayout,"错误,你已添加过了",Snackbar.LENGTH_SHORT).show();
											return;
										}
									}
									FileInputStream fis=new FileInputStream(fc.chose);
									byte b[]=new byte[4];
									fis.read(b);
									fis.close();
									if(b[0]==0x7f&&b[1]==0x45&&b[2]==0x4c&&b[3]==0x46){//判断文件头
									projects.put(""+Integer.valueOf(projects.getString("num")),fc.chose.getPath());
									ProjectButton pj=new ProjectButton(self,projects,Integer.valueOf(projects.getString("num")),width,height/10);
									plist.addView(pj,width,height/10);
									projects.put("num",(Integer.valueOf(projects.getString("num"))+1)+"");
									}else{
									    Snackbar.make(mainlayout,"错误,该文件不是有效的elf文件",Snackbar.LENGTH_SHORT).show();
									}
								}
								catch (Exception e)
								{
								}
							}
						});
					fc.start();
				}
			});
		mainlayout.addView(newpj,height/10,height/10);
    }
	
	void initFiles() throws Exception{
		copyBin();
		File m=new File(Utils.mainPath);
		if(!m.exists()){
			m.mkdir();
		}
		File pjs=new File(Utils.mainPath+"/projects");
		if(!pjs.exists()){
			pjs.createNewFile();
		}
		String str=new String(Utils.readFile(Utils.mainPath+"/projects"));
		
		projects=new JSONObject(str);
	}
	
	void copyBin(){
		try
		{
			byte[]b=new byte[1457080];
			InputStream in=getAssets().open("objdump");
			in.read(b);
			in.close();
			OutputStream out=openFileOutput("objdump",MODE_WORLD_WRITEABLE|MODE_WORLD_READABLE);
			out.write(b);
			out.close();
		}
		catch (Exception e)
		{
			Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
		}
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Utils.saveFile(Utils.mainPath+"/projects",projects.toString().getBytes());
	}
}
