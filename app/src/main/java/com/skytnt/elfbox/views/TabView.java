package com.skytnt.elfbox.views;
import android.content.*;
import android.graphics.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class TabView extends RelativeLayout
{
	Context con;
	int width,height,tabheight;
	TabLayout tl;
	MyAdapter ad;
    ViewPager vp;
    HashMap<Integer,View>vmap=new HashMap<Integer,View>();
    HashMap<Integer,String>namemap=new HashMap<Integer,String>();
    public Vector<Integer>ids=new Vector<Integer>();
    
    public TabView(Context con,int width,int height,int tabheight){
	super(con);
	this.con=con;
	this.width=width;
	this.height=height;
	this.tabheight=tabheight;
	tl=new TabLayout(con);
	tl.setBackgroundColor(0xff1e88e5);
	tl.setSelectedTabIndicatorColor(Color.BLUE);
	addView(tl,width,tabheight);
	ad=new MyAdapter();
	vp=new ViewPager(con);
	vp.setAdapter(ad);
	vp.setY(tabheight);
	tl.setupWithViewPager(vp);
	addView(vp,width,height-tabheight);
	
	}
	
	/*public void setTabClor(int tabcolor,int textcolor){
	}*/
	
	public void addTab(int id,String name,View v){
	    
		ids.add(id);
		namemap.put(id,name);
		vmap.put(id,v);
		ad.notifyDataSetChanged();
		}
	public void setShowingView(int id){
	    vp.setCurrentItem(ids.indexOf(id));
	}
	public void tabRename(int id,String name){
		
	}
	
    class MyAdapter extends PagerAdapter
    {

	@Override
	public int getCount()
	{
	    
	    return ids.size();
	}

	@Override
	public boolean isViewFromObject(View p1, Object p2)
	{
	    
	    return p1==p2;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
	    
	    View v=vmap.get(ids.get(position));
	    container.addView(v,width,height-tabheight);
	    return v;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
	    
	    container.removeView(vmap.get(ids.get(position)));
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
	    return namemap.get(ids.get(position));
	}
       
   }
}
