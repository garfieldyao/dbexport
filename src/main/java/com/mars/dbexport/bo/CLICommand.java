/**
 * 
 */
package com.mars.dbexport.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yao Liqiang
 * @data Aug 5, 2013
 * @description
 */
public class CLICommand {
	private String prex = "";
	private int miniAttributes = 0;
	private List<CLIAttribute> attributes = new ArrayList<CLIAttribute>();
	private String sortor = "";
	/**
	 * @return the prex
	 */
	public String getPrex() {
		return prex;
	}
	/**
	 * @param prex the prex to set
	 */
	public void setPrex(String prex) {
		this.prex = prex;
	}
	/**
	 * @return the attributes
	 */
	public List<CLIAttribute> getAttributes() {
		return attributes;
	}
	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(List<CLIAttribute> attributes) {
		this.attributes = attributes;
	}
	/**
	 * @return the miniAttributes
	 */
	public int getMiniAttributes() {
		return miniAttributes;
	}
	/**
	 * @param miniAttributes the miniAttributes to set
	 */
	public void setMiniAttributes(int miniAttributes) {
		this.miniAttributes = miniAttributes;
	}
	public String getSortor() {
		return sortor;
	}
	public void setSortor(String sortor) {
		this.sortor = sortor;
	}
	
	
}
