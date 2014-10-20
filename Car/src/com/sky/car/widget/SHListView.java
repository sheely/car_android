package com.sky.car.widget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sky.car.R;

/**
 * ListView����ˢ�ºͼ��ظ���<p>
 * 
 * <strong>���˵��:</strong>
 * <p>Ĭ�����������OnRefreshListener�ӿں�OnLoadMoreListener�ӿڣ�<br>���Ҳ�Ϊnull����������������ˡ�
 * <p>ʣ������Flag��
 * <br>mIsAutoLoadMore(�Ƿ��Զ����ظ���)
 * <br>mIsMoveToFirstItemAfterRefresh(����ˢ�º��Ƿ���ʾ��һ��Item)
 * <br>mIsDoRefreshOnWindowFocused(����ListView���ڵĿؼ���ʾ����Ļ��ʱ���Ƿ�ֱ����ʾ����ˢ��...)
 * 
 * <p><strong>�иĽ�������뷢�͵����������~ ��л��λС����ˣ�^_^</strong>
 * 
 * @date 2013-11-11 ����10:09:26
 * @change JohnWatson 
 * @mail xxzhaofeng5412@gmail.com
 * @version 1.0
 */
public class SHListView extends ListView implements OnScrollListener {

	/**  ��ʾ��ʽ������ģ��   */
	private final static String DATE_FORMAT_STR = "yyyy��MM��dd�� HH:mm";
	
	/**  ʵ�ʵ�padding�ľ����������ƫ�ƾ���ı���   */
	private final static int RATIO = 3;
	//===========================����4������Ϊ ����ˢ�µ�״̬��ʶ===============================
	/**  �ɿ�ˢ��   */
	private final static int RELEASE_TO_REFRESH = 0;
	/**  ����ˢ��   */
	private final static int PULL_TO_REFRESH = 1;
	/**  ����ˢ��   */
	private final static int REFRESHING = 2;
	/**  ˢ�����   or ʲô��û�����ָ�ԭ״̬��  */
	private final static int DONE = 3;
	//===========================����3������Ϊ ���ظ����״̬��ʶ===============================
	/**  ������   */
	private final static int ENDINT_LOADING = 1;
	/**  �ֶ����ˢ��   */
	private final static int ENDINT_MANUAL_LOAD_DONE = 2;
	/**  �Զ����ˢ��   */
	private final static int ENDINT_AUTO_LOAD_DONE = 3;
	
	/**
	 * <strong>����ˢ��HeadView��ʵʱ״̬flag</strong>
	 *     
	 * <p> 0 : RELEASE_TO_REFRESH;
	 * <p> 1 : PULL_To_REFRESH;
	 * <p> 2 : REFRESHING;
	 * <p> 3 : DONE;
	 * 
	 */
	private int mHeadState;
	/**  
	 * <strong>���ظ���FootView��EndView����ʵʱ״̬flag</strong>
	 * 
	 * <p> 0 : ���/�ȴ�ˢ�� ;
	 * <p> 1 : ������  
	 */
	private int mEndState;
	
	// ================================= ��������Flag ================================
	
	/**  ���Լ��ظ��ࣿ   */
	private boolean mCanLoadMore = false;
	/**  ��������ˢ�£�   */
	private boolean mCanRefresh = false;
	/**  �����Զ����ظ����𣿣�ע�⣬���ж��Ƿ��м��ظ��࣬���û�У����flagҲû�����壩   */
	private boolean mIsAutoLoadMore = false;
	/**  ����ˢ�º��Ƿ���ʾ��һ��Item    */
	private boolean mIsMoveToFirstItemAfterRefresh = false;
	/**  ����ListView���ڵĿؼ���ʾ����Ļ��ʱ���Ƿ�ֱ����ʾ����ˢ��...   */
	private boolean mIsDoRefreshOnUIChanged = false;

	private int total;
	
	
	public void setTotal(int total) {
		this.total = total;
	}

	public boolean isCanLoadMore() {
		return mCanLoadMore;
	}
	
	public void setCanLoadMore(boolean pCanLoadMore) {
		mCanLoadMore = pCanLoadMore;
		if(mCanLoadMore && getFooterViewsCount() == 0){
			addFooterView();
		}
	}
	
	public boolean isCanRefresh() {
		return mCanRefresh;
	}
	
