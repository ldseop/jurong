package com.ssiot.jurong.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ssiot.jurong.R;

public class CtrUIView extends RelativeLayout {
    private static final String tag = "CtrUIView";
    private ImageView mImgView = null;
    private TextView mTextView = null;
    CompoundButton mToggle;
    private Context mContext;

    public CtrUIView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.ctr_ui_device, this, true);
        mContext = context;
        mImgView = (ImageView) findViewById(R.id.device_img);
        mTextView = (TextView) findViewById(R.id.device_title);
        setClickable(true);
    }

    public CtrUIView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.ctr_ui_device, this, true);
        // e(index, defaultValue)
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CtrUIView);
        // ta.getAttributeIntValue(R.styleable.IconView_myimg, 0);
        // ta.getInt(R.styleable.IconView_myimg, 0);
        int imgRes = ta.getResourceId(R.styleable.CtrUIView_myjurongbk, 0);
        int txtRes = ta.getResourceId(R.styleable.CtrUIView_myjurongtxt, 0);
        
        ta.recycle();
        mContext = context;
        mImgView = (ImageView) findViewById(R.id.device_img);
        mTextView = (TextView) findViewById(R.id.device_title);
        mToggle = (CompoundButton) findViewById(R.id.device_toggle);
        setClickable(true);
        if (imgRes != 0) {
            mImgView.setImageResource(imgRes);
        } else {
            Log.e(tag, "----!!!!");
        }
        if (txtRes != 0) {
            mTextView.setText(txtRes);
        }
    }

    @SuppressLint("NewApi")
    private void showRefreshAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            final Switch sw = (Switch) findViewById(R.id.device_toggle);
//            sw.setTrackResource(R.drawable.switch_track15);
//            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked){
//                        sw.setTrackResource(R.drawable.switch_track2);
//                    } else {
//                        sw.setTrackResource(R.drawable.switch_track15);
//                    }
//                }
//            });
        }
    }

    // public IconView(Context context, AttributeSet attrs, int defStyle) {
    // super(context, attrs, defStyle);
    // // TODO Auto-generated constructor stub
    // }

    @Override
    protected void drawableStateChanged() {
        boolean pressed = false;
        int[] curStates = getDrawableState();
        for (int i = 0; i < curStates.length; i++) {
            if (android.R.attr.state_pressed == curStates[i]) {
                pressed = true;
                break;
            }
        }
        if (pressed) {
            mImgView.setPressed(true);
        } else {
            mImgView.setPressed(false);
        }
        super.drawableStateChanged();
    }

    public void setImageResource(int resId) {
        mImgView.setImageResource(resId);
    }

    /* 设置文字接口 */
    public void setText(String str) {
        mTextView.setText(str);
    }

    /* 设置文字大小 */
    public void setTextSize(float size) {
        mTextView.setTextSize(size);
    }
    
    public boolean isChecked(){
        if (null != mToggle){
            return mToggle.isChecked();
        }
        return false;
    }
    
    public void setChecked(boolean b){
        if (mToggle != null){
            mToggle.setChecked(b);
        }
    }

}
