package com.rosa.swift.core.ui.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItem;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rosa.motocross.R;
import com.rosa.swift.core.ui.adapters.CustomDrawerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment implements Menu {

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private NavigationDrawerCallbacks mCallbacks;
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private List<ActionMenuItem> drawerItems;
    private List<ActionMenuItem> filteredItems;


    public NavigationDrawerFragment() {
        drawerItems = new ArrayList<ActionMenuItem>();
        filteredItems = new ArrayList<ActionMenuItem>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        //showRelocation(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        UpdateDrawerView();

        return mDrawerListView;
    }

    public List<ActionMenuItem> FilterItems(List<ActionMenuItem> items) {
        List<ActionMenuItem> newItems = new ArrayList<ActionMenuItem>();
        final int itemCount = items.size();
        for (int i = 0; i < itemCount; i++) {
            ActionMenuItem item = items.get(i);
            if (item.isVisible() && item.getItemId() != R.id.navigation_drawer_actionbar_title) {
                newItems.add(item);
            }
        }
        return newItems;
    }

    public void UpdateDrawerView() {
        filteredItems = FilterItems(drawerItems);
        mDrawerListView.setAdapter(new CustomDrawerAdapter(getActionBar().getThemedContext(), R.layout.drawer_list_item, filteredItems));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void closeDrawer(int gravity) {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(gravity);
        }
    }

    public void openDrawer(int gravity) {
        if (mDrawerLayout != null) {
            mDrawerLayout.openDrawer(gravity);
        }
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
//    public void setUp(int fragmentId, DrawerLayout drawerLayout, String[] sections) {
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).commit();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallbacks = (NavigationDrawerCallbacks) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen() && enabled) {
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setFirst_time(boolean first_time) {
        this.first_time = first_time;
    }

    public void reset() {
        mCurrentSelectedPosition = 0;
        first_time = true;
    }

    private boolean enabled;
    private boolean first_time = true;

    public void setDrawerEnabled(boolean enabled) {
        try {
            this.enabled = enabled;
            mDrawerToggle.setDrawerIndicatorEnabled(enabled);
            getActionBar().setDisplayHomeAsUpEnabled(enabled);
            mDrawerLayout.setDrawerLockMode(enabled ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//            if (enabled && first_time) {
//                openDrawer(Gravity.START);
//                first_time = false;
//            }
        } catch (Exception ignored) {

        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        MenuItem titleItem = findItem(R.id.navigation_drawer_actionbar_title);
        if (titleItem != null) {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setTitle(titleItem.getTitle());
            actionBar.setIcon(titleItem.getIcon());
        }
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    //Меню
    @Override
    public MenuItem getItem(int i) {
        return drawerItems.get(i);
    }

    public MenuItem getFilteredItem(int i) {
        return filteredItems.get(i);
    }

    @Override
    public void close() {

    }

    @Override
    public boolean performShortcut(int i, KeyEvent keyEvent, int i1) {
        return false;
    }

    @Override
    public boolean isShortcutKey(int i, KeyEvent keyEvent) {
        return false;
    }

    @Override
    public boolean performIdentifierAction(int i, int i1) {
        return false;
    }

    @Override
    public void setQwertyMode(boolean b) {

    }

    public Context getContext() {
        if (getActionBar() != null) {
            return getActionBar().getThemedContext();
        }
        return null;
    }

    @Override
    public MenuItem add(CharSequence title) {
        return add(0, 0, 0, title);
    }

    @Override
    public MenuItem add(int titleRes) {
        return add(0, 0, 0, titleRes);
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, int titleRes) {
        return add(groupId, itemId, order, getResources().getString(titleRes));
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
        ActionMenuItem newItem = new ActionMenuItem(getContext(),
                groupId, itemId, 0, order, title);

        final List<ActionMenuItem> items = drawerItems;
        final int itemCount = items.size();
        int index = -1;

        for (int i = 0; i < itemCount; i++) {
            ActionMenuItem item = items.get(i);
            index = i;
            if (item.getOrder() > order) {
                break;
            }
        }
        index++;

        drawerItems.add(index, newItem);
        return newItem;
    }

    @Override
    public SubMenu addSubMenu(CharSequence charSequence) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(int i) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(int i, int i1, int i2, CharSequence charSequence) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(int i, int i1, int i2, int i3) {
        return null;
    }

    @Override
    public int addIntentOptions(int i, int i1, int i2, ComponentName componentName, Intent[] intents, Intent intent, int i3, MenuItem[] menuItems) {
        return 0;
    }

    @Override
    public void removeItem(int id) {
        int index = findItemIndex(id);
        if (index > -1) drawerItems.remove(index);
    }

    @Override
    public void removeGroup(int groupId) {
        final List<ActionMenuItem> items = drawerItems;
        int itemCount = items.size();
        int i = 0;
        while (i < itemCount) {
            if (items.get(i).getGroupId() == groupId) {
                items.remove(i);
                itemCount--;
            } else {
                i++;
            }
        }
    }

    @Override
    public void clear() {
        drawerItems.clear();
    }

    @Override
    public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {
        final List<ActionMenuItem> items = drawerItems;
        final int itemCount = items.size();
        for (int i = 0; i < itemCount; i++) {
            ActionMenuItem item = items.get(i);
            if (item.getGroupId() == group) {
                item.setCheckable(checkable);
                item.setExclusiveCheckable(exclusive);
            }
        }
    }

    @Override
    public void setGroupVisible(int group, boolean visible) {
        final List<ActionMenuItem> items = drawerItems;
        final int itemCount = items.size();
        for (int i = 0; i < itemCount; i++) {
            ActionMenuItem item = items.get(i);
            if (item.getGroupId() == group) {
                item.setVisible(visible);
            }
        }
    }

    @Override
    public void setGroupEnabled(int group, boolean enabled) {
        final List<ActionMenuItem> items = drawerItems;
        final int itemCount = items.size();
        for (int i = 0; i < itemCount; i++) {
            ActionMenuItem item = items.get(i);
            if (item.getGroupId() == group) {
                item.setEnabled(enabled);
            }
        }
    }

    @Override
    public boolean hasVisibleItems() {
        final List<ActionMenuItem> items = drawerItems;
        final int itemCount = items.size();
        for (int i = 0; i < itemCount; i++) {
            if (items.get(i).isVisible()) {
                return true;
            }
        }
        return false;
    }

    private int findItemIndex(int id) {
        final List<ActionMenuItem> items = drawerItems;
        final int itemCount = items.size();
        for (int i = 0; i < itemCount; i++) {
            if (items.get(i).getItemId() == id) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public MenuItem findItem(int id) {
        int index = findItemIndex(id);
        if (index > -1) return drawerItems.get(index);
        return null;
    }

    @Override
    public int size() {
        return drawerItems.size();
    }

}
