/* **************************************************************************
 * Copyright 2016 Albertsons Safeway.
 *
 * This document/file contains proprietary data that is the property of
 * Albertsons Safeway.  Information contained herein may not be used,
 * copied or disclosed in whole or in part except as permitted by a
 * written agreement signed by an officer of Albertsons Safeway.
 *
 * Unauthorized use, copying or other reproduction of this document/file
 * is prohibited by law.
 *
 ***************************************************************************/

package com.safeway.app.emju.mylist.monitoring.controller;

import com.safeway.app.emju.logging.Logger;
import com.safeway.app.emju.logging.LoggerFactory;

import play.mvc.Controller;
import play.mvc.Result;

/* ***************************************************************************
 * NAME         : HealthMonitorController.java
 *
 * SYSTEM       : emju-mylist
 *
 * AUTHOR       : Arun Hariharan
 *
 * REVISION HISTORY
 *
 * Revision 0.0.0.0 Feb 24, 2016 ahani00
 * Initial creation for emju-mylist
 *
 ***************************************************************************/

/**
 * Controller for monitoring the health
 *
 * @author ahani00
 */
public class HealthMonitorController extends Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthMonitorController.class);

    /**
     * @return returns the health status
     */
    public Result getHealth() {
        return ok();
    }
}

