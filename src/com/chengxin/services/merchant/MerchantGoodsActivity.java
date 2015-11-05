package com.chengxin.services.merchant;import java.io.Serializable;import java.util.ArrayList;import java.util.List;import android.content.Intent;import android.net.Uri;import android.os.Bundle;import android.os.Handler;import android.os.Message;import android.os.Parcelable;import android.util.SparseArray;import android.view.LayoutInflater;import android.view.View;import android.widget.AbsListView;import android.widget.AbsListView.OnScrollListener;import android.widget.AdapterView;import android.widget.AdapterView.OnItemClickListener;import android.widget.Button;import android.widget.LinearLayout;import android.widget.ListView;import android.widget.ProgressBar;import android.widget.TextView;import android.widget.Toast;import com.chengxin.BaseActivity;import com.chengxin.ChatMainActivity;import com.chengxin.R;import com.chengxin.Entity.Login;import com.chengxin.Entity.LoginResult;import com.chengxin.Entity.MerchantGoods;import com.chengxin.Entity.MerchantGoodsList;import com.chengxin.Entity.MerchantInfo;import com.chengxin.Entity.WeiYuanState;import com.chengxin.adapter.MerchantGoodsAdapter;import com.chengxin.global.GlobalParam;import com.chengxin.global.WeiYuanCommon;import com.chengxin.map.BMapApiApp;import com.chengxin.net.WeiYuanException;public class MerchantGoodsActivity extends BaseActivity implements OnItemClickListener, OnScrollListener {	private MerchantInfo merchant;	private TextView mTextAddress;	private TextView mTextDistance;	private TextView mTextCategory;	private TextView mTextFeature;	private ListView mListView;	private Button nBtnCall;	private Button nBtnChat;	protected int mPage;	protected boolean mNoMore;	protected List<MerchantGoods> mList = new ArrayList<MerchantGoods>();	protected LinearLayout mFootView;	private MerchantGoodsAdapter mAdapter;	@Override	protected void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		mContext = this;				setContentView(R.layout.activity_merchant_goods);				if (getIntent().hasExtra("merchant")) {			merchant = (MerchantInfo)getIntent().getExtras().get("merchant");		}				if (merchant == null) {			merchant = new MerchantInfo();			merchant.name = "未知商户";		}				initComponent();		getData();	}	private void initComponent() {		setTitleContent(R.drawable.back_btn, 0, merchant.name);		mLeftBtn.setOnClickListener(this);				mTextAddress = (TextView)findViewById(R.id.address);		mTextDistance = (TextView)findViewById(R.id.distance);		mTextCategory = (TextView)findViewById(R.id.category);		mTextFeature = (TextView)findViewById(R.id.feature);				mListView = (ListView)findViewById(R.id.listview);		mListView.setOnItemClickListener(this);		mListView.setOnScrollListener(this);				nBtnCall = (Button)findViewById(R.id.btn_call);		nBtnChat = (Button)findViewById(R.id.btn_chat);				nBtnCall.setOnClickListener(this);		nBtnChat.setOnClickListener(this);	}	private void getData() {		if (merchant.id > 0) {			mTextAddress.setText(String.format("地址: %s", merchant.address));			mTextDistance.setText(String.format("距离您 " +  merchant.distance / 1000 + "公里"));;			mTextCategory.setText(String.format("经营类别: %s", merchant.getFeatures()));			mTextFeature.setText(String.format("特色: %s", merchant.content));						questGoods(GlobalParam.LIST_LOAD_FIRST);		}	}	private void questGoods(final int loadType) {		if (!WeiYuanCommon.getNetWorkState()) {			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);			return;		}		new Thread(){			public void run() {				try {					switch (loadType) {					case GlobalParam.LIST_LOAD_FIRST:					case GlobalParam.LIST_LOAD_REFERSH:						WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, 								mContext.getResources().getString(R.string.get_dataing));						mPage = 1;						break;					case GlobalParam.LIST_LOAD_MORE:						if(!mNoMore){							mPage += 1;		    			}						break;					}					MerchantGoodsList tempList = null;										tempList = WeiYuanCommon.getWeiYuanInfo().getMerchantGoodsByShopId(							merchant.id,							mPage);					if (loadType == GlobalParam.LIST_LOAD_MORE) {						WeiYuanCommon.sendMsg(mHandler, GlobalParam.HIDE_LOADINGMORE_INDECATOR, tempList);					} else {						mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);						WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_LOADINGFIRST_DATA, tempList);					}				} catch (WeiYuanException e) {					e.printStackTrace();					WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR, 							mContext.getResources().getString(e.getStatusCode()));				} catch (Exception e) {					e.printStackTrace();					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);				}			};		}.start();	}	@Override	public void onClick(View v) {		super.onClick(v);		switch (v.getId()) {		case R.id.left_btn:			this.finish();			break;		case R.id.btn_call:			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + merchant.phone));			if (intent != null) startActivity(intent);			break;		case R.id.btn_chat:			beginChatWithUid(String.valueOf(merchant.uid));			break;		}	}	@Override	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {		Intent intent = new Intent(mContext, PictureViewActivity.class);		intent.putExtra("data", mList.get(arg2));		startActivity(intent);	}	private void beginChatWithUid(final String uid) {		if (uid != null && !uid.equals("")) {			if (!WeiYuanCommon.getNetWorkState()) {				mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);				return;			}			new Thread(){				public void run() {					try {						LoginResult user = WeiYuanCommon.getWeiYuanInfo().getUserInfo(uid);						WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_GET_CONTACT_DATA, user);					} catch (WeiYuanException e) {						e.printStackTrace();						WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR, 								mContext.getResources().getString(e.getStatusCode()));					} catch (Exception e) {						e.printStackTrace();					}				};			}.start();		}	}		protected Handler mHandler = new Handler() {		@Override		public void handleMessage(Message msg) {			super.handleMessage(msg);			switch (msg.what) {			case GlobalParam.MSG_GET_CONTACT_DATA:				LoginResult login = (LoginResult)msg.obj;								if(login == null){					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();					return;				}								if(login.mState.code == 0){					Login user = login.mLogin;										if(user != null){						Intent intent = new Intent(mContext, ChatMainActivity.class);						user.mIsRoom = 100;						intent.putExtra("data", user);						startActivity(intent);					}				} else {					Toast.makeText(mContext, login.mState.errorMsg,Toast.LENGTH_LONG).show();				}				break;			case GlobalParam.MSG_LOADINGFIRST_DATA:				MerchantGoodsList list = (MerchantGoodsList)msg.obj;								if (list == null) {					return;				}								WeiYuanState state = list.mState;				if(state == null){					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();					return;				}								if (state.errorMsg != null && state.errorMsg.length() > 0)					Toast.makeText(mContext, state.errorMsg,Toast.LENGTH_LONG).show();				if(state.code == 0){					mList.clear();										if (list != null && list.size() > 0) {						if (list.mPageInfo != null) {							mNoMore = list.mPageInfo.hasMore == 0;						}												mList.addAll(list.mList);					}										showData();				}				break;							case GlobalParam.SHOW_LOADINGMORE_INDECATOR:				if (mListView.getFooterViewsCount() == 0) {					if (mFootView == null) {						mFootView = (LinearLayout) LayoutInflater.from(mContext)								.inflate(R.layout.hometab_listview_footer, null);					}					mListView.addFooterView(mFootView);						ProgressBar pb = (ProgressBar)mFootView.findViewById(R.id.hometab_addmore_progressbar);					pb.setVisibility(View.VISIBLE);		 							TextView more = (TextView)mFootView.findViewById(R.id.hometab_footer_text);					more.setText(BMapApiApp.getInstance().getResources().getString(R.string.add_more_loading));				}		 		questGoods(GlobalParam.LIST_LOAD_MORE);				break;			case GlobalParam.HIDE_LOADINGMORE_INDECATOR:				list = (MerchantGoodsList)msg.obj;								if (list == null) {					return;				}								state = list.mState;								if(state == null){					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();					return;				}								if (state.errorMsg != null && state.errorMsg.length() > 0)					Toast.makeText(mContext, state.errorMsg,Toast.LENGTH_LONG).show();				if(state.code == 0){					if (list != null && list.size() > 0) {						if (list.mPageInfo != null) {							mNoMore = list.mPageInfo.hasMore == 0;						}												mList.addAll(list.mList);					}										showData();				}								if (mFootView != null) {					mListView.removeFooterView(mFootView);				}					break;			}		}	};	protected void showData() {		if (mAdapter == null) {			mAdapter = new MerchantGoodsAdapter(mContext, mList);//			mAdapter.setOnMerchantInfoClickListener(this);			mListView.setAdapter(mAdapter);		} else {			mAdapter.notifyDataSetChanged();;		}	}	@Override	public void onScroll(AbsListView view, int firstVisibleItem,			int visibleItemCount, int totalItemCount) {			}	@Override	public void onScrollStateChanged(AbsListView view, int scrollState) {		switch (scrollState) {		case OnScrollListener.SCROLL_STATE_IDLE://处理加载更多					if(view.getLastVisiblePosition() == (view.getCount()-1) && !mNoMore){				if (WeiYuanCommon.verifyNetwork(mContext)){					mHandler.sendEmptyMessage(GlobalParam.SHOW_LOADINGMORE_INDECATOR);				}else{					Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();				}			}			break;		default:			break;		}	}}