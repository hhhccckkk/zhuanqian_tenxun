package com.hck.getmoney.ui;import java.util.ArrayList;import org.json.JSONObject;import android.content.Context;import android.os.Bundle;import android.util.Log;import android.view.View;import android.widget.TextView;import com.hck.getmoney.data.Data;import com.hck.getmoney.data.HttpUrls;import com.hck.getmoney.net.JsonHttpResponseHandler;import com.hck.getmoney.net.RequestParams;import com.hck.getmoney.util.HttpUtil;import com.hck.getmoney.widget.Toasts;import com.hck.kedouzq.R;import com.qidian.intwal.QDOpenWall;import com.qidian.intwal.QDScoreCallBack;import com.qidian.intwal.ScoreAdMsg;import com.qidian.intwal.Utils;/** *  * @author *  */public class QiDianActivity extends BaseActivity {	private int num;	private String content;	private int point;	private TextView nameTextView, contenTextView;	private int kid;	@Override	protected void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		initAd();		setContentView(R.layout.activity_play_dl);		initView();		num = getIntent().getIntExtra("num", 5);		content = getIntent().getStringExtra("neirong");		kid = getIntent().getIntExtra("kid", 0);		initDate();		nt();	}	private void initAd() {		try {			QDOpenWall.setAppId(this, "108");		} catch (Exception e) {			Toasts.toast(this, "初始化失败 请重试");		}	}	private void initView() {		nameTextView = (TextView) findViewById(R.id.play_dl_name);		contenTextView = (TextView) findViewById(R.id.play_dl_content);	}	private void initDate() {		nameTextView.setText("本区每天限制下载试用" + num + "个软件");		contenTextView.setText(content);	}	public void startGetMoney(View view) {		QDOpenWall.initInterfaceType(this, new QDScoreCallBack() {			@Override			public void onSuccess(Context arg0, String arg1) {				point=0;                try {                	ArrayList<ScoreAdMsg> msgs = Utils.getScoreMsgList(arg1);    				if (msgs==null) {    					return;    					    				}    				for (int i = 0; i < msgs.size(); i++) {    					ScoreAdMsg msg = msgs.get(i);    					point = Integer.parseInt(msg.getScore());    				}    				    				if (point > 0) {    					savePoint();    				}				} catch (Exception e) {					Log.d("hck","eeee: "+e.toString());				}							}		});		QDOpenWall.getInstance().show();	}	private void savePoint() {		point= hashYQM(point);		params = new RequestParams();		params.put("kindid", kid + "");		params.put("num", num + "");		params.put("type", "麒点");		if (Data.userBean == null) {			Toasts.toast(this, "增加金币异常  请重新登录");			return;		}		params.put("uid", Data.userBean.getId() + "");		params.put("money", point + "");		HttpUtil.getHttpUtil().get(HttpUrls.addMoneyP, params,				new JsonHttpResponseHandler() {					@Override					public void onFinish(String url) {						super.onFinish(url);					}					@Override					public void onFailure(Throwable error, String content) {						super.onFailure(error, content);						Toasts.toast(getApplicationContext(), "网络异常 增加金币失败 ");					}					@Override					public void onSuccess(int statusCode, JSONObject response) {						super.onSuccess(statusCode, response);						try {							isOk = response.getBoolean("isok");							if (isOk) {								Toasts.toast(getApplicationContext(), nt);								Data.userBean.setAllKeDouBi(Data.userBean										.getAllKeDouBi() + point);							} else {								if (response.getInt("type") == 1) {									Toasts.toast(getApplicationContext(),											"获取金币失败 本区每天限制下载" + num + "个应用  ");								} else {									Toasts.toast(getApplicationContext(),											"网络异常 增加金币失败 ");								}							}						} catch (Exception e) {							Toasts.toast(QiDianActivity.this, "网络异常 增加金币失败 ");						}					}				});	}}