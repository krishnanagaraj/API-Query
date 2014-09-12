package com.goeuro.service;


import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

/**
 * This is a wrapper on the Apache HTTP Client.
 */
public class HttpClient {

    // HTTP client properties
    private static int TIME_OUT = 60 * 1000; // 1 second
    private static int SOCKET_BUFFER_SIZE_BYTES = 8192;
    private static int MAX_TOTAL_CONNECTIONS = 100;
    private static int MAX_CONNECTIONS_PER_ROUTE = 20;
    private static int CONNECTION_KEEP_ALIVE_TIMEOUT_SECS = 100;
    private static boolean STALE_CONNECTION_CHECK_ENABLED = false;

    // Apache HttpClient instance
    private static DefaultHttpClient _httpClient = null;

    // Initialize Apache HttpClient
    static {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes Apache HttpClient.
     *
     * @throws Exception
     */
    private static void init() throws Exception {
        HttpParams params = new BasicHttpParams();

        ConnManagerParams.setMaxTotalConnections(params, MAX_TOTAL_CONNECTIONS);

        // Increase default max connection per route to 20
        System.setProperty("http.conn-manager.max-per-route", String.valueOf(MAX_CONNECTIONS_PER_ROUTE));

        // Create and initialize scheme registry
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        // TrustManager that trusts everybody.
        TrustManager easyTrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // I accept everything
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                // I accept everything
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        // HostnameVerifier that accepts everybody.
        X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {

            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }

            @Override
            public void verify(String s, SSLSocket sslSocket) throws IOException {

            }

            @Override
            public void verify(String s, X509Certificate x509Certificate) throws SSLException {

            }

            @Override
            public void verify(String s, String[] strings, String[] strings1) throws SSLException {

            }
        };

        // SSL setup
        SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, new TrustManager[] { easyTrustManager }, null);
        SSLSocketFactory socketFactory = new SSLSocketFactory(sslcontext);
        socketFactory.setHostnameVerifier(hostnameVerifier);
        schemeRegistry.register(new Scheme("https", socketFactory, 443));

        // Miscellaneous settings
        HttpClientParams.setRedirecting(params, false);
        // Enable stale check to avoid intermittent "Connection reset" errors
        params.setBooleanParameter(HttpConnectionParams.STALE_CONNECTION_CHECK, STALE_CONNECTION_CHECK_ENABLED);
        params.setIntParameter(HttpConnectionParams.SOCKET_BUFFER_SIZE, SOCKET_BUFFER_SIZE_BYTES);

        int timeout;
        String httpClientTimeout = "120000";
        try {
            timeout = Integer.parseInt(httpClientTimeout);
        } catch (NumberFormatException ne) {
            timeout = TIME_OUT;
        }
        HttpConnectionParams.setSoTimeout(params, timeout);
        HttpConnectionParams.setConnectionTimeout(params, timeout);

        // Use the ThreadSafeClientConnManager, since more than one thread can access this client.
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);

        _httpClient = new TestHttpClient(cm, params);
        _httpClient.setKeepAliveStrategy(new ConnectionKeepAliveStrategyImpl());
    }

    private static class TestHttpClient extends DefaultHttpClient {

        private TestHttpClient(ClientConnectionManager cm, HttpParams params) {
            super(cm, params);
        }

        @Override
        public HttpContext createHttpContext() {
            return super.createHttpContext();
        }

    }


    /**
     * ConnectionKeepAlive Strategy implementation.
     */
    private static class ConnectionKeepAliveStrategyImpl extends DefaultConnectionKeepAliveStrategy {
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            long retVal = super.getKeepAliveDuration(response, context);
            if (retVal == -1 && CONNECTION_KEEP_ALIVE_TIMEOUT_SECS != -1) {
                retVal = CONNECTION_KEEP_ALIVE_TIMEOUT_SECS * 1000L;
            }
            return retVal;
        }
    }

    /**
     * Executes a HTTP GET request.
     *
     * @param requestUrl The URL to test, without any query parameters.
     * @return
     */
    public static HttpResponse executeAsGet(String requestUrl) throws ClientProtocolException, IOException {
        HttpGet httpGet = new HttpGet(requestUrl);
        _httpClient.getParams().setBooleanParameter("http.protocol.expect-continue", false);
        try {
            return _httpClient.execute(httpGet);
        } catch (ClientProtocolException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
    }

}
