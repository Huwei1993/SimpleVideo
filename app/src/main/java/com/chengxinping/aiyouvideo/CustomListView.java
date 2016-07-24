package com.chengxinping.aiyouvideo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * Created by 平瓶平瓶子 on 2016/7/24.
 */
public class CustomListView extends ListView {

    public static int MOD_FORBID = 0;//禁止侧滑模式
    public static int MOD_LEFT = 1;//从左向右划出菜单模式
    private int mode = MOD_FORBID;//当前的模式
    private int leftLength = 0;//左滑菜单的长度
    private int slidePosition;//当前滑动的ListView position
    //手指按下xy的坐标
    private int downX;
    private int downY;

    private View itemView;

    private Scroller scroller; //滑动类
    private int mTouchSlop;//认为是用户滑动的最小距离
    private boolean canMove = false;//判断是否可以侧向滑动
    private boolean isSlided = false;//标识是否完成侧滑


    public CustomListView(Context context) {
        this(context, null);
    }

    public CustomListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    //初始化菜单的划出模式，供外部调用
    public void initSlideMode(int mode) {
        this.mode = mode;
    }

    /**
     * 处理拖动ListView item的逻辑
     *
     * @param ev
     * @return
     */

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        int lastX = (int) ev.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                /* 当前模式不允许滑动，则直接返回，交给ListView自身去处理 */
                if (this.mode == MOD_FORBID) {
                    return super.onTouchEvent(ev);
                }
                //如果处于侧滑完成状态，侧滑回去，并直接返回
                if (isSlided) {
                    scrollBack();
                    return false;
                }
                //假设scroller滚动还没结束，直接返回
                if (!scroller.isFinished()) {
                    return false;
                }
                downX = (int) ev.getX();
                downY = (int) ev.getY();
                slidePosition = pointToPosition(downX, downY);

                //无效的position 不做任何处理
                if (slidePosition == AdapterView.INVALID_POSITION) {
                    return super.onTouchEvent(ev);
                }

                //获取点击的item view
                itemView = getChildAt(slidePosition - getFirstVisiblePosition());

                /*根据设置的滑动模式，自动获取侧滑菜单的长度*/
                if (this.mode == MOD_LEFT) {
                    this.leftLength = -itemView.getPaddingLeft();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!canMove && slidePosition != AdapterView.INVALID_POSITION && (Math.abs(ev.getX() - downX) > mTouchSlop && Math.abs(ev.getY() - downY) < mTouchSlop)) {
                    int offsetX = downX - lastX;
                    if (offsetX < 0 && this.mode == MOD_LEFT) {
                        //从左向右滑动
                        canMove = true;
                    } else {
                        canMove = false;
                    }
                    //此段代码是为了避免我们在侧滑时同时触发ListView的点击事件
                    MotionEvent cancelEvent = MotionEvent.obtain(ev);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL | (ev.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    onTouchEvent(cancelEvent);
                }
                if (canMove) {
                    //设置此属性，可以在侧向滑动时，保持ListView不会上下滚动
                    requestDisallowInterceptTouchEvent(true);

                    //手指拖向itemView滚动，deltaX大于0向左滚动，小于0向右滚动
                    int deltaX = downX - lastX;
                    if (deltaX < 0 && this.mode == MOD_LEFT) {
                        itemView.scrollTo(deltaX, 0);
                    } else {
                        itemView.scrollTo(0, 0);
                    }
                    return true;  //拖动时listView不动
                }
            case MotionEvent.ACTION_UP:
                if (canMove) {
                    canMove = false;
                    scrollByDistanceX();
                }
                break;
        }
        return super.onTouchEvent(ev); //否则交给ListView处理
    }

    /**
     * 根据手指滚动itemView的距离判断是滚动到开啥位置还是向左向右滚动
     */
    private void scrollByDistanceX() {
        //当前模式不允许滑动，直接返回
        if (this.mode == MOD_FORBID) {
            return;
        }
        if (itemView.getScaleX() < 0 && this.mode == MOD_LEFT) {
            //左向右
            if (itemView.getScaleX() <= -leftLength / 2) {
                scrollRight();
            } else {
                //滚回原来的位置
                scrollBack();
            }
        } else {
            scrollBack();
        }
    }

    /**
     * 往右滑动，getScrollX()返回的是左边缘的距离，就是以View左边缘为原点到开始滑动的距离，所以向右边滑动为负值
     */

    private void scrollRight() {
        isSlided = true;
        final int delta = (int) (leftLength + itemView.getScaleX());
        scroller.startScroll(itemView.getScrollX(), 0, -delta, 0, Math.abs(delta));
        postInvalidate();//刷新itemView
    }

    /**
     * 滑动到原来的位置
     */

    private void scrollBack() {
        isSlided = false;
        scroller.startScroll(itemView.getScrollX(), 0, (int) -itemView.getScaleX(), 0, (int) Math.abs(itemView.getScaleX()));
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    public void slideBack() {
        this.scrollBack();
    }

}
