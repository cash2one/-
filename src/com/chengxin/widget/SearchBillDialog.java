package com.chengxin.widget;import java.util.ArrayList;import java.util.List;import com.chengxin.ChatMainActivity;import com.chengxin.R;import com.chengxin.Entity.Bill;import com.chengxin.Entity.Login;import com.chengxin.Entity.LoginResult;import com.chengxin.adapter.BillAdapter;import com.chengxin.financial.BillBankActivity;import com.chengxin.financial.BillInfoActivity;import com.chengxin.global.GlobalParam;import com.chengxin.global.WeiYuanCommon;import com.chengxin.net.WeiYuanException;import android.app.Dialog;import android.content.Context;import android.content.Intent;import android.os.Bundle;import android.os.Handler;import android.os.Message;import android.text.Editable;import android.text.TextWatcher;import android.view.MotionEvent;import android.view.View;import android.view.inputmethod.InputMethodManager;import android.widget.AdapterView;import android.widget.Button;import android.widget.EditText;import android.widget.ListView;import android.widget.Toast;import android.widget.AdapterView.OnItemClickListener;public class SearchBillDialog extends Dialog  implements OnItemClickListener, android.view.View.OnClickListener{	public interface OnSearchBillClickListener {		public void onSearchBillClickListener(int sourcePosition);	}		private List< Bill > mSourceList;	private List< Bill > mSearchList;	private List< Integer > mListPos;		private Context mContext;	private Button mCancleBtn;	private EditText mContentEdit;	protected ListView mListView;	private BillAdapter mAdapter;	private OnSearchBillClickListener mListener = null;	public void setOnSearchBillClickListener(OnSearchBillClickListener l) {		mListener = l;	}		public SearchBillDialog(Context context, List<Bill> sourceList) {		super(context, R.style.ContentOverlay);		mSourceList = sourceList;		mContext =context;	}	public SearchBillDialog(Context context) {		super(context, R.style.ContentOverlay);		mContext = context;	}	@Override	protected void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		setContentView(R.layout.contact_search_dialog);		initComponent();	}	private void initComponent() {		mCancleBtn = (Button)findViewById(R.id.cancle_btn);		mCancleBtn.setOnClickListener(this);		mSearchList = new ArrayList<Bill>();		mListPos = new ArrayList<Integer>();		mContentEdit = (EditText) findViewById(R.id.searchcontent);		mContentEdit.addTextChangedListener(new TextWatcher() {			@Override			public void onTextChanged(CharSequence s, int start, int before,					int count) {			}			@Override			public void beforeTextChanged(CharSequence s, int start, int count,					int after) {			}			@Override			public void afterTextChanged(Editable s) {				if(mSourceList == null || mSourceList.size()<=0){					return;				}								List< Bill > tempList = new ArrayList<Bill>();				List<Integer> indexList = new ArrayList<Integer>();								if (s.toString() != null && !s.toString().equals("")) {					for (int i = 0; i < mSourceList.size(); i++) {						String name = mSourceList.get(i).name;						String bank = mSourceList.get(i).bank;												if(name!=null && !name.equals("")){							if (name.contains(s.toString())) {								tempList.add(mSourceList.get(i));								indexList.add(new Integer(i));							}						}						if(bank!=null && !bank.equals("")){							if (bank.contains(s.toString())) {								tempList.add(mSourceList.get(i));								indexList.add(new Integer(i));							}						}					}				}				if (mSearchList != null) {					mSearchList.clear();				}				if (mListPos != null) {					mListPos.clear();				}				mSearchList.addAll(tempList);				mListPos.addAll(indexList);				updateListView();								if(mSearchList!=null && mSearchList.size()>0){					mListView.setVisibility(View.VISIBLE);				}else{					mListView.setVisibility(View.GONE);				}			}		});		mListView = (ListView) findViewById(R.id.contact_list);		mListView.setVisibility(View.GONE);		mListView.setDivider(mContext.getResources().getDrawable(R.drawable.order_devider_line));		mListView.setCacheColorHint(0);		mListView.setOnItemClickListener(this);//		mListView.setSelector(mContext.getResources().getDrawable(R.drawable.transparent_selector));	}	private void updateListView() {		if(mSearchList != null && mSearchList.size() != 0){			mListView.setVisibility(View.VISIBLE);		}		if(mAdapter != null){			mAdapter.notifyDataSetChanged();			return;		} else if (mSearchList != null) {			mAdapter = new BillAdapter(mContext, mSearchList);			mListView.setAdapter(mAdapter);		}	}	@Override	public void onItemClick(AdapterView<?> listView, View convertView, int position, long uid) {		if (mListener != null) {			mListener.onSearchBillClickListener(mListPos.get(position).intValue());		}				SearchBillDialog.this.dismiss();	}		@Override	public void onClick(View v) {		SearchBillDialog.this.dismiss();	}		private void hideKeyBoard(){		if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){			InputMethodManager manager= (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);			manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  		}  	}	@Override	public boolean onTouchEvent(MotionEvent event) {		if(event.getAction() == MotionEvent.ACTION_DOWN){  			if(mSearchList == null || mSearchList.size()<=0){				hideKeyBoard();				SearchBillDialog.this.dismiss();			}		}  		return super.onTouchEvent(event);	}		protected Handler mHandler = new Handler() {		@Override		public void handleMessage(Message msg) {			super.handleMessage(msg);			switch (msg.what) {			case GlobalParam.MSG_GET_CONTACT_DATA:				LoginResult login = (LoginResult)msg.obj;								if(login == null){					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();					return;				}								if(login.mState.code == 0){					Login user = login.mLogin;										if(user != null){						Intent intent = new Intent(mContext, ChatMainActivity.class);						user.mIsRoom = 100;						intent.putExtra("data", user);						mContext.startActivity(intent);						SearchBillDialog.this.dismiss();					}				} else {					Toast.makeText(mContext, login.mState.errorMsg,Toast.LENGTH_LONG).show();				}				break;						default:				break;			}		}	};}