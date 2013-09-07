package com.mars.dbexport.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import com.mars.dbexport.AppContext;

public class FileUtils {
	public static List<Byte> readRawFile(File file) {
		List<Byte> bytes = new ArrayList<Byte>();
		if (file == null || !file.exists() || !file.isFile()) {
			return bytes;
		}
		FileInputStream fin = null;
		FileChannel fcin = null;
		ByteBuffer buffer = null;
		try {
			fin = new FileInputStream(file);
			fcin = fin.getChannel();
			buffer = ByteBuffer.allocate(1024);
			while (true) {
				buffer.clear();
				if (fcin.read(buffer) == -1)
					break;
				buffer.flip();
				for (byte bt : buffer.array()) {
					bytes.add(bt);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fcin.close();
				fin.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bytes;
	}

	public static void createIpFolder(String ipAddr) {
		try {
			File dir = new File(AppContext.getResourceFactory().dataRoot
					+ ipAddr);
			if (!dir.exists())
				dir.mkdirs();
			if (!dir.isDirectory()) {
				dir.delete();
				dir.mkdirs();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			File dir = new File(AppContext.getAppParamters().getFtpLocal()
					+ ipAddr);
			if (!dir.exists())
				dir.mkdirs();
			if (!dir.isDirectory()) {
				dir.delete();
				dir.mkdirs();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
