package com.demo.floatingactionbutton;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.demo.floatingactionbutton.dummy.DummyContent;
import com.demo.floatingactionbutton.dummy.DummyContent.DummyItem;

import java.util.List;

public class TestFragment extends Fragment {

    private static final int STATUS_NORMAL = 1;// 正常状态。无意义
    private static final int STATUS_SHOW = 2;// 显示状态
    private static final int STATUS_DISMISS = 3;// 隐藏状态

    private int mLastScrollY = 0;// 上次滑动时Y的起始坐标
    private int mSlop;
    private transient int currentStatus = STATUS_NORMAL; // 当前Float Button的状态
    private transient boolean isExecutingAnim = false; // 是否正在执行动画
    private RecyclerView recyclerView;
    private ImageView mPostBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_list, container, false);

        // Set the adapter
        mPostBtn = (ImageView) view.findViewById(R.id.post_btn);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new MyTestRecyclerViewAdapter(DummyContent.ITEMS));
        recyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkWhetherExecuteAnimation(event);
                return false;
            }
        });
        return view;
    }
    /**
     * 检查是否为Float button执行动画</br>
     *
     * @param event
     */
    private void checkWhetherExecuteAnimation(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastScrollY = y;
                break;

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
                int deltaY = mLastScrollY - y;
                mLastScrollY = y;

                if (Math.abs(deltaY) < mSlop) {
                    return;
                }
                if (deltaY > 0) {
                    executeAnimation(false);
                } else {
                    executeAnimation(true);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 为Float button执行动画</br>
     *
     * @param show 显示 or 隐藏
     */
    private void executeAnimation(final boolean show) {

        if (isListViewEmpty()) {
            return;
        }

        if (isExecutingAnim || (show && currentStatus == STATUS_SHOW)
                || (!show && currentStatus == STATUS_DISMISS)) {
            return;
        }
        isExecutingAnim = true;
        int moveDis = ((FrameLayout.LayoutParams) (mPostBtn.getLayoutParams())).bottomMargin
                + mPostBtn.getHeight();
        Animation animation = null;
        if (show) {
            animation = new TranslateAnimation(0, 0, moveDis, 0);
        } else {
            animation = new TranslateAnimation(0, 0, 0, moveDis);
        }
        animation.setDuration(300);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isExecutingAnim = false;
                if (show) {
                    currentStatus = STATUS_SHOW;
                } else {
                    currentStatus = STATUS_DISMISS;
                }
                mPostBtn.setClickable(show);
            }
        });
        mPostBtn.startAnimation(animation);
    }

    private boolean isListViewEmpty() {
        // listview中没有数据时不隐藏发布按钮
        return recyclerView.getAdapter().getItemCount() == 0;
    }
}
