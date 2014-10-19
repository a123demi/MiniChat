package com.lming.minichat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.lming.minichat.util.ExpressQueryUtil;
import com.lming.minichat.util.NetUtil;
import com.lming.minichat.util.ToastUtil;

public class ExpressActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = ExpressActivity.class.getSimpleName();
	
	private EditText expressNumEt;
	private Spinner expressNameSp;
	private Button expressQueryBtn;
	private ProgressDialog progressDialog;
	
	private String[] expressNames;
	private String[] expressCodes;
	
	private ArrayAdapter<String> adapter;
	private int selectedPosition = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.express_activity);
		findById();
		initData();
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	@Override
	public void findById(){
		expressNumEt = (EditText) this.findViewById(R.id.express_number_et);
		expressNameSp = (Spinner) this.findViewById(R.id.express_company_sp);
		expressQueryBtn = (Button) this.findViewById(R.id.express_query_btn);
		
		expressQueryBtn.setOnClickListener(this);
	}
	
	private void initData(){
		
		if(!NetUtil.isNetConnected(this)){
			ToastUtil.toast(this, "网络无法连接,请重新连接");
		}
		
		expressNames = this.getResources().getStringArray(R.array.express_names);
		expressCodes = this.getResources().getStringArray(R.array.express_codes);
		
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,expressNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		expressNameSp.setAdapter(adapter);
		
		expressNameSp.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				selectedPosition = position;
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.express_query_btn:
			processQuery();
			break;
		}
	}
	
	private void processQuery(){
		if(!NetUtil.isNetConnected(this)){
			ToastUtil.toast(this, "网络无法连接,请重新连接");
			return;
		}
		expressNumEt.setText("968043082536");
		String selectedNum = expressNumEt.getText().toString();
		if("".equals(selectedNum)){
			ToastUtil.toast(this, "请填写快递单号");
			return;
		}
		
		String selectedName = expressNames[selectedPosition];
		String selectedCode = expressCodes[selectedPosition];
		
		progressDialog = new ProgressDialog(this);
		ExpressQueryUtil.queryExpressForNumber(selectedNum, selectedName, selectedCode, this, progressDialog);
	}
	
	
}
