package com.lming.minichat;

import java.io.File;
import java.util.Calendar;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.lming.minichat.util.DateUtil;
import com.lming.minichat.util.ToastUtil;

public class MessageAddFragment extends Fragment implements OnClickListener {

	private static final int LOCAL_IMAGE_CODE = 1;
	private static final int CAMERA_IMAGE_CODE = 2;// 拍照
	private static final String IMAGE_UNSPECIFIED = "image/*";

	private LinearLayout msgAddImageLl, msgAddCameraLl, msgAddMapLl,
			msgKuaidiLl;
	private Context mContext;
	
	private String rootUrl = null;
	private String curFormatDateStr = null;

	private AddImageListener addImageListener;

	public interface AddImageListener {
		/**
		 * 1.本地图片;2拍照图片
		 * @param url
		 * @param imageType
		 */
		public void processAddImage(String url,int imageType);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			addImageListener = (AddImageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnArticleSelectedListener");
		}
	}

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			// Log.d(LTAG, "action: " + s);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				ToastUtil.toast(MessageAddFragment.this.getActivity(),
						"key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				ToastUtil.toast(MessageAddFragment.this.getActivity(), "网络出错");
			}
		}
	}

	private SDKReceiver mReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		this.getActivity().registerReceiver(mReceiver, iFilter);
		rootUrl = Environment.getExternalStorageDirectory().getPath();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View addView = inflater.inflate(R.layout.message_add_fragment,
				container, false);
		initView(addView);
		mContext = this.getActivity();
		return addView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void initView(View v) {
		msgAddImageLl = (LinearLayout) v.findViewById(R.id.id_msg_add_image_ll);
		msgAddCameraLl = (LinearLayout) v
				.findViewById(R.id.id_msg_add_camera_ll);
		msgAddMapLl = (LinearLayout) v.findViewById(R.id.id_msg_add_map_ll);
		msgKuaidiLl = (LinearLayout) v.findViewById(R.id.id_msg_add_kuaidi_ll);

		msgAddImageLl.setOnClickListener(this);
		msgAddCameraLl.setOnClickListener(this);
		msgAddMapLl.setOnClickListener(this);
		msgKuaidiLl.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.id_msg_add_image_ll:
			processInsertImage();
			break;
		case R.id.id_msg_add_camera_ll:
			processCamera();
			break;
		case R.id.id_msg_add_map_ll:
			processMap();
			break;
		case R.id.id_msg_add_kuaidi_ll:
			processKuaidi();
			break;
		}
	}

	/**
	 * 添加图片
	 */
	private void processInsertImage() {
		
		Intent intent = new Intent();
		/* 开启Pictures画面Type设定为image */
		intent.setType(IMAGE_UNSPECIFIED);
		/* 使用Intent.ACTION_GET_CONTENT这个Action */
		intent.setAction(Intent.ACTION_GET_CONTENT);
		/* 取得相片后返回本画面 */
		startActivityForResult(intent, LOCAL_IMAGE_CODE);
	}

	/**
	 * 处理拍照
	 */
	private void processCamera() {
		curFormatDateStr = DateUtil.getDateFormatString(Calendar.getInstance()
				.getTime());
		String fileName = "IMG_" + curFormatDateStr + ".png";
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(rootUrl, fileName)));
		intent.putExtra("fileName", fileName);
		startActivityForResult(intent, CAMERA_IMAGE_CODE);
	}

	/**
	 * 快递处理
	 */
	private void processKuaidi() {
		Intent intent  = new Intent();
		intent.setClass(this.getActivity(), ExpressActivity.class);
		this.getActivity().startActivity(intent);
	}

	/**
	 * 地图定位
	 */
	private void processMap() {
		Intent intent = new Intent();
		intent.setClass(this.getActivity(), LocationActivity.class);
		this.getActivity().startActivity(intent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			String url = "";
//			Bitmap bitmap = null;
			if (requestCode == LOCAL_IMAGE_CODE) {
				Uri uri = data.getData();
				Log.e("uri", uri.toString());
				
				if(url.indexOf("media/external/images/") != -1){
					url = uri.toString().substring(
							uri.toString().indexOf("//") + 2);
					if (url.contains(".jpg") && url.contains(".png")) {
						Toast.makeText(this.getActivity(), "请选择图片", Toast.LENGTH_SHORT).show();
						return;
					}
				}else{
					url = uri.toString().substring(
							uri.toString().indexOf("/") + 1);
				}
				
				
//				bitmap = HelpUtil.getBitmapByUrl(url);

				/**
				 * 获取bitmap另一种方法
				 * 
				 * ContentResolver cr = this.getContentResolver(); bitmap =
				 * HelpUtil.getBitmapByUri(uri, cr);
				 */
				addImageListener.processAddImage(url,LOCAL_IMAGE_CODE);
			} else if (requestCode == CAMERA_IMAGE_CODE) {
				url = rootUrl + "/" + "IMG_" + curFormatDateStr + ".png";
//				bitmap = HelpUtil.getBitmapByUrl(url);
//				showImageIv.setImageBitmap(HelpUtil.createRotateBitmap(bitmap));

				/**
				 * 获取bitmap另一种方法
				 * 
				 * File picture = new File(url); 
				 * Uri uri = Uri.fromFile(picture); 
				 * ContentResolver cr = this.getContentResolver(); 
				 * bitmap = HelpUtil.getBitmapByUri(uri, cr);
				 */
				addImageListener.processAddImage(url,CAMERA_IMAGE_CODE);
			}

		} else {
			Toast.makeText(this.getActivity(), "没有添加图片", Toast.LENGTH_SHORT).show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.getActivity().unregisterReceiver(mReceiver);
	}
}
