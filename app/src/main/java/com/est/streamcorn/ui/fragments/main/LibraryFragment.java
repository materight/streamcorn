package com.est.streamcorn.ui.fragments.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.est.streamcorn.R;
import com.est.streamcorn.adapters.LibraryListPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class LibraryFragment extends Fragment {

    private static final String TAG = "LibraryFragment";

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private LibraryListPagerAdapter libraryListPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_main_library, container, false);
        inflateView.setTag(TAG);
        ButterKnife.bind(this, inflateView);

        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.title_movies)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.title_tv_series)));

        libraryListPagerAdapter = new LibraryListPagerAdapter(getFragmentManager());
        viewPager.setAdapter(libraryListPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return inflateView;
    }

    private void search(String searchQuery) {
        libraryListPagerAdapter.setSearchQuery(searchQuery);
    }

}
