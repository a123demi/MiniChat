package com.lming.minichat;

import com.lming.minichat.util.FileUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

public class MessagePreviewActivity extends BaseActivity {
	public ImageView msgImagePreviewIv;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_image_preview_activity);
		findById();
		initData();
	}
	
	@Override
	public void findById(){
		msgImagePreviewIv = (ImageView)this.findViewById(R.id.msg_img_preview_iv);
	}
	
	private void initData(){
		Intent intent = this.getIntent();
		String url = intent.getStringExtra("imageUrl");
		if(url != null && !TextUtils.isEmpty(url)){
			msgImagePreviewIv.setImageBitmap(FileUtils.getBitmap(url));
		}
	}
}
