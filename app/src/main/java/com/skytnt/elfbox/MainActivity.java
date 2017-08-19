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

public class MainActivity extends BaseActivity
{
	LinearLayout plist;
	public static JSONObject projects;
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		
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
		tb.setSubtitle("工程");
		
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
				    Intent intent=new Intent(p1.getContext(),FileChooserActivity.class);
				    self.startActivityForResult(intent,0);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    try{
	    super.onActivityResult(requestCode, resultCode, data);
	    projects.put(""+Integer.valueOf(projects.getString("num")),data.getExtras().getString("path"));
	    ProjectButton pj=new ProjectButton(self,projects,Integer.valueOf(projects.getString("num")),width,height/10);
	    plist.addView(pj,width,height/10);
	    projects.put("num",(Integer.valueOf(projects.getString("num"))+1)+"");
	    
	    }catch(Exception e){
		
	    }
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Utils.saveFile(Utils.mainPath+"/projects",projects.toString().getBytes());
	}
}
