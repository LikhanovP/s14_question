package com.rosa.swift.core.ui.fragments;

import android.view.MenuItem;

import com.rosa.swift.core.business.ChatMessageSentCallback;
import com.rosa.swift.core.business.DeliverySearch;
import com.rosa.swift.core.data.dto.common.Delivery;
import com.rosa.swift.core.network.json.sap.cup.JCupSetInfoOut;
import com.rosa.swift.core.network.json.sap.driverRecords.JDriverRecords;
import com.rosa.swift.core.network.json.sap.swchat.JRoom;

/**
 * Created by yalang on 04.09.2014.
 */
public interface FragmentListener {
    void onFragmentStart(int code);

    void onDeliverySelect(Delivery d);

    void onFindClick(DeliverySearch.SearchOptions options);

    void doFind(DeliverySearch.SearchOptions options);

    void onDeliveryMenuItemSelected(Delivery d, MenuItem item);

    void onChatRoomSelect(JRoom room);

    void sendChatMessage(final String room_id, final ChatMessageSentCallback callback);

    void markRoomAsRead(final JRoom room);

    void onCupSelected(final JCupSetInfoOut.JCupSetInfo cupSetInfo);

    void onDriverRecordsSelected(final JDriverRecords.JDriverRecord driverRecords);

}
