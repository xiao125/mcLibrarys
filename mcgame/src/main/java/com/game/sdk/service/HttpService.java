package com.game.sdk.service;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.net.RestClient;
import com.game.sdk.net.callback.IError;
import com.game.sdk.net.callback.ISuccess;
import com.game.sdk.task.SDK;
import com.game.sdk.bean.GameInfo;
import com.game.sdk.bean.GameUser;
import com.game.sdk.bean.PayInfo;
import com.game.sdk.bean.UserInfo;
import com.game.sdk.listener.BaseListener;
import com.game.sdk.task.CommonAsyncTask;
import com.game.sdk.task.GetAccontMobileAsyncTask;
import com.game.sdk.task.GetUserNameAsyncTask;
import com.game.sdk.task.LoginAsyncTask;
import com.game.sdk.task.QueryAccountBindAsyncTask;
import com.game.sdk.task.QueryMsiBindAsyncTask;
import com.game.sdk.task.VisitorAccountBindAsyncTask;
import com.game.sdk.task.VisitorAsyncTask;
import com.game.sdk.task.VisitorBindMobileAsyncTask;
import com.game.sdk.util.BuildHelper;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.DeviceUtil;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.LogUtil;
import com.game.sdk.util.Md5Util;
import com.game.sdk.util.Util;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpService {

	private static final String PROXY_VERSION = "1.0.1" ;
	private static final String reg_key="kuniu@!#2014";

	//查询账号是否绑定手机号
	public static void queryBindAccont(String user_Name, final ISuccess iSuccess, IError iError ){

		try {
			HashMap<String,String> update_params = getCommonParams();
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = PROXY_VERSION;
			JSONObject content = new JSONObject();
			content.put("user_name",user_Name);
			update_params.put("content", content.toString());
			update_params.put("proxyVersion", versionCode);
			HashMap<String, String> update_params1 = Util.getSign( update_params , app_secret );

			//update_params.put("sign", Md5Util.getMd5(content + GameSDK.getInstance().getGameInfo().getRegKey())); //这个接口验签必须是md5
            //{"code":"-1","reason":"该帐号没有绑定手机"}

			//网络请求
			RestClient.builder()
					.url(SDK.URL.QUERY_ACCOUNT_BIND)
					.params(update_params1)
					.success(iSuccess)
					.error(iError)
					.build()
					.post();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	//验证账号是否存在
	public static void getUsername(String user_Name,ISuccess iSuccess,IError iError){
		try {
			HashMap<String , String> params = getCommonParams();
			JSONObject content = new JSONObject();
			content.put("user_name",user_Name);
			params.put("content", content.toString());
			params.put("proxyVersion", "1.0.0");
			params.put("sign", Md5Util.getMd5(content + GameSDK.getInstance().getGameInfo().getRegKey())); //这个接口验签必须是md5
			//网络请求
			RestClient.builder()
					.url(SDK.URL.GET_USER_NAME)
					.params(params)
					.success(iSuccess)
					.error(iError)
					.build()
					.post();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//游客绑定账号
	public static void visitorBindAccount( Context applicationContext, Handler handler,
			String username, String password ){
		String gameId = GameSDK.getInstance().getGameInfo().getGameId() ;
		String channel = GameSDK.getInstance().getGameInfo().getChannel();
		String platform = GameSDK.getInstance().getGameInfo().getPlatform() ;
		String ad_channel = GameSDK.getInstance().getGameInfo().getAdChannel() ;
		String imei = DeviceUtil.getDeviceId();
		String appInfo = Util.getAppInfo( GameSDK.getInstance().getActivity() );
//		String proxy_version = KnUtil.getJsonStringByName(appInfo, "versionCode") ;
		String proxy_version = PROXY_VERSION ;
		String app_id     = "1011";
		String app_secret = "3d759cba73b253080543f8311b6030bf";
		Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return arg0.compareTo(arg1);
			}
		} );
		JSONObject content = new JSONObject();
		try {
			content.put("user_name",username);
			content.put("passwd",password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		update_params.put("content", content.toString());
		update_params.put("game_id",gameId);
		update_params.put("channel",channel);
		update_params.put("platform",platform);
		update_params.put("ad_channel",ad_channel);
		update_params.put("msi",imei);
		update_params.put("proxyVersion",proxy_version);
		Map<String, String> update_params1 = Util.getSign( update_params , app_secret );
		new VisitorAccountBindAsyncTask(applicationContext, handler, SDK.VISITOR_ACCOUNT_BIND)
				.execute(new Map[] { update_params1, null, null });
	}

	//游客登录
	public static void visitorReg( Context applicationContext, Handler handler ){
		
		String gameId = GameSDK.getInstance().getGameInfo().getGameId() ;
		String channel = GameSDK.getInstance().getGameInfo().getChannel();
		String platform = GameSDK.getInstance().getGameInfo().getPlatform() ;
		String ad_channel = GameSDK.getInstance().getGameInfo().getAdChannel() ;
		String imei = DeviceUtil.getDeviceId();
		String appInfo = Util.getAppInfo( GameSDK.getInstance().getActivity() );
//		String proxy_version = KnUtil.getJsonStringByName(appInfo, "versionCode") ;
		String proxy_version = PROXY_VERSION ;
		
		String app_id     = "1011";
		String app_secret = "3d759cba73b253080543f8311b6030bf";
		
		Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return arg0.compareTo(arg1);
			}
		} );
		
		update_params.put("game_id",gameId);
		update_params.put("channel",channel);
		update_params.put("platform",platform);
		update_params.put("ad_channel",ad_channel);
		update_params.put("msi",imei);
		update_params.put("proxyVersion",proxy_version);
		
		Map<String, String> update_params1 = Util.getSign( update_params , app_secret );
		
		new VisitorAsyncTask(applicationContext, handler, SDK.VISITOR_REG)
				.execute(new Map[] { update_params1, null, null });
		
	}

	//获取验证码请求
	public static void getSecCode(String mobile,ISuccess iSuccess,IError iError ){
		try {
			HashMap<String,String> update_params = getCommonParams();
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = PROXY_VERSION ;
			JSONObject content = new JSONObject();
			content.put("mobile",mobile);
			update_params.put("content", content.toString());
			update_params.put("proxyVersion", versionCode);
			//update_params.put("sign", Md5Util.getMd5(content+GameSDK.getInstance().getGameInfo().getRegKey()));
			HashMap<String, String> update_params1 = Util.getSign( update_params , app_secret );
			//网络请求
			RestClient.builder()
					.url(SDK.URL.GET_RESURITY_CODE_URL)
					.params(update_params1)
					.success(iSuccess)
					.error(iError)
					.build()
					.post();

		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	//绑定手机请求
	public static void bindMobile(String mobile , String security_code , String user_Name, final ISuccess iSuccess, final IError iError ){

		try {
			HashMap<String,String> update_params = getCommonParams();
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = PROXY_VERSION ;
			JSONObject content = new JSONObject();
			content.put("mobile",mobile);
			content.put("user_name",user_Name);
			content.put("rand_code",security_code);
			update_params.put("content", content.toString());
			update_params.put("proxyVersion", versionCode);
			HashMap<String, String> update_params1 = Util.getSign( update_params , app_secret );
			//update_params.put("sign", Md5Util.getMd5(content+GameSDK.getInstance().getGameInfo().getRegKey()));

			//网络请求
			RestClient.builder()
					.url(SDK.URL.BIND_MOBILE_URL)
					.params(update_params1)
					.success(iSuccess)
					.error(iError)
					.build()
					.post();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//游客绑定手机
	public static void visitorbindMobile( Context applicationContext, Handler handler, String mobile , String security_code , String user_Name,String user_Password ){

		try {
			GameInfo gameInfo = GameSDK.getInstance().getGameInfo();
			String appInfo = Util.getAppInfo( GameSDK.getInstance().getActivity() );
			String app_id     = "1011";
			String app_secret = "3d759cba73b253080543f8311b6030bf";
//			String versionCode = KnUtil.getJsonStringByName(appInfo, "versionCode") ;
			String versionCode = PROXY_VERSION ;
			String gameName = gameInfo.getGameId() ;
			String imei = DeviceUtil.getDeviceId();
			String channel = gameInfo.getChannel();
			String ad_channel = gameInfo.getAdChannel();
			String platform = gameInfo.getPlatform();
			Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

				@Override
				public int compare(String arg0, String arg1) {
					// TODO Auto-generated method stub
					return arg0.compareTo(arg1);
				}
			} );

			JSONObject content = new JSONObject();
			content.put("mobile",mobile);
			content.put("user_name",user_Name);
			content.put("passwd",user_Password);
			content.put("rand_code",security_code);
			update_params.put("content", content.toString());
			update_params.put("proxyVersion", versionCode);
			update_params.put("msi",imei);
			update_params.put("game_id",gameName);
			update_params.put("channel",channel);
			update_params.put("platform",platform);
			update_params.put("ad_channel",ad_channel);

			Map<String, String> update_params1 = Util.getSign( update_params , app_secret );

			new VisitorBindMobileAsyncTask(applicationContext, handler, SDK.VISITOR_BIND_MOBILE)
					.execute(new Map[] { update_params1 , null, null });
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	//随机分配用户名接口
	public static void RandUserName(String time, final ISuccess iSuccess, final IError iError ){

		try {
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = PROXY_VERSION ;
			HashMap<String,String> update_params = new HashMap<>();
			update_params.put("time",time);
			update_params.put("proxyVersion", versionCode);
			HashMap<String, String> update_params1 = Util.getSign( update_params , app_secret );
			//update_params.put("sign", Md5Util.getMd5(time+reg_key));
			//网络请求
			RestClient.builder()
					.url(SDK.URL.RAND_USER_NAME)
					.params(update_params1)
					.success(iSuccess)
					.error(iError)
					.build()
					.post();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//设备激活
	public static void  recordActivate(Context applicationContext,ISuccess iSuccess,IError iError){

		try {
			HashMap<String , String> params = getCommonParams();
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = PROXY_VERSION ;
			params.put("proxyVersion",versionCode);
			String DisplayMetrics = Util.ImageGalleryAdapter(applicationContext);
			params.put("RP",DisplayMetrics); //当前手机分辨率
			HashMap<String, String> update_params1 = Util.getSign(params ,app_secret );

			//网络请求
			RestClient.builder()
					.url(SDK.URL.RECORD_ACTIVATE)
					.params(update_params1)
					.success(iSuccess)
					.error(iError)
					.build()
					.post();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void getAccountSubmit( Context applicationContext, Handler handler, String mobile , String security_code ){
	
		try {
			GameInfo gameInfo = GameSDK.getInstance().getGameInfo();
			String appInfo = Util.getAppInfo( GameSDK.getInstance().getActivity() );
			String app_id     = "1011";
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = Util.getJsonStringByName(appInfo, "versionCode") ;
//			String versionCode = PROXY_VERSION ;
			String gameName = gameInfo.getGameId() ;
			Map<String, String> update_params = new TreeMap<String, String>( new Comparator<String>() {

				@Override
				public int compare(String arg0, String arg1) {
					// TODO Auto-generated method stub
					return arg0.compareTo(arg1);
				}
			} );
			
			JSONObject content = new JSONObject();
			content.put("mobile",mobile);
			content.put("rand_code",security_code);
			update_params.put("content", content.toString());
			update_params.put("proxyVersion", versionCode);
			update_params.put("game", gameName);
			update_params.put("app_id",app_id);
			
			Map<String, String> update_params1 = Util.getSign( update_params , app_secret );
			
			new GetAccontMobileAsyncTask(applicationContext, handler, SDK.GET_ACCOUNT_URL)
					.execute(new Map[] { update_params1 , null, null });
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	//更加手机验证码修改密码
	public static void passwordNewSubmit(String mobile , String security_code , String new_password,
										 ISuccess iSuccess,IError iError){

		try {
			HashMap<String , String> update_params = getCommonParams();
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = PROXY_VERSION ;
			JSONObject content = new JSONObject();
			content.put("mobile",mobile);
			content.put("pwd_new",new_password);
			content.put("rand_code",security_code);
			update_params.put("content", content.toString());
			update_params.put("newSdk", "1");//区分sdk
			update_params.put("proxyVersion", versionCode);
			HashMap<String, String> update_params1 = Util.getSign( update_params , app_secret );
			//update_params.put("sign", Md5Util.getMd5(content.toString()+GameSDK.getInstance().getGameInfo().getRegKey()));

			//网络请求
			RestClient.builder()
					.url(SDK.URL.UPDATE_PASSWORD_URL)
					.params(update_params1)
					.success(iSuccess)
					.error(iError)
					.build()
					.post();


		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	//账号登录请求
	public static void doLogin(String username, String password, final ISuccess iSuccess, IError iError) {

		try {
			final HashMap<String , String> params = getCommonParams();
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			final JSONObject content = new JSONObject();
			content.put("user_name", username);
			content.put("passwd", password);
			params.put("content", content.toString());
			String versionCode = PROXY_VERSION ;
			params.put("proxyVersion",versionCode);
			/*params.put(
					"sign",
					Md5Util.getMd5(game_id + channel
							+ platform + content.toString()
							+ gameInfo.getAppKey()));*/
			HashMap<String, String> update_params = Util.getSign(params ,app_secret );
            //{"code":"0","open_id":"c0745cbc52e5e2802f8b7e49ce0f101a","reason":"登录成功!","is_bind_mobile":"0","isSwitch":0,"sign":"d362909ee120459a13f8edf71055f910","sid":"ffdad7841411fd0e2bcfec3bf1adff1d","iscompany":0,"invite":{"code":0,"reason":"ok"},"extra_info":{"isLogState":0}}
			//网络请求
			RestClient.builder()
					.url(SDK.URL.LOGIN_URL)
					.params(update_params)
					.success(new ISuccess() {
						@Override
						public void onSuccess(String response) {
							final int code = JSON.parseObject(response).getIntValue("code");

							switch (code) {
								case ResultCode.SUCCESS: //成功
									if (GameSDK.getInstance().getmLoginListener() != null) {
										GameSDK.getInstance().getmLoginListener().onSuccess(response.toString());
									}

									final String open_id = JSON.parseObject(response).getString("open_id");
									final String sid = JSON.parseObject(response).getString("sid");
									final String user_name = JSON.parseObject(content.toString()).getString("user_name");
									final String passwd = JSON.parseObject(content.toString()).getString("passwd");

									UserInfo userInfo = new UserInfo();
									userInfo.setOpenId(open_id);
									userInfo.setSid(sid);
									userInfo.setUsername(user_name);
									userInfo.setLogin(true);
									GameSDK.getInstance().setUserInfo(userInfo);
									//登录成功之后就保存账号密码
									DBHelper.getInstance().insertOrUpdateUser( user_name , passwd);
									iSuccess.onSuccess(response);
									break;

								default:
									if (GameSDK.getInstance().getmLoginListener() != null) {
										GameSDK.getInstance().getmLoginListener().onFail(response.toString());
									}
									iSuccess.onSuccess(response);
									break;
							}
						}
					})
					.error(iError)
					.build()
					.post();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//根据用户名与密码来注册账号
	public static void doRegister(String username, String password,ISuccess iSuccess,IError iError) {

		try {
			HashMap<String,String> params = getCommonParams();
			JSONObject obj = new JSONObject();
			obj.put("user_name", username);
			obj.put("passwd", password);
			String content = obj.toString();
			params.put("content", content);
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = PROXY_VERSION ;
			params.put("proxyVersion",versionCode);
			HashMap<String, String> update_params = Util.getSign(params ,app_secret );
			//params.put("sign", Md5Util.getMd5(content + GameSDK.getInstance().getGameInfo().getRegKey()));
			//返回 {"code":"0","reason":"注册成功"}
			//网络请求
			RestClient.builder()
					.url(SDK.URL.REG_URL)
					.params(update_params)
					.success(iSuccess)
					.error(iError)
					.build()
					.post();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//手机号注册账号
	public static void doMobileRegister(String mobile, String code, String password,  final ISuccess iSuccess, IError iError) {

		try {
			HashMap<String,String> params = getCommonParams();
			JSONObject obj = new JSONObject();
			obj.put("mobile",mobile);
			obj.put("passwd",password);
			obj.put("rand_code",code);
			String content = obj.toString();
			params.put("content",content);
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			String versionCode = PROXY_VERSION ;
			params.put("proxyVersion",versionCode);
			HashMap<String, String> update_params = Util.getSign(params ,app_secret );
			//params.put("sign", Md5Util.getMd5(content + GameSDK.getInstance().getGameInfo().getRegKey()));
			//返回
			//网络请求
			RestClient.builder()
					.url(SDK.URL.REG_MOBILE)
					.params(update_params)
					.success(new ISuccess() {
						@Override
						public void onSuccess(String response) {
							final int code = JSON.parseObject(response).getIntValue("code");
							switch (code) {
								case ResultCode.SUCCESS: //成功
									if (GameSDK.getInstance().getmLoginListener() != null) {
										GameSDK.getInstance().getmLoginListener().onSuccess(response.toString());
									}
									iSuccess.onSuccess(response);
									break;
								default:
									if (GameSDK.getInstance().getmLoginListener() != null) {
										GameSDK.getInstance().getmLoginListener().onFail(response.toString());
									}
									iSuccess.onSuccess(response);
									break;
							}
							}
					})
					.error(iError)
					.build()
					.post();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	//注销接口 act_type: 2 注销 1：退出
	public static void doCancel( String act_type, ISuccess iSuccess, IError iError) {

		try {
			HashMap<String,String> params =  new HashMap<String,String>();
			UserInfo userInfo = GameSDK.getInstance().getUserInfo();
			GameInfo gameInfo = GameSDK.getInstance().getGameInfo();
			String open_id ="";
			String server_id ="";
			String nick_name ="";
			String uid = "";

			if(userInfo.getOpenId()!=null){
				open_id = userInfo.getOpenId();
			}
			if(userInfo.getServerId()!=0){
				server_id = String.valueOf(userInfo.getServerId());
			}
			if(userInfo.getUsername()!=null){
				nick_name = userInfo.getUsername();
			}
			if(userInfo.getUid()!=null){
				uid = userInfo.getUid();
			}

			String platform = gameInfo.getPlatform();
			String game_id =  gameInfo.getGameId();
			String channel = gameInfo.getChannel();
			String ad_channel = gameInfo.getAdChannel();
			String app_secret = gameInfo.getAppKey();
			String versionCode = PROXY_VERSION ;
			String msi = DeviceUtil.getDeviceId();
			params.put("game_id", game_id);//游戏名称
			params.put("open_id",open_id );
			params.put("imei",msi );
			params.put("channel",channel );
			params.put("ad_channel",ad_channel  );
			params.put("proxyVersion",versionCode);
			params.put("act_type",act_type);
			params.put("uid",uid);
			params.put("server_id",server_id );
			params.put("nick_name",nick_name );
			Collection<String> keyset= params.keySet();
			List<String> list = new ArrayList<String>(keyset);
			Collections.sort(list);
			String key = "";
			for(int i=0;i<list.size();i++){
				if(params.get(list.get(i))==null || params.get(list.get(i))=="") {
					continue;
				}
				key += list.get(i)+"="+params.get(list.get(i))+"&";
			}
			key += "app_secret="+app_secret;
			params.put("sign",Md5Util.getMd5(key));
			//KnLog.log("排序字段:"+key);
			RestClient.builder()
					.url(SDK.URL.CANCEL)
					.params(params)
					.success(iSuccess)
					.error(iError)
					.build()
					.post();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//发送等级url
	public static void enterGame( GameUser gameUser,ISuccess iSuccess,IError iError) {
		try {
			String versionCode ="";
			String app_secret = "3d759cba73b253080543f8311b6030bf";
			GameInfo gameInfo = GameSDK.getInstance().getGameInfo();
			String channel = gameInfo.getChannel();//渠道
			String adchannel = gameInfo.getAdChannel();//广告渠道
			String mis = DeviceUtil.getDeviceId(); //IMEI码
			String game_id =  gameInfo.getGameId(); //游戏品牌
			String uid = gameUser.getUid();//游戏uid
			String open_id = gameUser.getOpenid();//游戏openid
			int   serverId = gameUser.getServerId();//服务区id
			int  lv = gameUser.getUserLevel();// 游戏等级
			String gid = gameUser.getGid(); //工会id

			HashMap<String,String> params =new HashMap<String, String>();

			if(gameUser!=null){

				params.put("game_id",game_id);
				params.put("uid",uid);
				params.put("open_id", open_id);
				params.put("server_id",String.valueOf(serverId));
				params.put("lv", String.valueOf(lv));
				params.put("msi",mis);
				params.put("ad_channel",adchannel);
				params.put("channel", channel);
				params.put("gid", gid);
				params.put("extraInfo", gameUser.getExtraInfo());
				params.put("proxyVersion", versionCode);

			}
			//params.put("getuiClientId", GeTuiPushModule.getInstance().getClientId());
			LogUtil.log("上报游戏数据接口========"+ params.toString());

			//网络请求
			RestClient.builder()
					.url( SDK.URL.ENTER_GAME)
					.params(params)
					.success(iSuccess)
					.error(iError)
					.build()
					.post();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void chanagePwd(Activity activity,
			String username, String oldpassword , String newpassword , BaseListener listener) {
		try {
			HashMap<String,String> params = getCommonParams();
			
			JSONObject obj = new JSONObject();
			obj.put("user_name", username);
			obj.put("passwd", oldpassword);
			obj.put("new_pwd", newpassword);

			String content = obj.toString();

			params.put("content", content);
			params.put("sign", Md5Util.getMd5(content + GameSDK.getInstance().getGameInfo().getRegKey()));
			
			KnLog.log(params.toString());

			new CommonAsyncTask(activity , SDK.CHANGE_PWD_URL, listener)
			.execute(new Map[] { params, null, null });
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void applyOrder( Activity activity ,PayInfo payInfo , BaseListener listener) {
		try {

			UserInfo userInfo = GameSDK.getInstance().getUserInfo();
			GameInfo gameInfo = GameSDK.getInstance().getGameInfo();

			Map<String,String> params = getCommonParams();
			
			String uid = payInfo.getUid();
			int server_id = payInfo.getServerId();
			
			String open_id = userInfo.getOpenId();

			String game_id = gameInfo.getGameId();
			String platform = gameInfo.getPlatform();
			String channel = gameInfo.getChannel();

			params.put("platform", platform);
			params.put("extra_info", payInfo.getCpprivateinfo());

			params.put(
					"sign",
					Md5Util.getMd5(game_id + channel + platform + uid + open_id
							+ server_id + gameInfo.getAppKey()));
			
			new CommonAsyncTask(activity , SDK.APPLY_ORDER_URL, listener)
					.execute(new Map[] { params, null, null });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static HashMap<String, String> getCommonParams(){
		
		HashMap<String, String> params = new HashMap<String, String>();
		
		UserInfo userInfo = GameSDK.getInstance().getUserInfo();
		GameInfo gameInfo = GameSDK.getInstance().getGameInfo();
		
		String open_id="",game_id="",channel="",ad_channel="",msi="",platform="",appkey= "";
		String uid="",server_id="";
		
		if(userInfo!= null){
			open_id = userInfo.getOpenId();
			uid = userInfo.getUid();
			server_id = String.valueOf(userInfo.getServerId());
		}
		
		if(gameInfo!=null){
			platform = gameInfo.getPlatform();
			game_id =  gameInfo.getGameId();
			channel = gameInfo.getChannel();
			ad_channel = gameInfo.getAdChannel();
			appkey = gameInfo.getAppKey();
		}
		msi = DeviceUtil.getDeviceId();
		String Product = BuildHelper.getProduct(); //手机制造商
		String Mode = BuildHelper.getMode(); //手机型号
		String ip =DeviceUtil.getIPAddress(); //手机ip地址
		String time = Util.getTimes();


		params.put("game_id", game_id); //游戏名称
		params.put("channel", channel); //联运渠道
		params.put("ad_channel", ad_channel); //广告渠道
		params.put("uid", String.valueOf(uid));
		params.put("open_id", open_id);
		params.put("server_id", String.valueOf(server_id));
		params.put("mac", DeviceUtil.getMacAddress());
		params.put("platform", platform);
		params.put("netType", DeviceUtil.getNetWorkType()); //手机网络状态
		//params.put("phoneType", DeviceUtil.getPhoneType()); //手机型号
		params.put("app_key",appkey);
		
		String appInfo = Util.getAppInfo( GameSDK.getInstance().getActivity() );
		params.put("packageName", Util.getJsonStringByName(appInfo, "packageName") ); //客户端包名
		params.put("versionName", Util.getJsonStringByName(appInfo, "versionName") ); //客户端版本
		params.put("versionCode", Util.getJsonStringByName(appInfo, "versionCode") );
		params.put("msi", msi );//手机IMEI码
		params.put("phone_type",Product+"_"+Mode); //手机型号
		params.put("ip",ip); //手机型号
		params.put("time",time); //当前时间
		KnLog.log(" Login params :"+params.toString());
		return params;
		
	}

}
