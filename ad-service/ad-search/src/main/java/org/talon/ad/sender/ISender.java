package org.talon.ad.sender;

import org.talon.ad.mysql.dto.IncreRowData;

public interface ISender {

    void send(IncreRowData rowData);

}
