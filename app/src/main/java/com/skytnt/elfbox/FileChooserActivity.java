package com.skytnt.elfbox;
import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import org.json.*;
import android.util.*;

public class FileChooserActivity extends BaseActivity
{
    ListView lv;
    JSONObject projects;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	try
	{
	    projects = new JSONObject(new String(Utils.readFile(Utils.mainPath + "/projects")));
	}
	catch (Exception e)
	{}

	tb.setSubtitle("文件选择");
	
	lv=new ListView(this);
	final FileAdapter fa=new FileAdapter(this,"/sdcard");
	lv.setAdapter(fa);
	lv.setY(height/10);
	lv.setFastScrollEnabled(true);
	mainlayout.addView(lv,width,height-height/10);
	
	lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
		{
		    if(p3!=0){
			if(fa.fl[p3-1].isDirectory()){
			    fa.setPath(fa.fl[p3-1].getPath());
			}else{
			    String path=fa.fl[p3-1].getPath();
			    try
			    {
				JSONArray ns=projects.names();
				for (int i=0;i<ns.length();i++)
				{
				    if(((String)ns.get(i)).equals("num")){
					continue;
				    }
				    if(projects.getString((String)ns.get(i)).equals(path)){
					Snackbar.make(mainlayout,"错误,你已添加过了",Snackbar.LENGTH_SHORT).show();
					return;
				    }
				}
				FileInputStream fis=new FileInputStream(fa.fl[p3-1]);
				byte b[]=new byte[4];
				fis.read(b);
				fis.close();
				if(b[0]==0x7f&&b[1]==0x45&&b[2]==0x4c&&b[3]==0x46){//判断文件头
				    Intent data=new Intent();
				    data.putExtra("path",path);
				    setResult(0,data);
				    self.finish();
				}else{
				    Snackbar.make(mainlayout,"错误,该文件不是有效的elf文件",Snackbar.LENGTH_SHORT).show();
				}
			    }
			    catch (Exception e)
			    {
				Toast.makeText(self,""+Log.getStackTraceString(e),Toast.LENGTH_LONG).show();
			    }
			}
		    }else{
			String path=fa.path.substring(0,fa.path.lastIndexOf("/"));
			if(path.equals("")){
			    path="/";
			}
			fa.setPath(path);
		    }
		}
	    });
    }
    
    class FileAdapter extends BaseAdapter
    {
	public String path;
	Context con;
	public File[]fl;
	Comparator<File> mc=new Comparator<File>() {
	    @Override
	    public int compare(File o1, File o2) {
		if (o1.isDirectory() && o2.isFile())
		    return -1;
		if (o1.isFile() && o2.isDirectory())
		    return 1;
		return o1.getName().compareTo(o2.getName());
	    }
	};
	
	FileAdapter(Context con,String path){
	    this.con=con;
	    this.path=path;
	    Vector<File> vf=new Vector<File>();
	    File[] flielist=new File(path).listFiles();
	    for(File f:flielist){
		if(f.canRead())vf.add(f);
	    }
	    Collections.sort(vf,mc);
	    fl=new File[vf.size()];
	    for(int i=0;i<vf.size();i++){
		fl[i]=vf.get(i);
	    }
	}
	
	public void setPath(String path){
	    this.path=path;
	    this.path=path;
	    Vector<File> vf=new Vector<File>();
	    File[] flielist=new File(path).listFiles();
	    for(File f:flielist){
		if(f.canRead())vf.add(f);
	    }
	    Collections.sort(vf,mc);
	    fl=new File[vf.size()];
	    for(int i=0;i<vf.size();i++){
		fl[i]=vf.get(i);
	    }
	    notifyDataSetChanged();
	}
	
	@Override
	public int getCount()
	{
	    return fl.length+1;
	}

	@Override
	public Object getItem(int p1)
	{
	    return fl[p1-1];
	}

	@Override
	public long getItemId(int p1)
	{

	    return p1-1;
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3)
	{
	    LinearLayout ml=new LinearLayout(con);
	    ImageView iv=new ImageView(con);
	    TextView tv=new TextView(con);
	    if(p1!=0){
		if(fl[p1-1].isFile()){
		    iv.setImageResource(R.drawable.file);
		}else{
		    iv.setImageResource(R.drawable.folder);
		}
		tv.setText(fl[p1-1].getName());
	    }else{
		iv.setImageResource(R.drawable.folder);
		tv.setText("..");
	    }
	    ml.addView(iv,60,60);
	    ml.addView(tv);
	    return ml;
	}
    }
}
