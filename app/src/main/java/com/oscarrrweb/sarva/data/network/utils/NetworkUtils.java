package com.oscarrrweb.sarva.data.network.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.oscarrrweb.sarva.data.Constants;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import timber.log.Timber;

/**
 * Utility class for the <code>network</code> package.
 *
 * @author Johnny Spence
 * @version 1.0.0
 */
public final class NetworkUtils {

    /**
     * Returns a HMAC-SHA512 hash from a given auth token and API server request body. Used
     * for server-side validation an API call.
     *
     * @param authToken     The auth token
     * @param requestBody   The request body as a byte array
     * @return              The HMAC-SHA512 hash
     */
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

    /**
     * Checks if the device has an active internet connection.
     *
     * @return True device with internet connection, otherwise false.
     */
    public static boolean hasInternetConnection(@NonNull Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