	public void setCanRefresh(boolean pCanRefresh) {
		mCanRefresh = pCanRefresh;
	}
	
	public boolean isAutoLoadMore() {
		return mIsAutoLoadMore;
	}

	public void setAutoLoadMore(boolean pIsAutoLoadMore) {
		mIsAutoLoadMore = pIsAutoLoadMore;
	}
		
	public boolean isMoveToFirstItemAfterRefresh() {
		return mIsMoveToFirstItemAfterRefresh;
	}

	public void setMoveToFirstItemAfterRefresh(
			boolean pIsMoveToFirstItemAfterRefresh) {
		mIsMoveToFirstItemAfterRefresh = pIsMoveToFirstItemAfterRefresh;
	}
	
	public boolean isDoRefreshOnUIChanged() {
		return mIsDoRefreshOnUIChanged;
	}

	public void setDoRefreshOnUIChanged(boolean pIsDoRefreshOnWindowFocused) {
		mIsDoRefreshOnUIChanged = pIsDoRefreshOnWindowFocused;
	}
	// ============================================================================

	private LayoutInflater mInflater;

	private LinearLayout mHeadRootView;
	private TextView mTipsTextView;
	private TextView mLastUpdatedTextView;
	private ImageView mArrowImageView;
	private ProgressBar mProgressBar;
	
	private View mEndRootView;
	private ProgressBar mEndLoadProgressBar;
	private TextView mEndLoadTipsTextView;

	/**  headView����   */
	private RotateAnimation mArrowAnim;
	/**  headView��ת����   */
	private RotateAnimation mArrowReverseAnim;
 
	/** ���ڱ�֤startY��ֵ��һ��������touch�¼���ֻ����¼һ��    */
	private boolean mIsRecored;

	private int mHeadViewWidth;
	private int mHeadViewHeight;

	private int mStartY;
	private boolean mIsBack;
	
	private int mFirstItemIndex;
	private int mLastItemIndex;
	private int mCount;
	@SuppressWarnings("unused")
	private boolean mEnoughCount;//�㹻����������Ļ�� 
	
	private OnRefreshListener mRefreshListener;
	private OnLoadMoreListener mLoadMoreListener;

	private String mLabel;
	
	public String getLabel() {
		return mLabel;
	}

	public void setLabel(String pLabel) {
		mLabel = pLabel;
	}

	public SHListView(Context pContext) {
		super(pContext);
		init(pContext);
	}
	
	public SHListView(Context pContext, AttributeSet pAttrs) {
		super(pContext, pAttrs);
		init(pContext);
	}

	public SHListView(Context pContext, AttributeSet pAttrs, int pDefStyle) {
		super(pContext, pAttrs, pDefStyle);
		init(pContext);
	}

	/**
	 * ��ʼ������
	 * @param pContext 
	 * @date 2013-11-20 ����4:10:46
	 * @change JohnWatson
	 * @version 1.0
	 */
	private void init(Context pContext) {
//		final ViewConfiguration _ViewConfiguration = ViewConfiguration.get(pContext);
//		mTouchSlop = _ViewConfiguration.getScaledTouchSlop();
		
		setCacheColorHint(pContext.getResources().getColor(R.color.full_transparent));
		setOnLongClickListener(null);
		mInflater = LayoutInflater.from(pContext);

		addHeadView();
		
		setOnScrollListener(this);

		initPullImageAnimation(0);
	}

	/**
	 * �������ˢ�µ�HeadView 
	 * @date 2013-11-11 ����9:48:26
	 * @change JohnWatson
	 * @version 1.0
	 */
	private void addHeadView() {
		mHeadRootView = (LinearLayout) mInflater.inflate(R.layout.pull_to_refresh_head, null);

		mArrowImageView = (ImageView) mHeadRootView
				.findViewById(R.id.head_arrowImageView);
		mArrowImageView.setMinimumWidth(70);
		mArrowImageView.setMinimumHeight(50);
		mProgressBar = (ProgressBar) mHeadRootView
				.findViewById(R.id.head_progressBar);
		mTipsTextView = (TextView) mHeadRootView.findViewById(
				R.id.head_tipsTextView);
		mLastUpdatedTextView = (TextView) mHeadRootView
				.findViewById(R.id.head_lastUpdatedTextView);

		measureView(mHeadRootView);
		mHeadViewHeight = mHeadRootView.getMeasuredHeight();
		mHeadViewWidth = mHeadRootView.getMeasuredWidth();
		
		mHeadRootView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
		mHeadRootView.invalidate();

		Log.v("size", "width:" + mHeadViewWidth + " height:"
				+ mHeadViewHeight);

		addHeaderView(mHeadRootView, null, false);
		
		mHeadState = DONE;
		changeHeadViewByState();
	}
	
