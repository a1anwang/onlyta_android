package com.a1anwang.onlyta.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class AuthButton extends Button{

	private final int  countDownTime=60;
	
	
	private int nowtimecount;
	
	private int normal_text_color;
	
	private int normal_bg_color;
	
	private String  normal_text;
	
	
	private int countdown_text_color;
	private int countdown_bg_color;
	
	
	
	public AuthButton(Context context) {
		super(context);
		 
	}
	
	 
	
	
	public AuthButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}




	public AuthButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}




	public void setNormalStyle(int bgcolor,int textcolor,String text){
		
		this.normal_bg_color=bgcolor;
		
		this.normal_text_color=textcolor;
		
		this.normal_text=text;
		
		
	}
 
	public void setCountdownStyle(int bgcolor,int textcolor ){
		this.countdown_bg_color=bgcolor;
		
		this.countdown_text_color=textcolor;
	}
	
	
	
	public void setToNormal(){
		setBackgroundColor(normal_bg_color);
		setTextColor (normal_text_color);
		
		setText(normal_text);
		
		setEnabled(true);
		
		removeCallbacks(runnable);
	}
 
	public void setToCountdown(){
		setBackgroundColor(countdown_bg_color);
		
		setTextColor (countdown_text_color);
		
		setEnabled(false);
		removeCallbacks(runnable);
		
		nowtimecount=countDownTime;
		
		post(runnable);
	}
	
	
	private Runnable runnable=new Runnable() {
		@Override
		public void run() {
			setText(nowtimecount+"秒");
			nowtimecount--;
			if(nowtimecount>=0){
				postDelayed(runnable, 1000);
			}else{
				setToNormal();
			}
		}
	};
}
