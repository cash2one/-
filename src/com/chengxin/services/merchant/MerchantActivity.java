package com.chengxin.services.merchant;import java.util.ArrayList;import java.util.List;import android.content.Intent;import android.net.Uri;import android.os.Bundle;import android.os.Handler;import android.os.Message;import android.util.Log;import android.view.Gravity;import android.view.LayoutInflater;import android.view.View;import android.widget.AbsListView;import android.widget.AbsListView.OnScrollListener;import android.widget.AdapterView;import android.widget.AdapterView.OnItemClickListener;import android.widget.Button;import android.widget.LinearLayout;import android.widget.ListView;import android.widget.ProgressBar;import android.widget.RelativeLayout;import android.widget.TextView;import android.widget.Toast;import com.chengxin.BaseActivity;import com.chengxin.ChatMainActivity;import com.chengxin.R;import com.chengxin.Entity.Login;import com.chengxin.Entity.LoginResult;import com.chengxin.Entity.MapInfo;import com.chengxin.Entity.MerchantCategory;import com.chengxin.Entity.MerchantInfo;import com.chengxin.Entity.MerchantList;import com.chengxin.Entity.PopItem;import com.chengxin.Entity.WeiYuanState;import com.chengxin.adapter.MerchantInfoAdapter;import com.chengxin.adapter.MerchantInfoAdapter.OnMerchantInfoClickListener;import com.chengxin.dialog.CPAlert;import com.chengxin.dialog.CPAlert.OnAlertOkSelectId;import com.chengxin.global.GlobalParam;import com.chengxin.global.GlobleType;import com.chengxin.global.WeiYuanCommon;import com.chengxin.map.BMapApiApp;import com.chengxin.net.WeiYuanException;import com.chengxin.widget.PopWindows;import com.chengxin.widget.PopWindows.PopWindowsInterface;public class MerchantActivity extends BaseActivity implements OnMerchantInfoClickListener, OnItemClickListener {	protected static final int [] MERCHANT_TYPE_MASK = {1, 10, 100, 1000};	protected static final int MSG_GOODS_LIST = 0x7947;	protected static final int MSG_MERCHANT_ADDED = 0x7948;	protected static final int MSG_MERCHANT_EDITED = 0x7949;	private static final int REQUEST_MERCHANT_INFO = 0x7950;	private static final int RESQUEST_CHOOSE_CITY = 0x8001;	protected static final int MSG_GOODS_EDITED = 0x8022;	protected static final int MSG_GOODS_ADDED = 0x8021;		private LinearLayout mListFooter;	private ListView mListView;	private TextView mFooterText;	private List < MerchantInfo > mList = new ArrayList< MerchantInfo >();	private MerchantInfoAdapter mAdapter;		protected int mPosition = -1;	private List<PopItem> mPopList = new ArrayList<PopItem>();	private PopWindows mPopWindows;	private RelativeLayout mTitleLayout;	private MapInfo mMapInfo;	private Button mBtnLocate;	private Button mBtnChoose;	protected String mCurrentCity = null;	protected boolean mNoMore;	protected int mPage;	protected LinearLayout mFootView;	private int merchantType = 0;	private MerchantInfo merchantInfo = null;	private String mCatagory;	private List<PopItem> mChooseMenuList;	private LinearLayout mButtonLayout;	private PopWindows mPopChoose;	@Override	protected void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		mContext = this;				setContentView(R.layout.activity_financing_main);				if (getIntent().hasExtra("merchant")) {			merchantType = getIntent().getIntExtra("merchant", 0);		}				if(BMapApiApp.getContryList() == null				||BMapApiApp.getContryList().mCountryList == null				|| BMapApiApp.getContryList().mCountryList.size()<=0){			try {				BMapApiApp.setContryList(WeiYuanCommon.getWeiYuanInfo().getCityAndContryUser());			} catch (WeiYuanException e) {				e.printStackTrace();			}		}		Login user = WeiYuanCommon.getLoginResult(mContext);				if (user != null) {			merchantInfo = user.getMerchantByType(merchantType);		}				mMapInfo = new MapInfo();		mMapInfo.setLat(String.valueOf(WeiYuanCommon.getCurrentLat(mContext)));		mMapInfo.setLon(String.valueOf(WeiYuanCommon.getCurrentLng(mContext)));		mCurrentCity = getBriefOfCity(WeiYuanCommon.getCurrentCity(mContext));				initComponent();		getData(GlobalParam.LIST_LOAD_FIRST);	}	private void initComponent() {		int titleId = R.string.financing_main_title;				if (merchantType > 0) {			switch (merchantType) {			case 1:				titleId = R.string.merchant_title_01;				break;			case 2:				titleId = R.string.merchant_title_02;				break;			case 3:				titleId = R.string.merchant_title_03;				break;			case 4:				titleId = R.string.merchant_title_04;				break;			}		}				setTitleContent(R.drawable.back_btn, false, false, true, titleId);		mLeftBtn.setOnClickListener(this);		mMoreBtn.setOnClickListener(this);				mTitleLayout = (RelativeLayout) findViewById(R.id.title_layout);		mButtonLayout = (LinearLayout) findViewById(R.id.btn_layout);		mListFooter = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.services_require_footer, null);		mFooterText = (TextView)mListFooter.findViewById(R.id.sumary);		mListView = (ListView)findViewById(R.id.list);		mListView.addFooterView(mListFooter);		mListView.setOnItemClickListener(this);				mListView.setOnScrollListener(new OnScrollListener() {						@Override			public void onScrollStateChanged(AbsListView view, int scrollState) {				switch (scrollState) {				case OnScrollListener.SCROLL_STATE_IDLE://处理加载更多									if(view.getLastVisiblePosition() == (view.getCount()-1) && !mNoMore){						if (WeiYuanCommon.verifyNetwork(mContext)){							mHandler.sendEmptyMessage(GlobalParam.SHOW_LOADINGMORE_INDECATOR);						}else{							Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();						}					}					break;				default:					break;				}			}						@Override			public void onScroll(AbsListView view, int firstVisibleItem,					int visibleItemCount, int totalItemCount) {							}		});						mBtnLocate = (Button)findViewById(R.id.btn_locate);		mBtnLocate.setText("选择类别");		mBtnChoose = (Button)findViewById(R.id.btn_choose);		mBtnLocate.setOnClickListener(this);		mBtnChoose.setOnClickListener(this);			    Login user = WeiYuanCommon.getLoginResult(mContext);	    int status = 0;	    	    if (user != null) {	    	status = user.isMerchant / MERCHANT_TYPE_MASK[merchantType - 1];	    	status = status % 10;	    }	    	    if (status == 0) {		    mPopList.add(new PopItem(0, "申请入驻"));	    } else if (status == 2) {		    mPopList.add(new PopItem(1, "审核中..."));	    } else {		    mPopList.add(new PopItem(2, "基本信息管理"));			mPopList.add(new PopItem(3, "服务产品管理"));	    }		mPopWindows = new PopWindows(mContext, mPopList, mTitleLayout, new PopWindowsInterface() {			@Override			public void onItemClick(int dataId, int position, View view) {				switch (dataId) {				case 0:	//申请入驻					Intent intent = new Intent();					intent.setClass(mContext, MerchantInfoActivity.class);					intent.putExtra("merchant", merchantType);					startActivityForResult(intent, REQUEST_MERCHANT_INFO);					break;				case 1:	//审核中...					Toast.makeText(mContext, "你的申请正在审核中，敬请期待", Toast.LENGTH_LONG).show();					break;				case 2:	//基本信息管理					intent = new Intent();					intent.setClass(mContext, MerchantInfoActivity.class);					intent.putExtra("data", merchantInfo);					startActivityForResult(intent, REQUEST_MERCHANT_INFO);					break;				case 3:	//服务产品管理					intent = new Intent();					intent.setClass(mContext, MerchantGoodsManagerActivity.class);					intent.putExtra("merchant", merchantInfo);					startActivity(intent);					break;				default:					break;				}			}		});        mChooseMenuList = (List<PopItem>) MerchantCategory.getPopMenuList(merchantType);        mPopChoose = new PopWindows(mContext, mChooseMenuList, mButtonLayout, new PopWindowsInterface() {			@Override			public void onItemClick(int dataId, int position, View view) {				mCatagory = String.valueOf(dataId);				Log.i("DataID", String.valueOf(dataId));				getData(GlobalParam.LIST_LOAD_REFERSH);			}        });	}	private void getData(final int loadType) {		if (!WeiYuanCommon.getNetWorkState()) {			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);			return;		}		new Thread(){			public void run() {				try {					switch (loadType) {					case GlobalParam.LIST_LOAD_FIRST:					case GlobalParam.LIST_LOAD_REFERSH:						WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, 								mContext.getResources().getString(R.string.get_dataing));						mPage = 1;						break;					case GlobalParam.LIST_LOAD_MORE:						if(!mNoMore){							mPage += 1;		    			}						break;					}					MerchantList tempList = null;										tempList = WeiYuanCommon.getWeiYuanInfo().getMerchantList(							mPage,							merchantType,							mCurrentCity,							mCatagory,							Double.valueOf(mMapInfo.getLat()),							Double.valueOf(mMapInfo.getLng()));					if (loadType == GlobalParam.LIST_LOAD_MORE) {						WeiYuanCommon.sendMsg(mHandler, GlobalParam.HIDE_LOADINGMORE_INDECATOR, tempList);					} else {						mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);						WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_LOADINGFIRST_DATA, tempList);					}				} catch (WeiYuanException e) {					e.printStackTrace();					WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR, 							mContext.getResources().getString(e.getStatusCode()));				} catch (Exception e) {					e.printStackTrace();					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);				}			};		}.start();	}	protected Handler mHandler = new Handler() {		@Override		public void handleMessage(Message msg) {			super.handleMessage(msg);			switch (msg.what) {			case GlobalParam.MSG_GET_CONTACT_DATA:				LoginResult login = (LoginResult)msg.obj;								if(login == null){					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();					return;				}								if(login.mState.code == 0){					Login user = login.mLogin;										if(user != null){						Intent intent = new Intent(mContext, ChatMainActivity.class);						user.mIsRoom = 100;						intent.putExtra("data", user);						startActivity(intent);					}				} else {					Toast.makeText(mContext, login.mState.errorMsg,Toast.LENGTH_LONG).show();				}				break;			//			case MSG_MERCHANT_ADDED://				MerchantInfo merchant = (MerchantInfo) msg.obj;//				//				if (merchant != null) {//					WeiYuanState state = merchant.state;////					if(state == null){//						Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();//						return;//					}//					//					if (state.errorMsg != null && state.errorMsg.length() > 0)//						Toast.makeText(mContext, state.errorMsg,Toast.LENGTH_LONG).show();////					if(state.code == 0){//						Login user = WeiYuanCommon.getLoginResult(mContext);//						//						if (user != null) {//							user.merchantList.add(merchant);//							user.isMerchant |= 2 * MERCHANT_TYPE_MASK[merchantType];//							WeiYuanCommon.saveLoginResult(mContext, user);//						}//					}//				}////				break;//				//			case MSG_MERCHANT_EDITED://				merchant = (MerchantInfo) msg.obj;//				//				if (merchant != null) {//					WeiYuanState state = merchant.state;////					if(state == null){//						Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();//						return;//					}//					//					if (state.errorMsg != null && state.errorMsg.length() > 0)//						Toast.makeText(mContext, state.errorMsg,Toast.LENGTH_LONG).show();////					if(state.code == 0){//						updateLoginWithMerchantInfo(merchant);//					}//				}//				break;//							case GlobalParam.MSG_LOADINGFIRST_DATA:				MerchantList list = (MerchantList)msg.obj;								if (list == null) {					return;				}								WeiYuanState state = list.mState;				if(state == null){					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();					return;				}								if (state.errorMsg != null && state.errorMsg.length() > 0)					Toast.makeText(mContext, state.errorMsg,Toast.LENGTH_LONG).show();				if(state.code == 0){					mList.clear();										if (list != null && list.size() > 0) {						if (list.mPageInfo != null) {							mNoMore = list.mPageInfo.hasMore == 0;						}												mList.addAll(list.mMerchantList);					}										showData();				}				break;							case GlobalParam.SHOW_LOADINGMORE_INDECATOR:				if (mListView.getFooterViewsCount() == 0) {					if (mFootView == null) {						mFootView = (LinearLayout) LayoutInflater.from(mContext)								.inflate(R.layout.hometab_listview_footer, null);					}					mListView.addFooterView(mFootView);						ProgressBar pb = (ProgressBar)mFootView.findViewById(R.id.hometab_addmore_progressbar);					pb.setVisibility(View.VISIBLE);		 							TextView more = (TextView)mFootView.findViewById(R.id.hometab_footer_text);					more.setText(BMapApiApp.getInstance().getResources().getString(R.string.add_more_loading));				}		 		getData(GlobalParam.LIST_LOAD_MORE);				break;			case GlobalParam.HIDE_LOADINGMORE_INDECATOR:				list = (MerchantList)msg.obj;								if (list == null) {					return;				}								state = list.mState;								if(state == null){					Toast.makeText(mContext, R.string.commit_data_error,Toast.LENGTH_LONG).show();					return;				}								if (state.errorMsg != null && state.errorMsg.length() > 0)					Toast.makeText(mContext, state.errorMsg,Toast.LENGTH_LONG).show();				if(state.code == 0){					if (list != null && list.size() > 0) {						if (list.mPageInfo != null) {							mNoMore = list.mPageInfo.hasMore == 0;						}												mList.addAll(list.mMerchantList);					}										showData();				}								if (mFootView != null) {					mListView.removeFooterView(mFootView);				}					break;			}		}	};	@Override	public void onClick(View v) {		super.onClick(v);		switch (v.getId()) {		case R.id.left_btn:			this.finish();			break;		case R.id.more_btn:			mPopWindows.showGroupPopView(mPopList,					Gravity.RIGHT,					R.drawable.no_top_arrow_bg,					R.color.white,					0);						break;					case R.id.btn_locate:			mPopChoose.showGroupPopView(mChooseMenuList,					Gravity.LEFT,					R.drawable.no_top_arrow_bg,					R.color.white,					0);			break;					case R.id.btn_choose:			CPAlert.showAlert(					mContext,					"选择城市", 					BMapApiApp.getContryList(),					new OnAlertOkSelectId() {												@Override						public void onOkClick(int whichButton, String state, String city) {							if (city != null ) {								if (city.equals(" - ")) {									mCurrentCity = state;								} else {									mCurrentCity = city;								}							} else if (state != null) {								mCurrentCity = state;							}							getData(GlobalParam.LIST_LOAD_REFERSH);						}					});			break;		default:			break;		}	}		protected void showData() {		if (mAdapter == null) {			mAdapter = new MerchantInfoAdapter(mContext, mList);			mAdapter.setOnMerchantInfoClickListener(this);			mListView.setAdapter(mAdapter);		} else {			mAdapter.notifyDataSetChanged();;		}				showFooter();	}	private void showFooter() {		if (mList.size() == 0) {			mListFooter.setVisibility(View.GONE);		} else {			mListFooter.setVisibility(View.VISIBLE);			mFooterText.setText(String.format("%d个商家", mList.size()));		}	}	@Override	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		super.onActivityResult(requestCode, resultCode, data);		switch (requestCode) {		case RESQUEST_CHOOSE_CITY:			if(data != null && RESULT_OK == resultCode){				Bundle bundle = data.getExtras();				if(bundle != null){					mCurrentCity = data.getStringExtra("city");					getData(GlobalParam.LIST_LOAD_REFERSH);				}			}			break;					case GlobleType.RESQUEST_MAP_LOACTION:			if(data != null && RESULT_OK == resultCode){				Bundle bundle = data.getExtras();				if(bundle != null){					MapInfo mapInfo = (MapInfo)data.getSerializableExtra("mapInfo");										if(mapInfo == null){						Toast.makeText(mContext, mContext.getString(R.string.get_location_failed), Toast.LENGTH_SHORT).show();						return;					}					String city = mapInfo.getCtiy();					mCurrentCity = getBriefOfCity(city);					getData(GlobalParam.LIST_LOAD_REFERSH);				}			}			break;		case REQUEST_MERCHANT_INFO:			if (resultCode == RESULT_OK) {				if (data != null) {					if (data.hasExtra("data")) {						MerchantInfo merchant = (MerchantInfo) data.getExtras().get("data");												if (merchant != null) {							updateLoginWithMerchantInfo(merchant);						}					}				}			}			break;		}	}	private String getBriefOfCity(String city) {		String mCity = city;				if (city.endsWith("市") || city.endsWith("省")) {			mCity = city.substring(0, city.length() - 1);		} else if (city.endsWith("特别行政区")) { 			mCity = city.substring(0, city.length() - 5);		}else {			mCity = city;		}				return mCity;	}	//	private void updateMerchant(final MerchantInfo merchant) {//		if (!WeiYuanCommon.getNetWorkState()) {//			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);//			return;//		}//		new Thread(){//			public void run() {//				try {//					if (merchant.id == 0) {//						WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, //								mContext.getResources().getString(R.string.get_dataing));//						MerchantInfo temp = WeiYuanCommon.getWeiYuanInfo().applyMerchantInfo(merchant);//						mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);//						WeiYuanCommon.sendMsg(mHandler, MSG_MERCHANT_ADDED, temp);//					} else {//						WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, //								mContext.getResources().getString(R.string.get_dataing));//						MerchantInfo temp = WeiYuanCommon.getWeiYuanInfo().editMerchantInfo(merchant);//						mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);//						WeiYuanCommon.sendMsg(mHandler, MSG_MERCHANT_EDITED, temp);//					}//					//				} catch (WeiYuanException e) {//					e.printStackTrace();//					WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR, //							mContext.getResources().getString(e.getStatusCode()));//				} catch (Exception e) {//					e.printStackTrace();//				}//			};//		}.start();		//	}	protected void updateLoginWithMerchantInfo(MerchantInfo merchant) {		Login user = WeiYuanCommon.getLoginResult(mContext);				if (user != null) {			MerchantInfo oldMerchant = user.getMerchantByType(merchantType);						if (oldMerchant == null) {				user.merchantList.add(merchant);				user.isMerchant |= 2 * MERCHANT_TYPE_MASK[merchantType];				merchantInfo = merchant;			} else {				if (merchant.name != null && !merchant.name.isEmpty()) {					oldMerchant.name = merchant.name;				}								if (merchant.address != null && !merchant.address.isEmpty()) {					oldMerchant.address = merchant.address;				}				if (merchant.city != null && !merchant.city.isEmpty()) {					oldMerchant.city = merchant.city;				}								if (merchant.type > 0) {					oldMerchant.type = merchant.type;				}								if (merchant.phone != null && !merchant.phone.isEmpty()) {					oldMerchant.phone = merchant.phone;				}				if (merchant.lat > 0) {					oldMerchant.lat = merchant.lat;				}				if (merchant.lng > 0) {					oldMerchant.lng = merchant.lng;				}								if (merchant.content != null && !merchant.content.isEmpty()) {					oldMerchant.content = merchant.content;				}				if (merchant.categories.length > 0) {					oldMerchant.setCategory(WeiYuanCommon.join(",", merchant.categories));				}				if (merchant.attachment != null && merchant.attachment.size() > 0) {					oldMerchant.setAttachment(merchant.attachment);				}								merchantInfo = oldMerchant;			}			WeiYuanCommon.saveLoginResult(mContext, user);		}	}	@Override	public void onPhone(View v, int Position, String phone) {		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));		if (intent != null) startActivity(intent);	}	@Override	public void onChat(View v, int Position, int uid) {		beginChatWithUid(String.valueOf(uid));	}	private void beginChatWithUid(final String uid) {		if (uid != null && !uid.equals("")) {			if (!WeiYuanCommon.getNetWorkState()) {				mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);				return;			}			new Thread(){				public void run() {					try {						LoginResult user = WeiYuanCommon.getWeiYuanInfo().getUserInfo(uid);						WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_GET_CONTACT_DATA, user);					} catch (WeiYuanException e) {						e.printStackTrace();						WeiYuanCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR, 								mContext.getResources().getString(e.getStatusCode()));					} catch (Exception e) {						e.printStackTrace();					}				};			}.start();		}	}	@Override	public void onItemClick(AdapterView<?> parent, View convertView, int position, long id) {		MerchantInfo merchant = mList.get(position);				Intent intent = new Intent();		intent.setClass(mContext, MerchantGoodsActivity.class);		intent.putExtra("merchant", merchant);		startActivity(intent);	}}