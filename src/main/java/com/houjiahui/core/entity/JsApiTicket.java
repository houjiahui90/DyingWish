package com.houjiahui.core.entity;

import java.io.Serializable;

public class JsApiTicket implements Serializable {

	private static final long serialVersionUID = 1L;
	// 错误代码
	private int errcode;
	// 错误信息
	private String errmsg;
	// 获取到的凭证
	private String ticket;
	// 凭证有效时间，单位：秒
	private int expires_in;

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public int getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}
}
