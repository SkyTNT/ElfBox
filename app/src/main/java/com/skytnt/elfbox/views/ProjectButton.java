package com.skytnt.elfbox.views;
import android.animation.*;
import android.content.*;
import android.graphics.*;
import android.support.design.widget.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.skytnt.elfbox.*;
import java.io.*;
import org.json.*;
import com.skytnt.elfbox.elf.*;

public class ProjectButton extends RelativeLayout
{
    ProjectButton self=this;
    JSONObject pjs;
    CardView cv;
    RelativeLayout cvl;
    ImageView down;
    String path,info;
    int id,width,height;
    public ProjectButton(Context con,JSONObject pjs,int id,int width,final int height){
	super(con);
	this.pjs=pjs;
	this.id=id;
	try{
	this.path=pjs.getString(id+"");
	}catch(Exception e){
	}
	this.width=width;
	this.height=height;
	
	cv=new CardView(con);
	cvl=new RelativeLayout(con);
	ImageView icon=new ImageView(con);
	TextView name=new TextView(con);
	cv.setRadius(16);
	cv.setCardElevation(10);
	cv.setX(10);
	cv.setY(10);
	cv.setCardBackgroundColor(Color.WHITE);
	icon.setImageResource(R.drawable.file_so);
	cvl.addView(icon,height-20,height-20);
	name.setX(height-20);
	name.setText(path.substring(path.lastIndexOf("/")+1));
	name.setTextColor(Color.BLACK);
	name.setGravity(Gravity.CENTER|Gravity.LEFT);
	cvl.addView(name,width-height-(height-20)/3-40,height-20);
	cv.addView(cvl,width-20,height-20);
	addView(cv,width-20,height-20);

	down=new ImageView(con);
	down.setImageResource(R.drawable.down);
	down.setX(width-40-(height-20)/3);
	down.setY((height-20)*1/3);
	cvl.addView(down,(height-20)/3,(height-20)/3);
	
	final TextView enter=new TextView(con);
	enter.setX(0);
	enter.setY(height);
	enter.setGravity(Gravity.CENTER);
	enter.setText("进入");
	enter.setTextColor(Color.BLACK);
	final TextView sinfo=new TextView(con);
	sinfo.setX((width-20)/3);
	sinfo.setY(height);
	sinfo.setText("信息");
	sinfo.setGravity(Gravity.CENTER);
	sinfo.setTextColor(Color.BLACK);
	final TextView del=new TextView(con);
	del.setX((width-20)*2/3);
	del.setY(height);
	del.setText("删除");
	del.setTextColor(Color.RED);
	del.setGravity(Gravity.CENTER);
	cvl.addView(enter,(width-20)/3,height-20);
	cvl.addView(sinfo,(width-20)/3,height-20);
	cvl.addView(del,(width-20)/3,height-20);
	OnClickListener ml=new OnClickListener(){
		@Override
		public void onClick(View p1)
		{
		    if(p1==cv){
			float r=down.getRotation();
			if(r==0){
			    startAnimator(0);
			}else if(r==180){
			    startAnimator(1);
			}
		    }else if(p1==enter){
			Intent intent=new Intent(p1.getContext(),DisasmActivity.class);
			intent.putExtra("path",path);
			p1.getContext().startActivity(intent);
		    }else if(p1==del){
			delete();
		    }else if(p1==sinfo){
			showInfo();
		    }
		}
	    };
	 cv.setOnClickListener(ml);
	 enter.setOnClickListener(ml);
	sinfo.setOnClickListener(ml);
	del.setOnClickListener(ml);
	
	byte bs[]=new byte[52];
	try{
	File file = new File(path);
	FileInputStream fis=new FileInputStream(file);
	fis.read(bs);
	fis.close();
	header h=new header();
	h.ident = Utils.cp(bs, 0, 16);
	Utils.endian=h.ident[5];
	h.type =Utils.cb2i(bs, 16, 2);
	h.machine = Utils.cb2i(bs, 18, 2);
	h.version = Utils.cb2i(bs, 20, 4);
	h.entry = Utils.cb2i(bs, 24, 4);
	h.phoff = Utils.cb2i(bs, 28, 4);
	h.shoff = Utils.cb2i(bs, 32, 4);
	h.flags = Utils.cb2i(bs, 36, 4);
	h.ehsize = Utils.cb2i(bs, 40, 2);
	h.phentsize = Utils.cb2i(bs, 42, 2);
	h.phnum = Utils.cb2i(bs, 44,2);
	h.shentsize = Utils.cb2i(bs, 46,2);
	h.shnum = Utils.cb2i(bs, 48, 2);
	h.shstrndx = Utils.cb2i(bs, 50, 2);
	info="endian:"+Tables.endian.get((int)h.ident[5])
	    +"\ntype:"+Tables.type.get(h.type)
	    +"\nversion:"+Tables.version.get(h.version)
	    +"\nentry:"+h.entry
	    +"\nprogram headers offset:"+Utils.i2hex(h.phoff)
	    +"\nprogram headers num:"+h.phnum
	    +"\nsection headers offset:"+Utils.i2hex(h.shoff)
	    +"\nsection headers num:"+h.shnum
	    +"\nehsize:"+h.ehsize
	    +"\nphentsize:"+h.phentsize
	    +"\nshentsize:"+h.shentsize
	    +"\nshstrndx:"+h.shstrndx
	    +"\n\n\n";
	}catch(Exception e){
	}
	
	}
    
	void showInfo(){
	    
	  BottomSheetDialog sheetDialog=new BottomSheetDialog(getContext());
	  TextView tv=new TextView(getContext());
	  tv.setText(info);
	  tv.setTextSize(20);
	  sheetDialog.setContentView(tv);
	  sheetDialog.show();
	}
	
	
    public void delete(){
	pjs.remove(id+"");
	((LinearLayout)getParent()).removeView(this);
    }
	
    public void startAnimator(int mode){
	float r_s=0,r_e=0;
	int h_s=0,h_e=0;
	
	if(mode==0){
	    r_s=0;
	    r_e=180;
	    h_s=height;
	    h_e=height*2;
	}else if(mode==1){
	    r_e=0;
	    r_s=180;
	    h_e=height;
	    h_s=height*2;
	}
	ObjectAnimator rotation = ObjectAnimator.ofFloat(down, "rotation", r_s, r_e);
	rotation.setDuration(300);
	rotation.start();
	ValueAnimator ha=ValueAnimator.ofInt(h_s,h_e);
	ha.setDuration(300);
	ha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
		@Override
		public void onAnimationUpdate(ValueAnimator p2)
		{
		    int h=p2.getAnimatedValue();
		    self.getLayoutParams().height=h;
		    cv.getLayoutParams().height=h-20;
		    cvl.getLayoutParams().height=h-20;
		    self.setLayoutParams(self.getLayoutParams());
		    cv.setLayoutParams(cv.getLayoutParams());
		    cvl.setLayoutParams(cvl.getLayoutParams());
		}
	  });
	  ha.start();
    }

}
