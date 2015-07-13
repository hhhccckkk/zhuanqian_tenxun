package com.hck.getmoney.ui;import java.util.ArrayList;import org.json.JSONException;import android.content.Intent;import android.os.Bundle;import android.text.TextUtils;import android.util.Log;import android.view.View;import android.view.View.OnClickListener;import android.widget.RelativeLayout;import android.widget.TextView;import com.hck.getmoney.data.Data;import com.hck.getmoney.data.HttpUrls;import com.hck.getmoney.net.JsonHttpResponseHandler;import com.hck.getmoney.net.RequestParams;import com.hck.getmoney.util.HttpUtil;import com.hck.getmoney.widget.PDialog;import com.hck.getmoney.widget.Toasts;import com.hck.kedouzq.R;import com.tencent.connect.share.QQShare;import com.tencent.connect.share.QzoneShare;import com.tencent.open.SocialConstants;import com.tencent.tauth.IUiListener;import com.tencent.tauth.Tencent;import com.tencent.tauth.UiError;public class TaskActivity extends BaseActivity implements OnClickListener {	private RelativeLayout wanshanziliao, fenxiangqq, fenxiangxinlang;	private RelativeLayout xiazai1, xiazai2, yaoqing;	private Tencent tencent;	private TextView wanshangTextView;	private TextView yaoqingTextView;	private TextView fenxiangQQ;	private TextView fenxiangXinLang;	private static final int jinbi = 150;	@Override	protected void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		setContentView(R.layout.activity_task);		tencent = Tencent.createInstance(Data.Tenxun, this);		initView();	}	public void back(View view) {		finish();	}	private void initView() {		wanshanziliao = (RelativeLayout) findViewById(R.id.xinshou_wanshanziliao);		fenxiangqq = (RelativeLayout) findViewById(R.id.xinshou_qq);		fenxiangxinlang = (RelativeLayout) findViewById(R.id.xinshou_xinlang);		xiazai1 = (RelativeLayout) findViewById(R.id.xinshou_xiazai1);		xiazai2 = (RelativeLayout) findViewById(R.id.xinshou_xiazai2);		yaoqing = (RelativeLayout) findViewById(R.id.xinshou_yaoqing);		wanshangTextView = (TextView) findViewById(R.id.xinshou_wanshangziliao);		yaoqingTextView = (TextView) findViewById(R.id.xinshou_yaoqingqq);		fenxiangQQ = (TextView) findViewById(R.id.xinshou_shareqq);		fenxiangXinLang = (TextView) findViewById(R.id.xinshou_sharexinlang);		yaoqing.setOnClickListener(this);		wanshanziliao.setOnClickListener(this);		fenxiangqq.setOnClickListener(this);		fenxiangxinlang.setOnClickListener(this);		xiazai1.setOnClickListener(this);		xiazai2.setOnClickListener(this);	}	@Override	protected void onResume() {		super.onResume();		initData();	}	private void initData() {		if (!TextUtils.isEmpty(Data.userBean.getQq())) {			wanshangTextView.setText("已完成任务");		}		if (Data.userBean.isShareQQ()) {			fenxiangQQ.setText("该项已完成");		}		if (Data.userBean.isShareXinLang()) {			fenxiangXinLang.setText("该项已完成");		}		if (Data.userBean.isYaoQingQQ()) {			yaoqingTextView.setText("该项已完成");		}	}	@Override	public void onClick(View arg0) {		switch (arg0.getId()) {		case R.id.xinshou_wanshanziliao:			updateUser();			break;		case R.id.xinshou_qq:			shareToFriends();			break;		case R.id.xinshou_xinlang:						break;		case R.id.xinshou_xiazai1:			startActivity(new Intent(TaskActivity.this, KeKeActivity.class));			break;		case R.id.xinshou_xiazai2:			startActivity(new Intent(TaskActivity.this, DLActivity.class));			break;		case R.id.xinshou_yaoqing:			login();			break;		default:			break;		}	}	private void updateUser() {		startActivity(new Intent(TaskActivity.this,				UpdateUserInfoActivity.class));	}	private void shareToFriends() {		final Bundle params = new Bundle();		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,				QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);		params.putString(QQShare.SHARE_TO_QQ_TITLE, "可以赚钱的手机app，推荐给大家");		params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,				Data.shareBean.getContent());			params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,					Data.shareBean.getUrlString());		ArrayList<String> imageUrls = new ArrayList<String>();		imageUrls.add(Data.shareBean.getImageUrl());		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);		doShareToQzone(params);	}	private void inviteFriend() {		Bundle params = new Bundle();		params.putString(SocialConstants.PARAM_APP_ICON,				Data.shareBean.getUrlString());		params.putString(SocialConstants.PARAM_APP_DESC,				Data.shareBean.getContent());		params.putString(SocialConstants.PARAM_ACT, "进入应用");		tencent.invite(TaskActivity.this, params, new IUiListener() {			@Override			public void onError(UiError arg0) {			}			@Override			public void onComplete(Object arg0) {				if (!Data.userBean.isYaoQingQQ()) {					sendDataToServer(Data.TENXUN_INVITE_APP);				}			}			@Override			public void onCancel() {			}		});	}	private void doShareToQzone(final Bundle params) {		if (tencent!=null) {			tencent.releaseResource();			tencent=null;		}		tencent =Tencent.createInstance(Data.Tenxun, this);			tencent.shareToQzone(TaskActivity.this, params, new IUiListener() {				@Override				public void onError(UiError arg0) {					Log.d("hck", "doShareToQzone onError: ");					Toasts.toast(TaskActivity.this, "分享失败");				}				@Override				public void onComplete(Object arg0) {					Log.d("hck", "doShareToQzone onComplete: ");					if (!Data.userBean.isShareQQ()) {						sendDataToServer(Data.TENXUN_SHARE);					}				}				@Override				public void onCancel() {					Log.d("hck", "doShareToQzone onCancel: ");					Toasts.toast(TaskActivity.this, "分享取消");				}			});	}	     IUiListener qZoneShareListener = new IUiListener() {        @Override        public void onCancel() {        	Log.d("hck", "doShareToQzone onCancel: ");        }        @Override        public void onError(UiError e) {        	Log.d("hck", "doShareToQzone onError: ");                   }		@Override		public void onComplete(Object response) {			Log.d("hck", "doShareToQzone onComplete: ");					}    };	@Override	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		super.onActivityResult(requestCode, resultCode, data);		Tencent.handleResultData(data, qZoneShareListener);			}	private void login() {		tencent = Tencent.createInstance(Data.Tenxun, this);		if (!tencent.isSessionValid()) {			Toasts.toast(this, "请先登录");			tencent.login(this, "all", new IUiListener() {				@Override				public void onError(UiError arg0) {					Toasts.toast(TaskActivity.this, "登录失败");				}				@Override				public void onComplete(Object arg0) {					inviteFriend();				}				@Override				public void onCancel() {					Toasts.toast(TaskActivity.this, "登录取消");				}			});		} else {			inviteFriend();		}	}	private void addMoney(int jinbi) {		Data.userBean.setAllKeDouBi(Data.userBean.getAllKeDouBi() + jinbi);	}	private void sendDataToServer(int type) {		PDialog.showDialog(this, "请稍等", "正在为您增加金币");		RequestParams params = new RequestParams();		params.put("uid", Data.userBean.getId() + "");		params.put("jinbi", jinbi + "");		params.put("type", type + "");		HttpUtil.getHttpUtil().post(HttpUrls.renwu, params,				new JsonHttpResponseHandler() {					public void onFailure(Throwable error, String content) {						Log.d("hck", "onFailure: " + content);					};					public void onSuccess(int statusCode,							org.json.JSONObject response) {						Log.d("hck", "onSuccess: " + response.toString());						boolean isok = false;						try {							isok = response.getBoolean("isok");						} catch (JSONException e) {							e.printStackTrace();						}						if (isok) {							addMoney(jinbi);							Toasts.toast(TaskActivity.this, "增加金币成功 您获取金币: "									+ jinbi + "个");						} else {							Toasts.toast(TaskActivity.this, "增加失败 请检查您的网络");						}					};					public void onFinish(String url) {						PDialog.hidenDialog();					};				});	}}