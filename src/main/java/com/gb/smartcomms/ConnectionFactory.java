package com.gb.smartcomms;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.log4j.Logger;

import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;

public class ConnectionFactory implements HttpURLConnectionFactory {

	private static Logger LOG = Logger.getLogger(ConnectionFactory.class);	
	
    Proxy proxy;

    String proxyHost;

    Integer proxyPort;

    SSLContext sslContext;

    public ConnectionFactory() {
    }

    public ConnectionFactory(String proxyHost, Integer proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    private void initializeProxy() {
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
    }

    @Override
    public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
    	
    	LOG.info("getHttpURLConnection");
    	
        initializeProxy();
        HttpURLConnection con = (HttpURLConnection) url.openConnection(proxy);
        
        //con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        
        //String encoded = new String(Base64.getEncoder().encode(new String("pjeffreys:WombleButt99").getBytes()));
        //con.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
        
        if (con instanceof HttpsURLConnection) {
            System.out.println("The valus is....");
            HttpsURLConnection httpsCon = (HttpsURLConnection) url.openConnection(proxy);
            httpsCon.setHostnameVerifier(getHostnameVerifier());
            httpsCon.setSSLSocketFactory(getSslContext().getSocketFactory());
            return httpsCon;
        } else {
            return con;
        }

    }

    
    
    public SSLContext getSslContext() {
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new SecureTrustManager()}, new SecureRandom());
        } catch (NoSuchAlgorithmException ex) {
        	LOG.error(ex);
        } catch (KeyManagementException ex) {
        	LOG.error(ex);
        }
        return sslContext;
    }

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname,
                    javax.net.ssl.SSLSession sslSession) {
                return true;
            }
        };
    }

}
