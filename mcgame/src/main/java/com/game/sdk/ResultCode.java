package com.game.sdk;

public class ResultCode {
	
	public static final int SUCCESS = 0;
	public static final int FAIL	= 1;
	public static final int FAILS	= -1;
	public static final int NONEXISTENT	= -3;
	public static final int PAY_CANCEL = 201;
	
	public static final int UNKNOW	= 2;
	
	public static final int APPLY_ORDER_FAIL = 202;
	
	public static final int NET_DISCONNET = 401;
	
	public static final int SECURITY_SUCCESS = 101;
	public static final int SECURITY_FAIL = 102;
	
	public static final int BIND_SUCCESS = 201;
	public static final int BIND_FAIL = 202;
	
	public static final int PASSWORD_NEW_SUCCESS = 301;
	public static final int PASSWORD_NEW_FAIL = 302;
	
	public static final int ACCOUNT_GET_SUCCESS = 501;
	public static final int ACCOUNT_GET_FAIL = 502;
	
	
	public static final int VISITOR_LOGIN_SUCCESS = 503;
	public static final int VISITOR_LOGIN_FAIL = 504;
	
	public static final int VISITOR_BIND_SUCCESS = 505;
	public static final int VISITOR_BIND_FAIL = 506;
	
	public static final int QUERY_ACCOUNT_BIND_SUCCESS = 507;
	public static final int QUERY_ACCOUNT_BIND_FAIL = 508;
	
	public static final int QUERY_MSI_BIND_SUCCESS = 509;
	public static final int QUERY_MSI_BIND_FAIL = 510;

	public static final int MOBILE_REG_SUCCRESS=511;
	public static final int MOBILE_REG_FAIL=512;

	public static final int GET_USER_SUCCRESS=513;
	public static final int GET_USER_FAIL=514;
	public static final int GET_USER_NoEXIStTENT=515;
	public static final int GET_USER=6;

	public static final int VISITOR_BIND_MODILE_SUCCESS = 516;
	public static final int VISITOR_BIND_MODILE_FAIL = 5517;

	public static final int RANDUSERNAME_SUCCESS = 518;
	public static final int RANDUSERNAME_FAIL = 519;
}
