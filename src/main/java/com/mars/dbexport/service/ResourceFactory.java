package com.mars.dbexport.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 
 * @author Yao Liqiang
 * @data Aug 3, 2013
 */
public class ResourceFactory {

	public final String rootPath = "META-INF/";

	public final String i18nRoot = "i18n/";
	public final String imagesRoot = "images/";
	public final String cn_file = "zh_cn.properties";
	public final String en_file = "en_us.properties";

	public final String dataRoot = "data/";
	public final String dbDefinRoot = "data/xml/";
	public final String dbDataRoot = "data/xml/data/";
	public final String mappingRoot = "mapping/";

	private Properties prop_i18n;

	public ResourceFactory() {

	}

	public URL getImageSource(String fileName) {
		StringBuilder sb = new StringBuilder();
		sb.append(rootPath);
		sb.append(imagesRoot);
		sb.append(fileName);
		return getUrl(sb.toString());
	}

	public File getBinFile(String fileName) {
		return getFile(dataRoot, fileName);
	}

	public File getDbDefineFile(String fileName) {
		return getFile(dbDefinRoot, fileName);

	}

	public File getDbDataFile(String fileName) {
		return getFile(dbDataRoot, fileName);
	}

	public List<File> getMappingFiles() {
		List<File> files = new ArrayList<File>();
		File mappingDir = getFile(mappingRoot, "");
		if (mappingDir != null && mappingDir.isDirectory()) {
			for (File tmp : mappingDir.listFiles()) {
				// TODO sort
				if (tmp.isFile()
						&& tmp.getName().toLowerCase().endsWith(".xml"))
					files.add(tmp);
			}
		}

		return files;
	}

	public File getMappingFile(String fileName) {
		return getFile(mappingRoot, fileName);
	}

	public File getFile(String folderName, String fileName) {
		StringBuilder sb = new StringBuilder();
		sb.append(folderName);
		sb.append(fileName);
		File file = new File(sb.toString());
		if (file.exists())
			return file;
		String localDir = null;

		localDir = System.getProperty("user.dir");
		sb = new StringBuilder();
		sb.append(localDir);
		sb.append("/");
		sb.append(folderName);
		sb.append(fileName);
		file = new File(sb.toString());
		if (file.exists())
			return file;

		sb = new StringBuilder();
		sb.append(folderName);
		sb.append(fileName);
		URL url = getUrl(sb.toString());
		if (url != null) {
			try {
				file = new File(URLDecoder.decode(url.getPath(), "utf-8"));
				if (file.exists())
					return file;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	public String getI18NString(String key) {
		if (prop_i18n == null) {
			prop_i18n = new Properties();
			StringBuilder sb = new StringBuilder();
			sb.append(rootPath);
			sb.append(i18nRoot);
			// if (ClientContext.getLanguage() == Lanaguage.CN)
			// sb.append(cn_file);
			// else
			sb.append(en_file);
			URL url = getUrl(sb.toString());
			try {
				prop_i18n.load(url.openStream());
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}

		return prop_i18n.getProperty(key, key);
	}

	private URL getUrl(String name) {
		URL url = null;
		try {
			File file = new File(name);
			if (file.exists()) {
				url = file.toURI().toURL();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (url == null) {
			url = ClassLoader.getSystemResource(name);
		}
		if (url == null) {
			ClassLoader loader = getClass().getClassLoader();
			url = loader.getResource(name);
			while (url == null && loader.getParent() != null) {
				loader = loader.getParent();
				url = loader.getResource(name);
			}
		}
		if (url == null) {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			url = loader.getResource(name);
			while (url == null && loader.getParent() != null) {
				loader = loader.getParent();
				url = loader.getResource(name);
			}
		}
		return url;
	}

}
