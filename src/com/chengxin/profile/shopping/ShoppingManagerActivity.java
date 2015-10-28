package com.chengxin.profile.shopping;import android.content.Intent;import android.os.Bundle;import android.os.Handler;import android.os.Message;import android.view.View;import android.widget.RelativeLayout;import com.chengxin.BaseActivity;import com.chengxin.R;import com.chengxin.Entity.MerchantMenu;import com.chengxin.global.GlobalParam;import com.chengxin.global.WeiYuanCommon;import com.chengxin.net.WeiYuanException;public class ShoppingManagerActivity extends BaseActivity {	private RelativeLayout mShopInfoLayout;	private RelativeLayout 	mShopGoodsLayout;	private RelativeLayout 	mShopShelfLayout;	private RelativeLayout 	mShopOrderLayout;	private RelativeLayout 	mShopAccountLayout;	protected int mShopType = 0;	@Override	protected void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		mContext = this;		setContentView(R.layout.shop_manager);		initComponent();		getShopCategory();	}	private void getShopCategory() {		if (MerchantMenu.hasData()) {			return;		}				if(!WeiYuanCommon.getNetWorkState()){			mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);			return;		}		new Thread(){			public void run() {				try {					WeiYuanCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG,							mContext.getResources().getString(R.string.get_dataing));					MerchantMenu merchantMenu =	WeiYuanCommon.getWeiYuanInfo().getShopType();					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_SHOW_MERCHANT_MENU_TYPE, merchantMenu);					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);				} catch (WeiYuanException e) {					e.printStackTrace();					WeiYuanCommon.sendMsg(mHandler, GlobalParam.MSG_TICE_OUT_EXCEPTION,							mContext.getResources().getString(e.getStatusCode()));				}catch (Exception e) {					e.printStackTrace();					mHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);				}			};		}.start();	}	private Handler mHandler = new Handler(){		@Override		public void handleMessage(Message msg) {			super.handleMessage(msg);			switch (msg.what) {			case GlobalParam.MSG_SHOW_MERCHANT_MENU_TYPE:				break;			default:				break;			}		}	};		private void initComponent() {		setTitleContent(R.drawable.back_btn, 0, R.string.shop_manager);		mLeftBtn.setOnClickListener(this);				mShopInfoLayout = (RelativeLayout)findViewById(R.id.layout_shop_info);		mShopInfoLayout.setOnClickListener(this);				mShopGoodsLayout = (RelativeLayout)findViewById(R.id.layout_shop_goods);		mShopGoodsLayout.setOnClickListener(this);		mShopShelfLayout = (RelativeLayout)findViewById(R.id.layout_shop_shelf);		mShopShelfLayout.setOnClickListener(this);				mShopOrderLayout = (RelativeLayout)findViewById(R.id.layout_shop_order);		mShopOrderLayout.setOnClickListener(this);				mShopAccountLayout = (RelativeLayout)findViewById(R.id.layout_shop_account);		mShopAccountLayout.setOnClickListener(this);	}	@Override	public void onClick(View v) {		super.onClick(v);				switch (v.getId()) {		case R.id.left_btn:			this.finish();			break;		case R.id.layout_shop_info:			Intent shopIntent = new Intent();			shopIntent.setClass(mContext, ShopInfoActivity.class);			startActivity(shopIntent);			break;		case R.id.layout_shop_goods:			Intent goodsIntent = new Intent();			goodsIntent.setClass(mContext, ShopGoodsActivity.class);			startActivity(goodsIntent);			break;		case R.id.layout_shop_shelf:			Intent shelfIntent = new Intent();			shelfIntent.setClass(mContext, ShopShelfActivity.class);			startActivity(shelfIntent);			break;		case R.id.layout_shop_order:			Intent orderIntent = new Intent();			orderIntent.setClass(mContext, ShopOrderActivity.class);			startActivity(orderIntent);			break;		case R.id.layout_shop_account:			Intent settleIntent = new Intent();			settleIntent.setClass(mContext, ShopSettleActivity.class);			startActivity(settleIntent);			break;		default:			break;		}	}		}