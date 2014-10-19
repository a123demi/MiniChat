package com.lming.minichat.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lming.minichat.ExpressInfoActivity;
import com.lming.minichat.R;

public class ExpressQueryUtil {

	private static final String TAG = ExpressQueryUtil.class.getSimpleName();

	/**
	 * express查询数据状态
	 * 
	 * @param number
	 * @param name
	 * @param code
	 * @param context
	 * @param progressDialog
	 * 
	 * 
	 *            status int 查询结果状态，0|1|2|3|4，0表示查询失败，1正常，2派送中，3已签收，4退回,5其他问题
	 *            errCode int
	 *            错误代码，0无错误，1单号不存在，2验证码错误，3链接查询服务器失败，4程序内部错误，5程序执行错误，
	 *            6快递单号格式错误，7快递公司错误，10未知错误，20API错误，21API被禁用，22API查询量耗尽。 message
	 *            string 错误消息 data array 进度 html string 其他HTML，该字段不一定存在 mailNo
	 *            string 快递单号 expSpellName string 快递公司英文代码 expTextName string
	 *            快递公司中文名 update int 最后更新时间（unix 时间戳） cache int 缓存时间，当前时间与
	 *            update 之间的差值，单位为：秒 ord string 排序，ASC | DESC
	 * 
	 *            {"status":"3","message":"","errCode":"0","data":[{"time":
	 *            "2013-02-23 17:10"
	 *            ,"context":"辽宁省大连市中山区四部公司 的收件员 王光 已收件"},{"time"
	 *            :"2013-02-24 17:59"
	 *            ,"context":"辽宁省大连市公司 已收入"},{"time":"2013-02-24 18:11"
	 *            ,"context"
	 *            :"辽宁省大连市中山区四部公司 已收件"},{"time":"2013-02-26 07:33","context"
	 *            :"吉林省长春市景阳公司 的派件员 张金达 派件中 派件员电话15948736487"
	 *            },{"time":"2013-02-26 16:47"
	 *            ,"context":"客户 同事收发家人 已签收 派件员 张金达"}
	 *            ],"html":"","mailNo":"7151900624"
	 *            ,"expTextName":"圆通快递","expSpellName"
	 *            :"yuantong","update":"1362656241"
	 *            ,"cache":"186488","ord":"ASC"}
	 */
	public static void queryExpressForNumber(final String number,
			final String name, final String code, final Context context,
			final ProgressDialog progressDialog) {
		StringBuffer url = new StringBuffer();
		url.append("http://api.ickd.cn/?").append("id=")
				.append(context.getResources().getString(R.string.api_id))
				.append("&secret=")
				.append(context.getResources().getString(R.string.api_secret))
				.append("&com=").append(code).append("&nu=").append(number)
				.append("&type=json&encode=utf8&ord=asc");

		// HttpUtils使用方法：使用普通get方法
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET, url.toString(),
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException err, String msg) {
						// TODO Auto-generated method stub
						Log.e(TAG, "onFailure");
						progressDialog.dismiss();
						ToastUtil.toast(context, "网络不稳定,请检查网络状态");
						;
					}

					@Override
					public void onStart() {
						Log.e(TAG, "onStart");
						progressDialog.setTitle("查询提示");
						progressDialog.setMessage("正在查询,请稍等...");
						// Sets whether this dialog is cancelable with the BACK
						// key.
						progressDialog.setCancelable(false);
						progressDialog
								.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						progressDialog.show();

						super.onStart();
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						Log.e(TAG, "onLoading");
						super.onLoading(total, current, isUploading);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						// TODO Auto-generated method stub
						try {
							JSONObject all = new JSONObject(responseInfo.result);
							String status = all.getString("status");

							if (status.equals("0")) { // 0代表查找失败
								String message = all.getString("message"); // message中存储着错误消息
								ToastUtil.toast(context, message);
								progressDialog.dismiss();
								return;
							}
							
							int errCode = Integer.valueOf(all.getInt("errCode"));
							if(processErrCode(errCode,context,progressDialog)){
								return;
							}
							// todo 传递数据到Express展示界面

							Intent intent = new Intent();
							intent.putExtra("expressData", all.getJSONArray("data").toString());
							intent.setClass(context, ExpressInfoActivity.class);
							context.startActivity(intent);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							ToastUtil.toast(context, e.getMessage());
						}
						progressDialog.dismiss();
					}

				});
	}
	
	/**
	 * 处理返回错误码错误提示信息
	 * @param errCode
	 * @param mContext
	 */
	private static boolean processErrCode(int errCode ,Context mContext,ProgressDialog progressDialog){
		String toastMsg = "";
		switch(errCode){
		case  1:
			toastMsg = "单号不存在";
			break;
		case  2:
			toastMsg = "验证码错误";
			break;
		case  3:
			toastMsg = "链接查询服务器失败";
			break;
		case  4:
			toastMsg = "程序内部错误";
			break;
		case  5:
			toastMsg = "程序执行错误";
			break;
		case  6:
			toastMsg = "快递单号格式错误";
			break;
		case  7:
			toastMsg = "快递公司错误";
			break;
		case  10:
			toastMsg = "未知错误";
			break;
		case  20:
			toastMsg = "API错误";
			break;
		case  21:
			toastMsg = "API被禁用";
			break;
		case  22:
			toastMsg = "API查询量耗尽";
			break;
		}
		
		if(!"".equals(toastMsg)){
			ToastUtil.toast(mContext, toastMsg);
			progressDialog.dismiss();
			return true;
		}
		return false;
	}
}
