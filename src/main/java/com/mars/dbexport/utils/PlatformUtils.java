/**
 * 
 */
package com.mars.dbexport.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

import org.apache.commons.lang.StringUtils;

import com.mars.dbexport.bo.enums.OsType;

/**
 * @author Yao
 * 
 */
public class PlatformUtils {
	private static File appLocker = new File("applocker");

	public static OsType getPlatformType() {
		String type = System.getProperty("os.name").toLowerCase();
		String arch = System.getProperty("os.arch").toLowerCase();
		if ("windows".equals(type)) {
			return OsType.WINDOWS;
		} else if ("linux".equals(type) && "i386".equals(arch)) {
			return OsType.LINUX_I386;
		} else if ("solaris".equals(type) && "i386".equals(arch)) {
			return OsType.SOLARIS_I386;
		} else if ("solaris".equals(type) && "sparc".equals(arch)) {
			return OsType.SOLARIS_SPARC;
		} else {
			return OsType.UNKNOWN;
		}
	}

	public static void executeShell(String cmd) {
		if (StringUtils.isEmpty(cmd))
			return;
		// TODO
	}

	/**
	 * This application only support one instance simutaneously 
	 * A file locker will be set up when app start
	 * 
	 * @return
	 */
	@SuppressWarnings("resource")
	public static boolean lockRunningState() {
		RandomAccessFile raf = null;
		try {
			if (!appLocker.exists())
				appLocker.createNewFile();
			raf = new RandomAccessFile(appLocker, "rw");
			FileLock lock = raf.getChannel().tryLock();
			if (lock == null || !lock.isValid()) {
				return false;
			}
		} catch (IOException ex) {
			return false;
		}
		return true;
	}

	public static void removeFile(File file) {
		if (!file.exists())
			return;
		if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			for(File tmpfile : file.listFiles()){
				removeFile(tmpfile);
			}
			file.delete();
		}
	}
}
