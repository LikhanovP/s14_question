package com.rosa.swift.core.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.view.menu.ActionMenuItem;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rosa.motocross.R;

import java.util.List;

/**
 * Created by inurlikaev on 26.11.2015.
 */
public class CustomDrawerAdapter extends ArrayAdapter<ActionMenuItem> {
    Context context;
    List<ActionMenuItem> navDrawerItemsList;
    int layoutResID;

    public CustomDrawerAdapter(Context context, int layoutResourceID,
                               List<ActionMenuItem> navDrawerMenuItems) {

        super(context, layoutResourceID, navDrawerMenuItems);
        this.context = context;
        this.navDrawerItemsList = navDrawerMenuItems;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DrawerItemHolder drawerHolder;
        View view = convertView;

        MenuItem dItem = this.navDrawerItemsList.get(position);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            drawerHolder = new DrawerItemHolder();

            if (dItem.getItemId() == R.id.pcab_action_exit ||
                    dItem.getItemId() == R.id.action_go_offline) {
                view = inflater.inflate(R.layout.drawer_list_item_exit, parent, false);
            } else {
                view = inflater.inflate(layoutResID, parent, false);
            }

            drawerHolder.itemText = (TextView) view.findViewById(R.id.drawer_item_title);
            drawerHolder.icon = (ImageView) view.findViewById(R.id.drawer_item_icon);

            view.setTag(drawerHolder);

        } else {
            drawerHolder = (DrawerItemHolder) view.getTag();

        }

        Drawable icon = dItem.getIcon();
        if (icon != null) {
            drawerHolder.icon.setImageDrawable(icon);
        }
        drawerHolder.itemText.setText(dItem.getTitle());

        drawerHolder.navDrawerItem = dItem;

        return view;
    }

    private static class DrawerItemHolder {
        TextView itemText;
        ImageView icon;
        MenuItem navDrawerItem;
    }
}
