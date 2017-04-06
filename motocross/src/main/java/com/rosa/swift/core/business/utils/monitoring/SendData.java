package com.rosa.swift.core.business.utils.monitoring;

import java.util.Date;

/**
 * Created by yalang on 08.08.13.
 */
public class SendData {
    public String description = "";
    public int lastTime = 0;
    public long token = 0L;
    public String machine = "";
    public String app = "";
    public Date opDate;
    public boolean opFinished;
}
