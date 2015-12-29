
package com.ssiot.jurong;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


public class MainFrag extends Fragment {
    private FMainBtnClickListener mFMainBtnClickListener;

    public MainFrag() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mFMainBtnClickListener = (FMainBtnClickListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ju_rong, container, false);
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        ((JuRongActivity) getActivity()).setActionTitle(getResources()
                .getString(R.string.app_title));
        super.onResume();
    }

    private void initViews(View rootView) {
        ImageButton btn_dapeng = (ImageButton) rootView.findViewById(R.id.btn_dapeng);
        ImageButton btn_shuichan = (ImageButton) rootView.findViewById(R.id.btn_shuichan);
        ImageButton btn_datian = (ImageButton) rootView.findViewById(R.id.btn_datian);
        ImageButton btn_moni = (ImageButton) rootView.findViewById(R.id.btn_moni);
        ImageButton btn_ctr = (ImageButton) rootView.findViewById(R.id.btn_ctr);
        ImageButton btn_video = (ImageButton) rootView.findViewById(R.id.btn_video);
        View.OnClickListener listen = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = 0;
                switch (v.getId()) {
                    case R.id.btn_dapeng:
                        index = 1;
                        break;
                    case R.id.btn_shuichan:
                        index = 2;
                        break;
                    case R.id.btn_datian:
                        index = 3;
                        break;
                    case R.id.btn_moni:
                        index = 4;
                        break;
                    case R.id.btn_ctr:
                        index = 5;
                        break;
                    case R.id.btn_video:
                        index = 6;
                        break;

                    default:
                        break;
                }
                if (null != mFMainBtnClickListener) {
                    mFMainBtnClickListener.onFMainBtnClick(index);
                }
            }
        };
        btn_dapeng.setOnClickListener(listen);
        btn_shuichan.setOnClickListener(listen);
        btn_datian.setOnClickListener(listen);
        btn_moni.setOnClickListener(listen);
        btn_ctr.setOnClickListener(listen);
        btn_video.setOnClickListener(listen);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        inflater.inflate(R.menu.menu_f_main, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.action_frag_main_setting:
                if (null != mFMainBtnClickListener){
                    mFMainBtnClickListener.onFMainBtnClick(7);
                }
                break;

            default:
                break;
        }
        return true;
    }

    public interface FMainBtnClickListener {
        void onFMainBtnClick(int index);
    }
}