	/**
	 * ��Ӽ��ظ���FootView
	 * @date 2013-11-11 ����9:52:37
	 * @change JohnWatson
	 * @version 1.0
	 */
	private void addFooterView() {
		mEndRootView = mInflater.inflate(R.layout.pull_to_refresh_load_more, null);
		mEndRootView.setVisibility(View.VISIBLE);
		mEndLoadProgressBar = (ProgressBar) mEndRootView
				.findViewById(R.id.pull_to_refresh_progress);
		mEndLoadTipsTextView = (TextView) mEndRootView.findViewById(R.id.load_more);
		mEndRootView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mCanLoadMore){
					if(mCanRefresh){
						// ����������ˢ��ʱ�����FootViewû�����ڼ��أ�����HeadViewû������ˢ�£��ſ��Ե�����ظ��ࡣ
						if(mEndState != ENDINT_LOADING && mHeadState != REFRESHING){
							mEndState = ENDINT_LOADING;
							onLoadMore();
						}
					}else if(mEndState != ENDINT_LOADING){
						// ����������ˢ��ʱ��FootView�����ڼ���ʱ���ſ��Ե�����ظ��ࡣ
						mEndState = ENDINT_LOADING;
						onLoadMore();
					}
				}
			}
		});
		
		addFooterView(mEndRootView);
		
		if(mIsAutoLoadMore){
			mEndState = ENDINT_AUTO_LOAD_DONE;
		}else{
			mEndState = ENDINT_MANUAL_LOAD_DONE;
		}
	}

	/**
	 * ʵ��������ˢ�µļ�ͷ�Ķ���Ч�� 
	 * @param pAnimDuration ��������ʱ��
	 * @date 2013-11-20 ����11:53:22
	 * @change JohnWatson
	 * @version 1.0
	 */
	private void initPullImageAnimation(final int pAnimDuration) {
		
		int _Duration;
		
		if(pAnimDuration > 0){
			_Duration = pAnimDuration;
		}else{
			_Duration = 250;
		}
//		Interpolator _Interpolator;
//		switch (pAnimType) {
//		case 0:
//			_Interpolator = new AccelerateDecelerateInterpolator();
//			break;
//		case 1:
//			_Interpolator = new AccelerateInterpolator();
//			break;
//		case 2:
//			_Interpolator = new AnticipateInterpolator();
//			break;
//		case 3:
//			_Interpolator = new AnticipateOvershootInterpolator();
//			break;
//		case 4:
//			_Interpolator = new BounceInterpolator();
//			break;
//		case 5:
//			_Interpolator = new CycleInterpolator(1f);
//			break;
//		case 6:
//			_Interpolator = new DecelerateInterpolator();
//			break;
//		case 7:
//			_Interpolator = new OvershootInterpolator();
//			break;
//		default:
//			_Interpolator = new LinearInterpolator();
//			break;
//		}
		
		Interpolator _Interpolator = new LinearInterpolator();
		
		mArrowAnim = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mArrowAnim.setInterpolator(_Interpolator);
		mArrowAnim.setDuration(_Duration);
		mArrowAnim.setFillAfter(true);

		mArrowReverseAnim = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mArrowReverseAnim.setInterpolator(_Interpolator);
		mArrowReverseAnim.setDuration(_Duration);
		mArrowReverseAnim.setFillAfter(true);
	}

	/**
	 * ����HeadView���(ע�⣺�˷�����������LinearLayout��������Լ�������֤��)
	 * @param pChild 
	 * @date 2013-11-20 ����4:12:07
	 * @change JohnWatson
	 * @version 1.0
	 */
	private void measureView(View pChild) {
		ViewGroup.LayoutParams p = pChild.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;

		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		pChild.measure(childWidthSpec, childHeightSpec);
	}
	
	/**
	 *Ϊ���жϻ�����ListView�ײ�û
	 */
	@Override
	public void onScroll(AbsListView pView, int pFirstVisibleItem,
			int pVisibleItemCount, int pTotalItemCount) {
//		System.out.println("onScroll . pFirstVisibleItem = "+pFirstVisibleItem);
		mFirstItemIndex = pFirstVisibleItem;
		mLastItemIndex = pFirstVisibleItem + pVisibleItemCount - 2;
		mCount = pTotalItemCount - 2;
		if (pTotalItemCount > pVisibleItemCount ) {
			mEnoughCount = true;
//			endingView.setVisibility(View.VISIBLE);
		} else {
			mEnoughCount = false;
		}
	}

	/**
	 *��������������е��ң���Ҷ������������ˡ�
	 */
	@Override
	public void onScrollStateChanged(AbsListView pView, int pScrollState) {
		if(mCanLoadMore){// ���ڼ��ظ��๦��
			if (mLastItemIndex ==  mCount && pScrollState == SCROLL_STATE_IDLE) {
				//SCROLL_STATE_IDLE=0������ֹͣ
				
				/**
				 * �����
				 */
				if(mLastItemIndex == total){
					mEndRootView.setVisibility(View.GONE);
					return;
				}
				if (mEndState != ENDINT_LOADING) {
					if(mIsAutoLoadMore){// �Զ����ظ��࣬������FootView��ʾ ����    �ࡱ
						if(mCanRefresh){
							// ��������ˢ�²���HeadViewû������ˢ��ʱ��FootView�����Զ����ظ��ࡣ
							if(mHeadState != REFRESHING){
								// FootView��ʾ : ��    ��  ---> ������...
								mEndState = ENDINT_LOADING;
								onLoadMore();
								changeEndViewByState();
							}
						}else{// û������ˢ�£�����ֱ�ӽ��м��ظ��ࡣ
							// FootView��ʾ : ��    ��  ---> ������...
							mEndState = ENDINT_LOADING;
							onLoadMore();
							changeEndViewByState();
						}
					}else{// �����Զ����ظ��࣬������FootView��ʾ ��������ء�
						// FootView��ʾ : �������  ---> ������...
						mEndState = ENDINT_MANUAL_LOAD_DONE;
						changeEndViewByState();
					}
				}
			}
		}else if(mEndRootView != null && mEndRootView.getVisibility() == VISIBLE){
			// ͻȻ�رռ��ظ��๦��֮������Ҫ�Ƴ�FootView��
			System.out.println("this.removeFooterView(endRootView);...");
			mEndRootView.setVisibility(View.GONE);
			this.removeFooterView(mEndRootView);
		}
	}

	/**
	 * �ı���ظ���״̬
	 * @date 2013-11-11 ����10:05:27
	 * @change JohnWatson
	 * @version 1.0
	 */
	private void  changeEndViewByState() {
		if (mCanLoadMore) {
			//������ظ���
			switch (mEndState) {
			case ENDINT_LOADING://ˢ����
				
				// ������...
				if(mEndLoadTipsTextView.getText().equals(
						"������...")){
					break;
				}
				mEndLoadTipsTextView.setText("������...");
				mEndLoadTipsTextView.setVisibility(View.VISIBLE);
				mEndLoadProgressBar.setVisibility(View.VISIBLE);
				break;
			case ENDINT_MANUAL_LOAD_DONE:// �ֶ�ˢ�����
				
				// �������
				mEndLoadTipsTextView.setText("�������");
				mEndLoadTipsTextView.setVisibility(View.VISIBLE);
				mEndLoadProgressBar.setVisibility(View.GONE);
				
				mEndRootView.setVisibility(View.VISIBLE);
				
				break;
			case ENDINT_AUTO_LOAD_DONE:// �Զ�ˢ�����
				
				// ��    ��
				mEndLoadTipsTextView.setText("��  ��");
				mEndLoadTipsTextView.setVisibility(View.VISIBLE);
				mEndLoadProgressBar.setVisibility(View.GONE);
				mEndRootView.setVisibility(View.VISIBLE);
				break;
			default:
				// ԭ���Ĵ�����Ϊ�ˣ� ������item�ĸ߶�С��ListView����ĸ߶�ʱ��
				// Ҫ���ص�FootView������Լ�ȥԭ���ߵĴ���ο���
				
//				if (enoughCount) {					
//					endRootView.setVisibility(View.VISIBLE);
//				} else {
//					endRootView.setVisibility(View.GONE);
//				}
				break;
			}
		}
	}
	/**
	 * *****����ע����� �˷�����������ViewPager�У���ΪviewpagerĬ��ʵ�������ڵ�item��View
	 *  ���飺 ��Ƕ�׵�ʱ�򣬿��Է������������ʹ�ã�Ч�����ǣ��������ֱ��ˢ�¡�����ˢ�µĿ������������Լ�������
	 *  ����Ϊ��ֱ�ӵ���pull2RefreshManually();
	 */
	@Override
	public void onWindowFocusChanged(boolean pHasWindowFocus) {
		super.onWindowFocusChanged(pHasWindowFocus);
		if(mIsDoRefreshOnUIChanged){
			if(pHasWindowFocus){
				pull2RefreshManually();
			}
		}
	}
	
	/**
	 * ����ListView���ڵĿؼ���ʾ����Ļ��ʱ��ֱ����ʾ����ˢ��...
	 * @date 2013-11-23 ����11:26:10
	 * @author JohnWatson
	 * @version 1.0
	 */
	public void pull2RefreshManually(){
		mHeadState = REFRESHING;
		changeHeadViewByState();
		onRefresh();
		
		mIsRecored = false;
		mIsBack = false;
	}
	
	/**
	 *ԭ���ߵģ���û�Ķ�������������Ż���
	 */
	public boolean onTouchEvent(MotionEvent event) {
		
		if (mCanRefresh) {
			if(mCanLoadMore && mEndState == ENDINT_LOADING){
				// ������ڼ��ظ��๦�ܣ����ҵ�ǰ���ڼ����У�Ĭ�ϲ���������ˢ�£����������Ϻ�����ˢ�²���ʹ�á�
				return super.onTouchEvent(event);
			}
			
			switch (event.getAction()) {
			
			case MotionEvent.ACTION_DOWN:
				
				if (mFirstItemIndex == 0 && !mIsRecored) {
					mIsRecored = true;
					mStartY = (int) event.getY();
//					MyLogger.showLogWithLineNum(5, "mFirstItemIndex == 0 && !mIsRecored mStartY = "+mStartY);
				}else if(mFirstItemIndex == 0 && mIsRecored){
					// ˵���ϴε�Touch�¼�ִֻ����Down������Ȼ��ֱ�ӱ����������ˡ�
					// ��ô��Ҫ���¸�mStartY��ֵ����
//					MyLogger.showLogWithLineNum(5, "mFirstItemIndex = "+mFirstItemIndex+"__!mIsRecored = "+!mIsRecored);
					mStartY = (int) event.getY();
				}

				break;

			case MotionEvent.ACTION_UP:
				
				if (mHeadState != REFRESHING) {
					
					if (mHeadState == DONE) {
						
					}
					if (mHeadState == PULL_TO_REFRESH) {
						// �����ֵ�ʱ�����HeadView��ʾ����ˢ�£��Ǿͻָ�ԭ״̬��
						mHeadState = DONE;
						changeHeadViewByState();
					}
					if (mHeadState == RELEASE_TO_REFRESH) {
						// �����ֵ�ʱ�����HeadView��ʾ�ɿ�ˢ�£��Ǿ���ʾ����ˢ�¡�
						mHeadState = REFRESHING;
						changeHeadViewByState();
						onRefresh();
					}
				}

				mIsRecored = false;
				mIsBack = false;
				
				break;

			case MotionEvent.ACTION_MOVE:
				
				int _TempY = (int)event.getY();

				if (!mIsRecored && mFirstItemIndex == 0) {
					mIsRecored = true;
					mStartY = _TempY;
//					MyLogger.showLogWithLineNum(4, "!mIsRecored && mFirstItemIndex == 0 and __mStartY = "+mStartY);
				}

				if (mHeadState != REFRESHING && mIsRecored) {

					// ��֤������padding�Ĺ����У���ǰ��λ��һֱ����head��
					// ����������б�����Ļ�Ļ����������Ƶ�ʱ���б��ͬʱ���й���
					// ��������ȥˢ����
					if (mHeadState == RELEASE_TO_REFRESH) {

						setSelection(0);
						
						// �������ˣ��Ƶ�����Ļ�㹻�ڸ�head�ĳ̶ȣ����ǻ�û���Ƶ�ȫ���ڸǵĵز�
						if (((_TempY - mStartY) / RATIO < mHeadViewHeight)
								&& (_TempY - mStartY) > 0) {
							mHeadState = PULL_TO_REFRESH;
							changeHeadViewByState();
						}
						// һ�����Ƶ�����
						else if (_TempY - mStartY <= 0) {
							mHeadState = DONE;
							changeHeadViewByState();
						}
						// �������ˣ����߻�û�����Ƶ���Ļ�����ڸ�head�ĵز�
					}
					// ��û�е�����ʾ�ɿ�ˢ�µ�ʱ��,DONE������PULL_To_REFRESH״̬
					if (mHeadState == PULL_TO_REFRESH) {

						setSelection(0);

						// ���������Խ���RELEASE_TO_REFRESH��״̬
						if ((_TempY - mStartY) / RATIO >= mHeadViewHeight) {
							mHeadState = RELEASE_TO_REFRESH;
							mIsBack = true;
							changeHeadViewByState();
						} else if (_TempY - mStartY <= 0) {
//							System.out.println("mHeadState == PULL_TO_REFRESH _TempY = "+_TempY+"__mStartY = "+mStartY);
							mHeadState = DONE;
							changeHeadViewByState();
						}
					}

					if (mHeadState == DONE) {
						if (_TempY - mStartY > 0) {
//							System.out.println("mHeadState == DONE ... _TempY - mStartY = "+(_TempY - mStartY));
							mHeadState = PULL_TO_REFRESH;
							changeHeadViewByState();
						}
					}

					if (mHeadState == PULL_TO_REFRESH) {
						mHeadRootView.setPadding(0, -1 * mHeadViewHeight
								+ (_TempY - mStartY) / RATIO, 0, 0);

					}

					if (mHeadState == RELEASE_TO_REFRESH) {
						mHeadRootView.setPadding(0, (_TempY - mStartY) / RATIO
								- mHeadViewHeight, 0, 0);
					}
				}
				break;
			}
		}

		return super.onTouchEvent(event);
	}
	
	/**
	 * ��HeadView״̬�ı�ʱ�򣬵��ø÷������Ը��½���
	 * @date 2013-11-20 ����4:29:44
	 * @change JohnWatson
	 * @version 1.0
	 */
	private void changeHeadViewByState() {
		switch (mHeadState) {
		case RELEASE_TO_REFRESH:
			mArrowImageView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
			mTipsTextView.setVisibility(View.VISIBLE);
			mLastUpdatedTextView.setVisibility(View.VISIBLE);

			mArrowImageView.clearAnimation();
			mArrowImageView.startAnimation(mArrowAnim);
			// �ɿ�ˢ��
			mTipsTextView.setText("�ɿ�ˢ��");

			break;
		case PULL_TO_REFRESH:
			mProgressBar.setVisibility(View.GONE);
			mTipsTextView.setVisibility(View.VISIBLE);
			mLastUpdatedTextView.setVisibility(View.VISIBLE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.VISIBLE);
			// ����RELEASE_To_REFRESH״̬ת������
			if (mIsBack) {
				mIsBack = false;
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(mArrowReverseAnim);
				// ����ˢ��
				mTipsTextView.setText("����ˢ��");
			} else {
				// ����ˢ��
				mTipsTextView.setText("����ˢ��");
			}
			break;

		case REFRESHING:
			
			changeHeaderViewRefreshState();
			break;
		case DONE:
			
			mHeadRootView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
			
			mProgressBar.setVisibility(View.GONE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setImageResource(R.drawable.arrow);
			// ����ˢ��
			mTipsTextView.setText("����ˢ��");
			mLastUpdatedTextView.setVisibility(View.VISIBLE);
			System.out.println("getLastVisiblePosition:"+getLastVisiblePosition());
			break;
		}
	}

	/**
	 * �ı�HeadView��ˢ��״̬�µ���ʾ
	 * @date 2013-11-23 ����10:49:00
	 * @author JohnWatson
	 * @version 1.0
	 */
	private void changeHeaderViewRefreshState(){
		mHeadRootView.setPadding(0, 0, 0, 0);
		
		// �����Ľ��飺 ʵ���������setPadding�����ö��������档��û���ԣ������Ҽ�������ʵ�е���Ҳ��Scroller����ʵ�����Ч����
		// ��ûʱ���о��ˣ���������չ�������������С���������~ ����Ľ��˼ǵ÷�����������~
		// �������䣺 xxzhaofeng5412@gmail.com
		
		mProgressBar.setVisibility(View.VISIBLE);
		mArrowImageView.clearAnimation();
		mArrowImageView.setVisibility(View.GONE);
		// ����ˢ��...
		mTipsTextView.setText("����ˢ��...");
		mLastUpdatedTextView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * ����ˢ�¼����ӿ�
	 * @date 2013-11-20 ����4:50:51
	 * @change JohnWatson
	 * @version 1.0
	 */
	public interface OnRefreshListener {
		public void onRefresh();
	}
	
	/**
	 * ���ظ�������ӿ�
	 * @date 2013-11-20 ����4:50:51
	 * @change JohnWatson
	 * @version 1.0
	 */
	public interface OnLoadMoreListener {
		public void onLoadMore();
	}
	
	public void setOnRefreshListener(OnRefreshListener pRefreshListener) {
		if(pRefreshListener != null){
			mRefreshListener = pRefreshListener;
			mCanRefresh = true;
		}
	}

	public void setOnLoadListener(OnLoadMoreListener pLoadMoreListener) {
		if(pLoadMoreListener != null){
			mLoadMoreListener = pLoadMoreListener;
			mCanLoadMore = true;
			if(mCanLoadMore && getFooterViewsCount() == 0){
				addFooterView();
			}
		}
	}
	
	/**
	 * ��������ˢ��
	 * @date 2013-11-20 ����4:45:47
	 * @change JohnWatson
	 * @version 1.0
	 */
	private void onRefresh() {
		if (mRefreshListener != null) {
			mRefreshListener.onRefresh();
		}
	}
	
	/**
	 * ����ˢ�����
	 * @date 2013-11-20 ����4:44:12
	 * @change JohnWatson
	 * @version 1.0
	 */
	public void onRefreshComplete() {
				
		mHeadState = DONE;
		// �������: Time
		mLastUpdatedTextView.setText(
				"������£�" + 
				new SimpleDateFormat(DATE_FORMAT_STR, Locale.CHINA).format(new Date()));
		changeHeadViewByState();
		
		// ����ˢ�º��Ƿ���ʾ��һ��Item
		if (mIsMoveToFirstItemAfterRefresh) {
			mFirstItemIndex = 0;
			setSelection(0);
		}
	}

	/**
	 * ���ڼ��ظ��࣬FootView��ʾ �� ������...
	 * @date 2013-11-20 ����4:35:51
	 * @change JohnWatson
	 * @version 1.0
	 */
	private void onLoadMore() {
		if (mLoadMoreListener != null) {
			// ������...
			mEndLoadTipsTextView.setText("������...");
			mEndLoadTipsTextView.setVisibility(View.VISIBLE);
			mEndLoadProgressBar.setVisibility(View.VISIBLE);
			
			mLoadMoreListener.onLoadMore();
		}
	}

	/**
	 * ���ظ������ 
	 * @date 2013-11-11 ����10:21:38
	 * @change JohnWatson
	 * @version 1.0
	 */
	public void onLoadMoreComplete() {
		if(mIsAutoLoadMore){
			mEndState = ENDINT_AUTO_LOAD_DONE;
		}else{
			mEndState = ENDINT_MANUAL_LOAD_DONE;
		}
		changeEndViewByState();
	}
	
	/**
	 * ��Ҫ����һ��ˢ��ʱ������
	 * @param adapter
	 * @date 2013-11-20 ����5:35:51
	 * @change JohnWatson
	 * @version 1.0
	 */
	public void setAdapter(BaseAdapter adapter) {
		// �������: Time
		mLastUpdatedTextView.setText("������£�" + 
				new SimpleDateFormat(DATE_FORMAT_STR, Locale.CHINA).format(new Date()));
		super.setAdapter(adapter);
	}
	
}
