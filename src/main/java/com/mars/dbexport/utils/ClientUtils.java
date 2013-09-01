package com.mars.dbexport.utils;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Window;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.mars.dbexport.AppContext;
import com.mars.dbexport.gui.TableLayout;
import com.mars.dbexport.gui.WaitingDialog;

/**
 * <p>
 * Title: ClientUtils
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Yao Liqiang
 * @created 2011-10-27 ����05:02:15
 * @modified [who date description]
 * @check [who date description]
 */
public class ClientUtils {

    public synchronized static void centerWindow(Window window) {
        Dimension screen = window.getToolkit().getScreenSize();
        window.setLocation((int) (screen.getWidth() - window.getWidth()) / 2,
                (int) (screen.getHeight() - window.getHeight()) / 2);
    }

    public synchronized static void centerRelativeWindow(Window child, Window parent) {
        Dimension size = parent.getSize();
        Dimension csize = child.getSize();
        Point local = parent.getLocation();
        child.setLocation(new Point((local.x + size.width / 2 - csize.width / 2),
                (local.y + size.height / 2 - csize.height / 2)));
    }

    public synchronized static void centerRelativeWindow(Window child) {
        centerRelativeWindow(child, AppContext.getMainFrame());
    }

    public static void showInfoDailog(String message) {
        JOptionPane.showMessageDialog(AppContext.getMainFrame(), message, "Information",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showInfoDailog(String title, String message) {
        JOptionPane.showMessageDialog(AppContext.getMainFrame(), message, title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showErrorDailog(String message) {
        JOptionPane.showMessageDialog(AppContext.getMainFrame(), message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static WaitingDialog getWaitingDialog() {
        WaitingDialog dialog = new WaitingDialog();
        dialog.initGui();
        return dialog;
    }

    public static LayoutManager getTableLayout(double[][] ds) {
        return new TableLayout(ds);
    }

    public static JLabel getIconLabel(String iconName) {
        Icon icon = AppContext.getIcon(iconName);
        JLabel label = new JLabel();
        if (icon != null)
            label.setIcon(icon);
        return label;
    }
    
    public static Dimension getScreenSize(){
        return java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    }

}
