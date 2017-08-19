package com.skytnt.elfbox;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.skytnt.elfbox.elf.*;
import com.skytnt.elfbox.views.*;
import java.util.*;
import java.util.regex.*;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.widget.PopupMenu;

public class DisasmActivity extends BaseActivity
{
	public String path;
	ProgressBar pb;
	ImageView showmenu;
	PopupMenu menu;
    RelativeLayout rsyms;
	TextView loadingtext;
	ListView symlist;
	EditText symsearch;
	public TabView tabview;
	HexView vhex;
	DisasmView vAIDA;
	SymbolAdapter symad;
	dump dumper;
	public Vector<symbol>syms=new Vector<symbol>();
	int symnum;
    Thread loadsyms;
	MyHandler mhandler=new MyHandler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		path=this.getIntent().getExtras().getString("path");
		dumper=new dump(path);
		
		tb.setSubtitle(path.substring(path.lastIndexOf("/")+1));
		
		pb=new ProgressBar(this);
		pb.setX(width-(2*height)/15-20);
		pb.setY(height/20-height/30);
		mainlayout.addView(pb,height/15,height/15);
		
		showmenu=new ImageView(this);
		showmenu.setX(width-height/15-10);
		showmenu.setY(height/20-height/30);
		showmenu.setImageResource(R.drawable.menu);
		mainlayout.addView(showmenu,height/15,height/15);
		menu=new PopupMenu(self,showmenu);
		showmenu.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					Menu m=menu.getMenu();
					m.clear();
					m.add(0,0,0,"搜索");
					m.add(0,1,0,"跳转");
					menu.show();
				}
			});
		menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
				@Override
				public boolean onMenuItemClick(MenuItem p1)
				{
					switch(p1.getItemId()){
						case 0:
							AlertDialog.Builder d=new AlertDialog.Builder(self,R.style.Theme_AppCompat_Light_Dialog_Alert);
							final EditText kw=new EditText(self);
							kw.setHint("关键字");
							d.setTitle("搜索").setView(kw).setCancelable(false).setNegativeButton("确定", new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface p1, int p2)
									{
										tabview.setShowingView(0);
										symsearch.setText(kw.getText().toString());
									}
								});
							d.create().show();
							break;
							case 1:
							AlertDialog.Builder d1=new AlertDialog.Builder(self,R.style.Theme_AppCompat_Light_Dialog_Alert);
							final EditText addr=new EditText(self);
							addr.setHint("地址(16进制)");
							addr.addTextChangedListener(new TextWatcher(){
									String sold="";
									@Override
									public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
									{
										sold=p1.toString();
									}

									@Override
									public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
									{
										char[]replace=p1.toString().substring(p2,p2+p4).toCharArray();
										for(char r:replace){
											if(r!=0&&(r<'0'||r>'f')){
												addr.setText(sold);
												addr.setSelection(p2);
											}
										}
									}

									@Override
									public void afterTextChanged(Editable p1)
									{
										
									}
								});
							d1.setTitle("跳转").setView(addr).setCancelable(false).setNegativeButton("确定", new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface p1, int p2)
									{
									    if(!addr.getText().toString().equals("")){
										int address=Integer.parseInt(addr.getText().toString(),16);
										vhex.setChoose(address,1);
										vhex.scrollToLine(address/8);
										vhex.memLine=address/8;
										tabview.setShowingView(1);
										}
									}
								});
							d1.create().show();
							break;
						default:break;
					}
					return false;
				}
			});
			
			tabview=new TabView(self,width,height-height/10,height/15);
			tabview.setX(0);
			tabview.setY(height/10);
			mainlayout.addView(tabview,width,height-height/10);
			initTabs();
			initSyms();
	}
	
	void initTabs(){
		rsyms=new RelativeLayout(this);
		symlist=new ListView(this);
		symlist.setFastScrollEnabled(true);
		symlist.setOnItemClickListener(new AdapterView.OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					symbol sym=syms.get(symad.showing.get(p3));
					if(sym.type==2){
						int addr=sym.value;
						int s=sym.size;
						if(s==0){
							s=1;
						}
						String h=Utils.disassemble(addr,addr+1,path);
						Pattern p = Pattern.compile("(?<=\\+0x)[123456789abcdef]*(?=\\>\\:)");
						Matcher m = p.matcher(h);
						if(m.find()){
							addr-=Integer.parseInt(h.substring(m.start(),m.end()),16);
						}
						Pattern p_2 = Pattern.compile("(?<=\\-0x)[123456789abcdef]*(?=\\>\\:)");
						Matcher m_2 = p_2.matcher(h);
						if(m_2.find()){
							addr+=Integer.parseInt(h.substring(m_2.start(),m_2.end()),16);
						}
						
					vhex.setChoose(addr,s);
					vhex.scrollToLine(addr/8);
					vhex.memLine=addr/8;
					
					tabview.setShowingView(2);
					int id=symad.showing.get(p3);
					boolean added=false;
					for(int i:vAIDA.ids){
						if(i==id){added=true;}
					}
					if(added)
						{
							vAIDA.setShowingView(id);
						}else{
							vAIDA.addNewText(id,Utils.disassemble(addr,addr+s,path));
							vAIDA.setShowingView(id);
						}
					}
				}
			});
		symad=new SymbolAdapter(this);
		symlist.setAdapter(symad);
		symlist.setY(height/15);
		rsyms.addView(symlist,width,height-height/10-(height*2)/15);
		symsearch=new EditText(this);
		symsearch.setHint("搜索");
		symsearch.addTextChangedListener(new TextWatcher(){

				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{}
				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{}
				@Override
				public void afterTextChanged(Editable p1)
				{
					symad.showing.clear();
					for(int i=0;i<symnum;i++){
						if(syms.get(i).demangledname.contains(p1.toString())){
							symad.showing.add(i);
						}
					}
					symad.notifyDataSetChanged();
					symlist.invalidate();
				}
			});
		rsyms.addView(symsearch,width,height/15);
		symlist.setVisibility(View.GONE);
		symsearch.setVisibility(View.GONE);
		loadingtext=new TextView(self);
		loadingtext.setTextSize(15);
		rsyms.addView(loadingtext);
		rsyms.setGravity(Gravity.CENTER);
		tabview.addTab(0,"符号表",rsyms);
		
		
		RelativeLayout rHex=new RelativeLayout(this);
		vhex=new HexView(this,width,height-height/10-height/15,dumper);
		rHex.addView(vhex,width,height-height/10-height/15);
		tabview.addTab(1,"HEX视图",rHex);
		
		RelativeLayout rAIDA=new RelativeLayout(this);
		vAIDA=new DisasmView(this,width,height-height/10-height/15);
		rAIDA.addView(vAIDA,width,height-height/10-height/15);
		tabview.addTab(2,"反汇编视图",rAIDA);
		
	
		tabview.setShowingView(0);
	}
	
	void initSyms(){
		for(section sec:dumper.elf.sections){
			if(sec.type==2||sec.type==11){
				symnum+=dumper.getSymNum(sec);
			}
		}
		
		loadsyms=new Thread(new Runnable(){
				@Override
				public void run()
				{
					for(section sec:dumper.elf.sections){
						if(sec.type==2||sec.type==11){
							for(int i=0;i<dumper.getSymNum(sec);++i){
								Message msg=new Message();
								msg.what=0;
								msg.arg1=i;
								msg.obj=dumper.getSym(sec,i);
								mhandler.sendMessage(msg);
							}
						}
					}
					Message msg=new Message();
					msg.what=1;
					mhandler.sendMessage(msg);
				}
			});
			loadsyms.start();
	}
	
	class SymbolAdapter extends BaseAdapter
	{
		Context con;
		Vector<Integer>showing;
		
		SymbolAdapter(Context con){
			this.con=con;
			showing=new Vector<Integer>();
		}

		@Override
		public int getCount()
		{
			return showing.size();
		}

		@Override
		public Object getItem(int p1)
		{
			return syms.get(showing.get(p1));
		}

		@Override
		public long getItemId(int p1)
		{
			return showing.get(p1);
		}
		
		void addSym(symbol sym){
			syms.add(sym);
			symlist.invalidate();
		}
		
		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
			LinearLayout ml=new LinearLayout(con);
				symbol sym=syms.get(showing.get(p1));
				LinearLayout ml2=new LinearLayout(con);
				ml2.setOrientation(1);
				TextView index=new TextView(con);
				index.setText(showing.get(p1)+"");
				index.setTextSize(10);
				index.setTextColor(Color.GRAY);
				index.setGravity(Gravity.CENTER);
				TextView name=new TextView(con);
				name.setText(sym.demangledname);
				name.setTextSize(20);
				TextView info=new TextView(con);
				info.setText("bind:"+Tables.symbol_bind.get(sym.bind)+"  类型:"+Tables.symbol_type.get(sym.type)+"  值:"+Utils.i2hex(sym.value)+"  大小:"+sym.size);
				info.setTextSize(10);
				info.setTextColor(Color.GRAY);
				ImageView iv=new ImageView(con);
				if(sym.type==1){
					iv.setImageResource(R.drawable.obj);
				}
				if(sym.type==2){
					iv.setImageResource(R.drawable.func);
				}
				ml.addView(index,50,50);
				ml.addView(iv,50,50);
				ml.addView(ml2);
				ml2.addView(name);
				ml2.addView(info);
			return ml;
		}
	}
	
	class MyHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			int what=msg.what;
			if(what==0){
				syms.add((symbol)(msg.obj));
				symad.showing.add(msg.arg1);
				loadingtext.setText("加载中("+msg.arg1+"/"+symnum+")");
			}
			if(what==1){
				pb.setVisibility(View.GONE);
				rsyms.removeView(loadingtext);
				symsearch.setVisibility(View.VISIBLE);
				symlist.setVisibility(View.VISIBLE);
				rsyms.setGravity(Gravity.TOP);
				Snackbar.make(mainlayout,"Symbol加载完成",Snackbar.LENGTH_SHORT).show();
			    loadsyms=null;
			    //System.gc();
			}
		}
	}
}
