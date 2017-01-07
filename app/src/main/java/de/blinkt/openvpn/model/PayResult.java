package de.blinkt.openvpn.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class PayResult implements Serializable {
	private String resultStatus;
	private String result;
	private String memo;

	public PayResult(Map<String, String> rawResult) {
		if (rawResult == null) {
			return;
		}
		Set<String> set = rawResult.keySet();
		for (String key : set) {
			if (TextUtils.equals(key, "resultStatus")) {
				resultStatus = rawResult.get(key);
			} else if (TextUtils.equals(key, "result")) {
				result = rawResult.get(key);
			} else if (TextUtils.equals(key, "memo")) {
				memo = rawResult.get(key);
			}
		}
	}

	@Override
	public String toString() {
		return "resultStatus={" + resultStatus + "};memo={" + memo
				+ "};result={" + result + "}";
	}

	/**
	 * @return the resultStatus
	 */
	public String getResultStatus() {
		return resultStatus;
	}

	/**
	 * @return the memo
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}
}
