/*
 * $Id: WaitingDialog.java, 2011-10-29 ����9:52:23 Yao Exp $
 * 
 * Copyright (c) 2011 Alcatel-Lucent Shanghai Bell Co., Ltd 
 * All rights reserved.
 * 
 * This software is copyrighted and owned by ASB or the copyright holder
 * specified, unless otherwise noted, and may not be reproduced or distributed
 * in whole or in part in any form or medium without express written permission.
 */
package com.mars.dbexport.gui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.mars.dbexport.AppContext;
import com.mars.dbexport.utils.ClientUtils;

/**
 * <p>
 * Title: WaitingDialog
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Yao Liqiang
 * @created 2011-10-29 ����9:52:23
 * @modified [who date description]
 * @check [who date description]
 */
public class WaitingDialog extends JDialog {
    private static final long serialVersionUID = -3185844309502596608L;

    private String title_wait = AppContext.getI18nString("title_wait");

    private JProgressBar progressBar;

    public void initGui() {
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(progressBar, BorderLayout.CENTER);

        this.setSize(200, 45);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle(title_wait);
        this.setIconImage(AppContext.getLogoIcon());

        ClientUtils.centerRelativeWindow(this);

        this.setModal(true);
    }

    public void showWaitingDialog() {
        if (this.isVisible())
            return;
        if (SwingUtilities.isEventDispatchThread()) {
            setVisible(true);
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    setVisible(true);
                }
            });
        }
    }

    public void closeWaitingDialog() {
        if (!this.isVisible())
            return;
        if (SwingUtilities.isEventDispatchThread()) {
            dispose();
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    dispose();
                }
            });
        }
    }
}
