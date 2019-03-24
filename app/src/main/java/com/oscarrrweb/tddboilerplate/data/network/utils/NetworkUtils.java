package com.oscarrrweb.tddboilerplate.data.network.utils;

import android.text.TextUtils;

import com.oscarrrweb.tddboilerplate.data.Constants;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import timber.log.Timber;

public class NetworkUtils {

    public static String getRequestHash(String authToken, byte[] requestBody) {
        if (TextUtils.isEmpty(authToken)) throw new IllegalArgumentException("authToken cannot be empty");
        if (requestBody == null) {
            requestBody = new byte[]{};
        }

        String HMAC_HASH = "HmacSHA512";
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String hashKey = authToken + dateFormat.format(now);
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                hashKey.getBytes(Charset.forName(Constants.CHAR_ENCODING)),
                HMAC_HASH
        );

        Mac mac;
        try {
            mac = Mac.getInstance(HMAC_HASH);
            mac.init(secretKeySpec);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            Timber.e(e.getMessage());
            return null;
        }

        Formatter formatter = new Formatter();
        byte[] bytes = mac.doFinal(requestBody);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }
}