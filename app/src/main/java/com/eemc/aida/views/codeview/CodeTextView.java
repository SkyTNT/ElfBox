package com.eemc.aida.views.codeview;
import android.content.*;
import android.graphics.*;
import android.view.*;
import java.util.*;
import java.util.regex.*;
import android.widget.*;
import android.os.*;
import android.util.*;

public class CodeTextView extends View
{
    Context con;
    String[] codes;
    public int width,height,textsize,wheight;
    Paint p=new Paint();
    int linenum;
    public int showpos;
    public CodeTextView(Context con){
	super(con);
	this.con=con;
	textsize=30;
	p.setTextSize(textsize);
	//p.setFakeBoldText(true);
	p.setTypeface(Typeface.createFromAsset(con.getAssets(),"front.ttf"));
    }

    public void setCode(String c){
	codes=c.split("\n");
	invalidate();
    }
    
    public void setTextSize(int ts){
	textsize=ts;
	p.setTextSize(textsize);
	invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
	linenum=codes.length;
	height=linenum*textsize;
	for(String c:codes){
	    int w=(int)p.measureText(c);
	    if(w>width){
		width=w;
	    }
	}
	setMeasuredDimension(width+100,height+100);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
	super.onDraw(canvas);
	canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
	for(int i=0;i<codes.length;i++){
	    if(i>=showpos/textsize&&i<=(showpos/textsize)+wheight/textsize){//只显示在屏内的内容
	    String c=codes[i];
	    p.setColor(0xff0000f0);
	    canvas.drawText(c,0,(i+1)*textsize,p);
	    setTextHighlight(canvas,i,c,"(?<=, )(a[1234]|r(0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15)|sl|fp|ip|sp|lr|pc|WR|SB|SL|FP|IP|SP|LR|PC)\\b",0xff217ecc);
	    setTextHighlight(canvas,i,c,"(a[1234]|r(0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15)|sl|fp|ip|sp|lr|pc|WR|SB|SL|FP|IP|SP|LR|PC)(?=[,\\}\\]\\!])",0xff217ecc);
	    setTextHighlight(canvas,i,c,"<.*>",0xffaa00ff);
	    setTextHighlight(canvas,i,c,"0x[0123456789abcdef]*",0xffff9435);
	    setTextHighlight(canvas,i,c,"#[0123456789+-]*",0xffff9435);
	    setTextHighlight(canvas,i,c,"(?<=\\b)[0123456789abcdef]*(?=(\\:)|( <.*>))",0xffff9435);
	    setTextHighlight(canvas,i,c,"[\\{\\}\\[\\]\\(\\)\\:\\.,;]",0xff9999ff);
	    setTextHighlight(canvas,i,c,"[\\+\\-\\!\\&\\*]",0xffe05020);
	    }
	}
    }
    
    void setTextHighlight(Canvas can,int line,String basetext,String matchStr,int color) {
	int start = 0;
	int end = 0;
	if (matchStr != null) {
	    Pattern pa = Pattern.compile(matchStr);
	    Matcher m = pa.matcher(basetext);
	    while (m.find()) {
		start = m.start();
		end = m.end();
		p.setColor(color);
		can.drawText(basetext.substring(start,end),p.measureText(basetext,0,start),(line+1)*textsize,p);
		p.setColor(0xff0000f0);
	    }
	}
    }
}
