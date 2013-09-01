/*
 * $Id: BatchResult.java, 2011-11-10 ����9:18:12 Yao Exp $
 * 
 * Copyright (c) 2011 Alcatel-Lucent Shanghai Bell Co., Ltd 
 * All rights reserved.
 * 
 * This software is copyrighted and owned by ASB or the copyright holder
 * specified, unless otherwise noted, and may not be reproduced or distributed
 * in whole or in part in any form or medium without express written permission.
 */
package com.mars.dbexport.service.batch;

import java.io.Serializable;

/**
 * <p>
 * Title: BatchResult
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Yao Liqiang
 * @created 2011-11-10 ����9:18:12
 * @modified [who date description]
 * @check [who date description]
 */
public class BatchResult implements Serializable {
    private static final long serialVersionUID = 2875617965927701358L;

    private String ipAddr = "";

    private boolean succeed = true;

    private Exception exception = null;

    /**
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }

    /**
     * @param exception
     *            the exception to set
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }

    /**
     * @return the ipAddr
     */
    public String getIpAddr() {
        return ipAddr;
    }

    /**
     * @param ipAddr
     *            the ipAddr to set
     */
    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    /**
     * @return the succeed
     */
    public boolean isSucceed() {
        return succeed;
    }

    /**
     * @param succeed the succeed to set
     */
    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

}
