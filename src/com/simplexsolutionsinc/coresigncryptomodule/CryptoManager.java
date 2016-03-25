package com.simplexsolutionsinc.coresigncryptomodule;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;

import com.simplexsolutionsinc.coresigncryptomodule.AesCbcWithIntegrity.CipherTextIvMac;
import com.simplexsolutionsinc.coresigncryptomodule.AesCbcWithIntegrity.SecretKeys;
import com.simplexsolutionsinc.coresignpreferencesmanager.PreferencesManager;

public class CryptoManager
{

	static final String defPassword = "7a345zxGH6";
	public static String encrypt(String seed, String data)
	{
		String result = "";
		SecretKeys key;
		String salt;
		try
		{
			PreferencesManager pref = PreferencesManager.getInstance();
			salt = pref.getString(PreferencesManager.SALT);
			if(salt.equals(""))
			{
				salt = AesCbcWithIntegrity.saltString(AesCbcWithIntegrity.generateSalt());
				
				pref.addString(PreferencesManager.SALT, salt);
			}
			
			key = AesCbcWithIntegrity.generateKeyFromPassword(seed, salt);
			CipherTextIvMac civ = AesCbcWithIntegrity.encrypt(data, key);
			result = civ.toString();
		}
		catch (GeneralSecurityException | UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static String decrypt(String seed, String data)
	{
		String result = "";
		PreferencesManager pref = PreferencesManager.getInstance();
		
		SecretKeys key;
		String salt = pref.getString(PreferencesManager.SALT);
		if(salt.equals(""))
			return "";
		try
		{
			key = AesCbcWithIntegrity.generateKeyFromPassword(seed, salt);
			CipherTextIvMac civ = new CipherTextIvMac(data);
			result = AesCbcWithIntegrity.decryptString(civ, key);
		}
		catch (GeneralSecurityException | UnsupportedEncodingException | IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		return result;
	}
	public static void savePassCode(String passCode, Context context)
	{
		String encString = encrypt(passCode, passCode);
		PreferencesManager pref = PreferencesManager.getInstance();
		pref.addString(PreferencesManager.PASSCODE, encString);
	}

	public static void savePattern(String pattern, Context context)
	{
		String encString = encrypt(pattern, pattern);
		PreferencesManager pref = PreferencesManager.getInstance();
		pref.addString(PreferencesManager.PATTERN, encString);
	}

	public static void savePassword(String code, String password,
			Context context)
	{
		String android_id = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID); 
		String key = code + defPassword + android_id;
		String encString = encrypt(key, password);
		Log.v("encString Size", encString.length() + "");
		Log.v("encString", encString);
		PreferencesManager pref = PreferencesManager.getInstance();
		pref.addString(PreferencesManager.CRYPTOPASSWORD, encString);
		
	}

	public static boolean verifyPassCode(String passCode, Context context)
	{

		boolean result = false;
		PreferencesManager pref = PreferencesManager.getInstance();
		String decPasscode = pref.getString(PreferencesManager.PASSCODE);
		decPasscode = decrypt(passCode, decPasscode);
		
		if (decPasscode.length() == 0) return false;
		if (passCode.equals(decPasscode))
			result = true;
		return result;
	}

	public static boolean verifyPattern(String pattern, Context context)
	{
		boolean result = false;
		PreferencesManager pref = PreferencesManager.getInstance();
		String decPattern = pref.getString(PreferencesManager.PATTERN);
		decPattern = decrypt(pattern, decPattern);
		
		if (decPattern.length() == 0) return false;
		if (pattern.equals(decPattern))
			result = true;
		return result;
	}

	public static String getPassword(String code, Context context)
	{
		String android_id = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID); 
		String key = code + defPassword + android_id;
		PreferencesManager pref = PreferencesManager.getInstance();

		String data = PreferencesManager.getInstance().getString(
				PreferencesManager.CRYPTOPASSWORD);

		String result = decrypt(key, data);
		return result;
	}


}
