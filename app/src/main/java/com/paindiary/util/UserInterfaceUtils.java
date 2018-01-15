package com.paindiary.util;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class UserInterfaceUtils {

    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {
            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;
        } else {
            return false;
        }
    }

    private static final int GREEN = Color.rgb(155, 200, 50);
    private static final int ORANGE = Color.rgb(245, 165, 5);
    private static final int RED = Color.rgb(200, 0, 0);
    public static int mapPainLevelToColor(int painLevel) {
        if(painLevel <= 3) {
            return GREEN;
        } else if(painLevel <= 7) {
            return ORANGE;
        } else {
            return RED;
        }
    }

    public static int mapPainLevelToContrastingColor(int painLevel) {
        if(painLevel <= 3) {
            return Color.BLACK;
        } else if(painLevel <= 7) {
            return Color.BLACK;
        } else {
            return Color.WHITE;
        }
    }
}