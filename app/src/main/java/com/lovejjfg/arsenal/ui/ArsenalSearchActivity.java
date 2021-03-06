/*
 *  Copyright (c) 2017.  Joe
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.lovejjfg.arsenal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lovejjfg.arsenal.R;
import com.lovejjfg.arsenal.api.mode.SearchInfo;
import com.lovejjfg.arsenal.base.SupportActivity;
import com.lovejjfg.arsenal.utils.JumpUtils;
import com.lovejjfg.arsenal.utils.TagUtils;
import com.lovejjfg.arsenal.utils.rxbus.RxBus;
import com.lovejjfg.arsenal.utils.rxbus.SearchEvent;
import com.miguelcatalan.materialsearchview.MaterialSearchView;


import butterknife.BindView;
import butterknife.ButterKnife;

public class ArsenalSearchActivity extends SupportActivity implements View.OnClickListener {
    private static final String TAG = ArsenalSearchActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        String currentTag = getIntent().getStringExtra(ArsenalListInfoFragment.KEY);
        String currentTagName = getIntent().getStringExtra(ArsenalListInfoFragment.TAG_NAME);
        getSupportActionBar().setTitle(TextUtils.isEmpty(currentTagName) ? currentTag : currentTagName);
        mToolbar.setNavigationOnClickListener(this);

        if (savedInstanceState == null) {
            ArsenalListInfoFragment listInfoFragment = new ArsenalListInfoFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(ArsenalListInfoFragment.TYPE_NAME, getIntent().getIntExtra(ArsenalListInfoFragment.TYPE_NAME, ArsenalListInfoFragment.TYPE_SEARCH_TAG));
            bundle.putString(ArsenalListInfoFragment.KEY, currentTag);
            listInfoFragment.setArguments(bundle);
            loadRoot(R.id.activity_tag_search, listInfoFragment);
        }
//        setSupportActionBar(mToolbar);

        searchView.setVoiceSearch(false);
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                String[] strings = TagUtils.getTagArray();
                if (strings != null) {
                    searchView.setSuggestions(strings, title -> {
                        searchView.closeSearch();
                        String s = TagUtils.getTagValue(title);
                        if (TextUtils.isEmpty(s)) {
                            JumpUtils.jumpToSearchList(searchView.getContext(), title);
                        } else {
                            JumpUtils.jumpToSearchList(searchView.getContext(), title, "/tag/" + s);
                        }
                    });
                }
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    TransitionManager.beginDelayedTransition((ViewGroup) mToolbar.getParent(), new Fade(Fade.IN));
//                }
//                mToolbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchViewAnimationStart() {
//                mToolbar.setVisibility(View.INVISIBLE);
            }
        });

        searchView.setEllipsize(true);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.closeSearch();
                SearchInfo searchInfo = new SearchInfo(query, ArsenalListInfoFragment.TYPE_SEARCH, null);
                TagUtils.save(searchInfo);
                JumpUtils.jumpToSearchList(ArsenalSearchActivity.this, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });


    }

    @Override
    public int initLayoutRes() {
        return R.layout.activity_tag_search;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
//        final MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                if (searchView.getMenuItem() == null) {
                    View searchMenuView = mToolbar.findViewById(R.id.action_search);
                    searchView.setMenuItem(searchMenuView);
                }
                if (!searchView.isSearchOpen()) {
                    searchView.showSearch();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String currentTag = intent.getStringExtra(ArsenalListInfoFragment.KEY);
        String currentTagName = intent.getStringExtra(ArsenalListInfoFragment.TAG_NAME);
        int type = intent.getIntExtra(ArsenalListInfoFragment.TYPE_NAME, ArsenalListInfoFragment.TYPE_SEARCH_TAG);
        getSupportActionBar().setTitle(TextUtils.isEmpty(currentTagName) ? currentTag : currentTagName);
        RxBus.getInstance().post(new SearchEvent(currentTag, currentTagName, type));
        super.onNewIntent(intent);
    }
}
