package com.simplexsolutionsinc.coresigncryptomodule;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.simplexsolutionsinc.coresigncryptomodule.AesCbcWithIntegrity.CipherTextIvMac;
import com.simplexsolutionsinc.coresigncryptomodule.AesCbcWithIntegrity.SecretKeys;

public class MainActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String ckey  = "111aads";
		String data = "123456789";
		String encString;
		SecretKeys key;
		String salt;
		try
		{
			salt = AesCbcWithIntegrity.saltString(AesCbcWithIntegrity.generateSalt());
			key = AesCbcWithIntegrity.generateKeyFromPassword(ckey, salt);
			CipherTextIvMac civ = AesCbcWithIntegrity.encrypt(data, key);
			String temp = civ.toString();
			civ = new CipherTextIvMac(temp);
			String result = AesCbcWithIntegrity.decryptString(civ, key);
			Log.v("RESULT",result);
		}
		catch (GeneralSecurityException | UnsupportedEncodingException | IllegalArgumentException e)
		{
			
			e.printStackTrace();
		}
		
		
		
		
	}
}
