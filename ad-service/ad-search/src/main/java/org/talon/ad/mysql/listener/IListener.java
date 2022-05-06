package org.talon.ad.mysql.listener;

import org.talon.ad.mysql.dto.BinlogRowData;

/**
 * Created by Zelong
 * On 2022/5/4
 **/

public interface IListener {

    void register();

    void onEvent(BinlogRowData eventData);

}
