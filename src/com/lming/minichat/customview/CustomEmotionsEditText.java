package com.lming.minichat.customview;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lming.minichat.MainApplication;
import com.lming.minichat.R;

public class CustomEmotionsEditText extends EditText implements  
        OnFocusChangeListener, TextWatcher { 
	/**
	 * 删除按钮的引用
	 */
    private Drawable mEmotionsDrawable; 
    private Context mContext;
 
    public CustomEmotionsEditText(Context context) { 
    	this(context, null); 
    } 
 
    public CustomEmotionsEditText(Context context, AttributeSet attrs) { 
    	//这里构造方法也很重要，不加这个很多属性不能再XML里面定义
    	this(context, attrs, android.R.attr.editTextStyle); 
    } 
    
    public CustomEmotionsEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }
    
    private void init() { 
    	//获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
    	mEmotionsDrawable = getCompoundDrawables()[2]; 
        if (mEmotionsDrawable == null) { 
        	mEmotionsDrawable = getResources() 
                    .getDrawable(R.drawable.message_emotions_selector); 
        } 
        mEmotionsDrawable.setBounds(0, 0, mEmotionsDrawable.getIntrinsicWidth(), mEmotionsDrawable.getIntrinsicHeight()); 
        setClearIconVisible(true); 
        setOnFocusChangeListener(this); 
        addTextChangedListener(this); 
    } 
 
 
    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向没有考虑
     */
    @Override 
    public boolean onTouchEvent(MotionEvent event) { 
        if (getCompoundDrawables()[2] != null) { 
            if (event.getAction() == MotionEvent.ACTION_UP) { 
            	boolean touchable = event.getX() > (getWidth() 
                        - getPaddingRight() - mEmotionsDrawable.getIntrinsicWidth()) 
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) { 
                	if(!MainApplication.getInstance().isShowEmotion()){
                		CustomEmotionsEditText.this.setFocusable(false);
                		toggleSoftInput();
                	}else{
                		CustomEmotionsEditText.this.setFocusable(true);
                		CustomEmotionsEditText.this.setFocusableInTouchMode(true);
                	}
                	emotionsClickListener.emotionsClick(this);
                } else{
                	CustomEmotionsEditText.this.setFocusable(true);
            		CustomEmotionsEditText.this.setFocusableInTouchMode(true);
                }
            } 
        } 
 
        return super.onTouchEvent(event); 
    } 
 
    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override 
    public void onFocusChange(View v, boolean hasFocus) { 
    	setClearIconVisible(true);
    	if(!hasFocus){
    		CustomEmotionsEditText.this.setFocusable(false);
    		toggleSoftInput();
    	}
    	System.out.println("hasFocus:" + hasFocus);
    } 
 
 
    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) { 
        Drawable right = visible ? mEmotionsDrawable : null; 
        setCompoundDrawables(getCompoundDrawables()[0], 
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]); 
    } 
     
    
    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override 
    public void onTextChanged(CharSequence s, int start, int count, 
            int after) { 
    } 
 
    @Override 
    public void beforeTextChanged(CharSequence s, int start, int count, 
            int after) { 
         
    } 
 
    @Override 
    public void afterTextChanged(Editable s) { 
         
    } 
    
   
    /**
     * 设置晃动动画
     */
    public void setShakeAnimation(){
    	this.setAnimation(shakeAnimation(5));
    }
    
    
    /**
     * 晃动动画
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts){
    	Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
    	translateAnimation.setInterpolator(new CycleInterpolator(counts));
    	translateAnimation.setDuration(1000);
    	return translateAnimation;
    }
 
    //回调Emotions点击事件
    public EmotionsClickListener emotionsClickListener;
    public void setEmotionsClickListener(EmotionsClickListener emotionsClickListener){
    	this.emotionsClickListener = emotionsClickListener;
    }
    public interface EmotionsClickListener{
    	public void emotionsClick(View v);
    }
    private void toggleSoftInput(){
    	// 隐藏输入法
    	InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
   
    	if(imm.isActive()){
    		imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
    	}
    }
}
