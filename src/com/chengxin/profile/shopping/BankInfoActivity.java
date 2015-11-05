package com.chengxin.profile.shopping;import android.annotation.SuppressLint;import android.content.Intent;import android.content.res.Resources.NotFoundException;import android.os.Bundle;import android.os.Handler;import android.os.Message;import android.view.View;import android.widget.EditText;import android.widget.Toast;import com.chengxin.BaseActivity;import com.chengxin.R;import com.chengxin.Entity.WeiYuanState;import com.chengxin.dialog.CPAlert;import com.chengxin.dialog.CPAlert.OnAlertOkSelectId;import com.chengxin.global.GlobalParam;import com.chengxin.global.GlobleType;import com.chengxin.global.WeiYuanCommon;import com.chengxin.net.WeiYuanException;public class BankInfoActivity extends BaseActivity {	private EditText mInputBankName;	private EditText mInputBankUser;	private EditText mInputBankCard;	private String mBankName = null;	private String mBankUser = null;	private String mBankCard = null;	private int mPosition;	private int mShopType;		@Override	protected void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		mContext = this;		setContentView(R.layout.bank_info_edit);				mShopType = getIntent().getIntExtra("type", GlobleType.SHOPPING_MANAGER);		initComponent();			}	private void initComponent() {		setTitleContent(R.drawable.back_btn, R.drawable.ok, R.string.bank_edit_title);		mLeftBtn.setOnClickListener(this);		mRightBtn.setOnClickListener(this);		mInputBankName = (EditText)findViewById(R.id.bank);		mInputBankName.setFocusable(false);		mInputBankName.setFocusableInTouchMode(false);		mInputBankName.setOnClickListener(this);				mInputBankUser = (EditText)findViewById(R.id.bankuser);		mInputBankCard = (EditText)findViewById(R.id.bankcard);				mPosition = getIntent().getIntExtra("pos", 0);	}	@Override	public void onClick(View v) {		super.onClick(v);				switch (v.getId()) {		case R.id.left_btn:			this.finish();			break;		case R.id.right_btn:			commit();			break;		case R.id.bank:			String list[] = mContext.getResources().getStringArray(R.array.bank_array);			CPAlert.showAlert(					mContext,					"选择开户银行",					list,					new OnAlertOkSelectId() {								@Override				public void onOkClick(int whichButton, String unused, String bank) {					mInputBankName.setText(bank);				}			});			break;		default:			break;		}	}	private void commit() {		if (verifyInput()) {			if(!WeiYuanCommon.getNetWorkState()){				mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);				return;			}			new Thread(){				public void run() {					try {						WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, 								mContext.getResources().getString(R.string.commit_dataing));						WeiYuanState state = WeiYuanCommon.getWeiYuanInfo().updateBank(								mShopType,								mBankName,								mBankUser,								mBankCard);												mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);						WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE,state);					} catch (NotFoundException e) {						e.printStackTrace();						mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);					} catch (WeiYuanException e) {						e.printStackTrace();						WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR, 								mContext.getResources().getString(e.getStatusCode()));					} catch (Exception e) {						e.printStackTrace();						mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);					}				};			}.start();		}	}	private boolean verifyInput() {		mBankName = mInputBankName.getText().toString();		mBankUser = mInputBankUser.getText().toString();		mBankCard = mInputBankCard.getText().toString();				boolean isCheck = true;		String hintMsg = "";				if (mBankName == null || mBankName.equals("")) {			isCheck = false;			hintMsg = "请输入开户银行名称";		} else if (mBankUser == null || mBankUser.equals("")) {			isCheck = false;			hintMsg = "请输入开户人姓名";		} else if (mBankCard == null || mBankCard.equals("")) {			isCheck = false;			hintMsg = "请输入开户银行账号";		}		if(!isCheck && (hintMsg != null && !hintMsg.equals(""))){			Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();		}		return isCheck;	}		@SuppressLint("HandlerLeak")	private Handler mHandler = new Handler(){		@Override		public void handleMessage(Message msg) {			super.handleMessage(msg);						switch (msg.what) {			case GlobalParam.MSG_CHECK_STATE:				WeiYuanState sate = (WeiYuanState)msg.obj;								if(sate == null){					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();					return;				}								Toast.makeText(mContext, sate.errorMsg,Toast.LENGTH_LONG).show();				if(sate.code == 0){					Intent intent = new Intent();					intent.putExtra("pos", mPosition);					intent.putExtra("bankname",mBankName);					intent.putExtra("bankuser",mBankUser);					intent.putExtra("bankcard",mBankCard);					setResult(RESULT_OK,intent);					BankInfoActivity.this.finish();				}								break;			default:				break;			}		}	};}