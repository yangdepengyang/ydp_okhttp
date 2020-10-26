package com.ydp.mylibrary.http3;

import com.ydp.mylibrary.util.LogUtil;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

public class LoggingInterceptor implements Interceptor {
    private static final String TAG = "LoggingInterceptor-->";
    private static final String GLOBAL_HEADER_TOKEN = "X-Access-Token";
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        RequestBody requestBody = originalRequest.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage = "--> " + originalRequest.method() + ' ' + originalRequest.url() + ' ' + protocol;
        if (hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }

        LogUtil.longlog(TAG, requestStartMessage);

        if (hasRequestBody) {
            // Request body headers are only present when installed as a network interceptor. Force
            // them to be included (when available) so there values are known.
            if (requestBody.contentType() != null) {
                LogUtil.longlog(TAG, "Content-Type: " + requestBody.contentType());
            }
            if (requestBody.contentLength() != -1) {
                LogUtil.longlog(TAG, "Content-Length: " + requestBody.contentLength());
            }
        }

        Headers headers = originalRequest.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            String name = headers.name(i);
            // Skip headers from the request body as they are explicitly logged above.
            if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                LogUtil.longlog(TAG, name + ": " + headers.value(i));
            }
        }

        if (!hasRequestBody) {
            LogUtil.longlog(TAG, "--> END " + originalRequest.method());
        } else if (bodyEncoded(originalRequest.headers())) {
            LogUtil.longlog(TAG, "--> END " + originalRequest.method() + " (encoded body omitted)");
        } else {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            if (isPlaintext(buffer)) {
                LogUtil.longlog(TAG, buffer.readString(charset));
                LogUtil.longlog(TAG, "--> END " + originalRequest.method()
                        + " (" + requestBody.contentLength() + "-byte body)");
            } else {
                LogUtil.longlog(TAG, "--> END " + originalRequest.method() + " (binary "
                        + requestBody.contentLength() + "-byte body omitted)");
            }
        }


        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(originalRequest);
        } catch (Exception e) {
            LogUtil.longlog(TAG, "<-- HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        LogUtil.longlog(TAG, "<-- " + response.code() + ' ' + response.message() + ' '
                + response.request().url() + " (" + tookMs + "ms" + (", "
                + bodySize + " body") + ')');

        for (int i = 0, count = headers.size(); i < count; i++) {
            LogUtil.longlog(TAG, headers.name(i) + ": " + headers.value(i));
        }

        if (!HttpHeaders.hasBody(response)) {
            LogUtil.longlog(TAG, "<-- END HTTP");
        } else if (bodyEncoded(response.headers())) {
            LogUtil.longlog(TAG, "<-- END HTTP (encoded body omitted)");
        } else {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {

                    LogUtil.longlog(TAG, "Couldn't decode the response body; charset is likely malformed.");
                    LogUtil.longlog(TAG, "<-- END HTTP");

                    return response;
                }
            }

            if (!isPlaintext(buffer)) {

                LogUtil.longlog(TAG, "<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                return response;
            }

            if (contentLength != 0) {
                LogUtil.longlog(TAG, "");
                LogUtil.longlog(TAG, buffer.clone().readString(charset));
            }

            LogUtil.longlog(TAG, "<-- END HTTP (" + buffer.size() + "-byte body)");
        }


        // 重新构建请求
        return response;
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}
