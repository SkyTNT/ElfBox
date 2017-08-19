package com.skytnt.elfbox;

import android.content.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public final class Utils {
	static String mainPath="/sdcard/AIDA";
	public static int endian=1;
	public static String b2hex(byte[] bytes){
		String result="";
		for(byte b:bytes){
			if(endian==1){
				result=b2hex(b)+result;
				}else if(endian==2){
					result+=b2hex(b);
				}
		}
		return result;
	}
	
	public static String b2hex(byte b){
		String result="";
			if(b>=0){
				result=Integer.toHexString(b);
			}else{
				result=Integer.toHexString(2*128+b);
			}
			if(result.length()<2){
				result="0"+result;
			}
		return result;
	}
	
	
	public static String i2hex(int value){
		String base=Integer.toHexString(value);
		String result=base;
		for(int i=0;i<(8-base.length());i++){
			result="0"+result;
		}
		result="0x"+result;
		return result;
	}
	
	public static boolean saveFile(String fileName, byte[] arys){
		File file = new File(fileName);
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(file);
			fos.write(arys);
			fos.flush();
			return true;
		}catch(Exception e){
			System.out.println("save file error:"+e.toString());
		}finally{
			if(fos != null){
				try{
					fos.close();
				}catch(Exception e){
					System.out.println("close file error:"+e.toString());
				}
			}
		}
		return false;
	}
	
	public static byte[] readFile(String fileName){
		try{
		File file = new File(fileName);
		FileInputStream fis=new FileInputStream(file);
		byte b[]=new byte[(int)file.length()];
		fis.read(b);
		fis.close();
		return b;
		}catch(Exception e){
			System.out.println(e);
		}
		return null;
	}
	
	public static byte[] cp(byte[] res, int start, int count){
		if(res == null){
			return null;
		}
		byte[] result = new byte[count];
		for(int i=0;i<count;i++){
			result[i] = res[start+i];
		}
		return result;
	}
	
	public static int b2i(byte[] src) {
		return Integer.parseInt(b2hex(src),16);
	}   
	static public int cb2i(byte[] res, int start, int count){
		return b2i(cp(res,start,count));
	}
	
	public static long b2l(byte[] src) {
		return Long.parseLong(b2hex(src),16);
	}   
	static public long cb2l(byte[] res, int start, int count){
		return b2l(cp(res,start,count));
	}
	
	static public String disassemble(int start_addr,int stop_addr,String filePath){
		try
		{
		    Runtime.getRuntime().exec("chmod 751 /data/data/com.skytnt.elfbox/files/objdump");
		    String start_addr_s=Integer.toHexString(start_addr);
		    BufferedReader br=new BufferedReader(new InputStreamReader( Runtime.getRuntime().exec("/data/data/com.skytnt.elfbox/files/objdump -S -C --start-address=0x"+start_addr_s+" --stop-address=0x"+Integer.toHexString(stop_addr)+" "+filePath).getInputStream()));
		    String result="";
		    String line;
		    boolean start=false;
		    while((line=br.readLine())!=null){
			if(line.contains(start_addr_s)){start=true;}
			if(start){
			    result+=line+"\n";
			}
		    }
			return result;
		}
		catch (Throwable e)
		{
			return e.toString();
		}
	}
	
	public static native String demangle(String name);
}
