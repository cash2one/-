package com.chengxin.services.merchant;import java.util.ArrayList;import java.util.List;import android.annotation.SuppressLint;import android.annotation.TargetApi;import android.os.Build;import android.os.Bundle;import android.os.Parcelable;import android.support.v4.view.PagerAdapter;import android.support.v4.view.ViewPager;import android.support.v4.view.ViewPager.OnPageChangeListener;import android.view.View;import android.view.View.OnClickListener;import android.view.ViewGroup;import android.widget.ImageView;import android.widget.ImageView.ScaleType;import android.widget.LinearLayout;import com.chengxin.BaseActivity;import com.chengxin.R;import com.chengxin.Entity.MerchantGoods;import com.chengxin.Entity.Picture;import com.chengxin.global.FeatureFunction;import com.chengxin.global.ImageLoader;@TargetApi(Build.VERSION_CODES.HONEYCOMB)@SuppressLint("NewApi")public class PictureViewActivity extends BaseActivity {	public static String IMAGE_CACHE_PATH = "imageloader/Cache"; // 图片缓存路径	private static MerchantGoods goods;	private ViewPager adViewPager;	private List<ImageView> imageViews;// 滑动的图片集合	private List<View> dotList;	// 轮播banner的数据	private List< Picture > adList;	// 异步加载图片	private ImageLoader mImageLoader;	private LinearLayout dotLayout;	@Override	protected void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		mContext = this;		setContentView(R.layout.picture_viewer);				if (getIntent().hasExtra("data")) {			// 广告数据			goods = (MerchantGoods)getIntent().getExtras().get("data");		}		// 获取图片加载实例		mImageLoader = new ImageLoader();		initAdData();	}	private void initAdData() {		imageViews = new ArrayList<ImageView>();		dotList = new ArrayList<View>();		adList = getBannerAd();				adViewPager = (ViewPager) findViewById(R.id.vp);		adViewPager.setOnClickListener(new OnClickListener() {						@Override			public void onClick(View v) {				PictureViewActivity.this.finish();			}		});		adViewPager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器		// 设置一个监听器，当ViewPager中的页面改变时调用		adViewPager.setOnPageChangeListener(new MyPageChangeListener());		dotLayout = (LinearLayout) findViewById(R.id.dot_layout);		addDynamicView();	}	private void addDynamicView() {		// 动态添加图片和下面指示的圆点		// 初始化图片资源		for (int i = 0; i < adList.size(); i++) {			// 异步加载图片			try {				ImageView imageView = new ImageView(this);				String url = adList.get(i).originUrl;								if (url != null && url.length() > 4) {					mImageLoader.getBitmap(mContext, imageView, null, url, 0, false, false, false);					imageView.setScaleType(ScaleType.FIT_CENTER);					imageViews.add(imageView);										View dot = new View(this, null, R.style.DotStyle);										if (i == 0) {						dot.setBackgroundResource(R.drawable.dot_focused);					} else {						dot.setBackgroundResource(R.drawable.dot_normal);					}										int r = FeatureFunction.dip2px(mContext, 10);					int margin = FeatureFunction.dip2px(mContext, 2);										dot.setLayoutParams(new ViewGroup.LayoutParams(r, r));										LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dot.getLayoutParams());  					lp.setMargins(margin, 0, margin, 0);  					dot.setLayoutParams(lp);										dotList.add(dot);					dotLayout.addView(dot);				}			} catch (Exception e) {				e.printStackTrace();			}		}	}	private class MyPageChangeListener implements OnPageChangeListener {		private int oldPosition = 0;		@Override		public void onPageScrollStateChanged(int arg0) {		}		@Override		public void onPageScrolled(int arg0, float arg1, int arg2) {		}		@Override		public void onPageSelected(int position) {			dotList.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);			dotList.get(position).setBackgroundResource(R.drawable.dot_focused);			oldPosition = position;		}	}	private class MyAdapter extends PagerAdapter {		@Override		public int getCount() {			return adList.size();		}		@Override		public Object instantiateItem(final ViewGroup container, int position) {			ImageView iv = imageViews.get(position);			((ViewPager) container).addView(iv);			// 在这个方法里面设置图片的点击事件			iv.setOnClickListener(new OnClickListener() {				@Override				public void onClick(View v) {					// 处理跳转逻辑					container.callOnClick();				}			});			return iv;		}		@Override		public void destroyItem(View arg0, int arg1, Object arg2) {			((ViewPager) arg0).removeView((View) arg2);		}		@Override		public boolean isViewFromObject(View arg0, Object arg1) {			return arg0 == arg1;		}		@Override		public void restoreState(Parcelable arg0, ClassLoader arg1) {		}		@Override		public Parcelable saveState() {			return null;		}		@Override		public void startUpdate(View arg0) {		}		@Override		public void finishUpdate(View arg0) {		}	}		public static List< Picture > getBannerAd() {		List< Picture > list = new ArrayList<Picture>();		if (goods != null) {			if (goods.attachment != null && goods.attachment.size() > 0) {				list.addAll(goods.attachment);			}		}				return list;	}}