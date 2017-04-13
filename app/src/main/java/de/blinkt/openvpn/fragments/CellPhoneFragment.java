package de.blinkt.openvpn.fragments;

/**
 * Created by Administrator on 2016/9/8 0008.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.androidannotations.annotations.App;

import java.lang.reflect.Field;
import java.util.ArrayList;

import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.CellPhoneFragmentPagerAdapter;
import cn.com.johnson.model.AppMode;
import de.blinkt.openvpn.activities.ProMainActivity;
import de.blinkt.openvpn.activities.SMSAcivity;
import de.blinkt.openvpn.util.ViewUtil;

import static de.blinkt.openvpn.constant.UmengContant.CLICKEDITSMS;
import static de.blinkt.openvpn.constant.UmengContant.CLICKTITLEPHONE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKTITLESMS;


public class CellPhoneFragment extends Fragment {
    public static RadioGroup operation_rg;
    RadioButton cell_phone_rb;
    RadioButton message_rb;
    /**
     * 拨打电话标题
     */
    public static FrameLayout dial_tittle_fl;
    TextView editTv;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    Activity activity;
    ViewPager mViewPager;
    //悬浮按钮
    public static ImageView floatingActionButton;
    public static boolean isForeground = false;
    Drawable drawable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.cell_phone_fragment, container, false);
        initView(view);
        addListener();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isForeground = isVisibleToUser;
        if (isVisibleToUser) {

            operation_rg.check(cell_phone_rb.getId());
            ClickPhone();

            hidePhoneBottomBar();
        }

    }

    @Override
    public void onDestroy() {
        operation_rg = null;
        phoneFragment = null;
        dial_tittle_fl = null;
        floatingActionButton = null;
        super.onDestroy();
    }

    public void hidePhoneBottomBar() {
        ProMainActivity.bottom_bar_linearLayout.setVisibility(View.VISIBLE);
        ProMainActivity.phone_linearLayout.setVisibility(View.GONE);
    }

    public void showPhoneBottomBar() {
        ProMainActivity.bottom_bar_linearLayout.setVisibility(View.GONE);
        ProMainActivity.phone_linearLayout.setVisibility(View.VISIBLE);
    }

    private void addListener() {
        editTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //友盟方法统计
                MobclickAgent.onEvent(getActivity(), CLICKEDITSMS);
                Intent intent = new Intent(getActivity(), SMSAcivity.class);
                startActivity(intent);
            }
        });

        /**
         * 悬浮按钮事件
         */
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionButton.setVisibility(View.GONE);
                //展示电话键
                ViewUtil.showView(phoneFragment.t9dialpadview);
                hidePhoneBottomBar();
                if (ProMainActivity.phone_linearLayout.getVisibility() == View.GONE || ProMainActivity.phone_linearLayout.getVisibility() == View.INVISIBLE) {
                    ProMainActivity.phone_linearLayout.setVisibility(View.VISIBLE);
                    ProMainActivity.bottom_bar_linearLayout.setVisibility(View.GONE);
                }

             /*   if (null != AppMode.getInstance().curCharacter) {
                    if (AppMode.getInstance().curCharacter.length() > 0) {
                        showPhoneBottomBar();
                    }
                }*/
            }
        });


    }

    private void initView(View view) {
        operation_rg = ((RadioGroup) view.findViewById(R.id.operation_rg));
        cell_phone_rb = ((RadioButton) view.findViewById(R.id.cell_phone_rb));
        //拨打电话标题
        dial_tittle_fl = (FrameLayout) view.findViewById(R.id.dial_tittle_fl);
        message_rb = ((RadioButton) view.findViewById(R.id.message_rb));
        editTv = ((TextView) view.findViewById(R.id.edit_tv));
        mViewPager = (ViewPager) view.findViewById(R.id.mViewPager);

        //悬浮按钮
        floatingActionButton = (ImageView) view.findViewById(R.id.floatingActionButton);
        initFragment();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    ClickPhone();

                } else {
                    ClickMessage();

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //初始化标题下标的小三角
        drawable = getActivity().getResources().getDrawable(R.drawable.image_slidethetriangle);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        this.activity = activity;
        super.onAttach(activity);
    }

    public void setFragment_Phone(Fragment_Phone phoneFragment) {
        CellPhoneFragment.phoneFragment = phoneFragment;
    }

    static Fragment_Phone phoneFragment;

    private void initFragment() {
        fragments.clear();//清空
        fragments.add(phoneFragment);
        fragments.add(new SmsFragment());
        CellPhoneFragmentPagerAdapter mAdapter = new CellPhoneFragmentPagerAdapter(getChildFragmentManager(), fragments);
        mAdapter.setFragments(fragments);
        mViewPager.setAdapter(mAdapter);
        operation_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.cell_phone_rb:
                        //友盟方法统计
                        MobclickAgent.onEvent(getActivity(), CLICKTITLEPHONE);
                        ClickPhone();
                        break;
                    case R.id.message_rb:
                        //友盟方法统计
                        MobclickAgent.onEvent(getActivity(), CLICKTITLESMS);
                        ClickMessage();
                        break;
                }
            }
        });
    }

    private void ClickMessage() {
        if (dial_tittle_fl.getVisibility() == View.VISIBLE) {
            dial_tittle_fl.setVisibility(View.GONE);
            operation_rg.setVisibility(View.VISIBLE);

        }

        message_rb.setCompoundDrawables(null, null, null, drawable);
        cell_phone_rb.setCompoundDrawables(null, null, null, null);
        mViewPager.setCurrentItem(1);
        editTv.setVisibility(View.VISIBLE);
        floatingActionButton.setVisibility(View.GONE);
    }

    private void ClickPhone() {
        if (Fragment_Phone.t9dialpadview != null) {
            if (Fragment_Phone.t9dialpadview.getT9Input() != null && Fragment_Phone.t9dialpadview.getT9Input().length() > 0) {

                if (dial_tittle_fl != null && operation_rg != null) {
                    dial_tittle_fl.setVisibility(View.VISIBLE);
                    operation_rg.setVisibility(View.GONE);
                }
            }

        }
/**
 * 监听ViewPage的状态变化，控制是否滑动
 */
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        mViewPager.requestDisallowInterceptTouchEvent(false);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        mViewPager.requestDisallowInterceptTouchEvent(false);
                    default:
                        break;
                }
                if (dial_tittle_fl.getVisibility() == View.VISIBLE) {

                    return true;
                } else {
                    return false;
                }

            }
        });

        /**
         * \根据键盘的显示来实现控件的显示或则隐藏
         */
        if (phoneFragment != null && phoneFragment.t9dialpadview != null && phoneFragment.t9dialpadview.getVisibility() == View.VISIBLE) {
            floatingActionButton.setVisibility(View.GONE);
            ProMainActivity.bottom_bar_linearLayout.setVisibility(View.GONE);
            ProMainActivity.phone_linearLayout.setVisibility(View.VISIBLE);
        } else {

            floatingActionButton.setVisibility(View.VISIBLE);
        }
        message_rb.setCompoundDrawables(null, null, null, null);
        cell_phone_rb.setCompoundDrawables(null, null, null, drawable);
        editTv.setVisibility(View.GONE);
        mViewPager.setCurrentItem(0);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}
