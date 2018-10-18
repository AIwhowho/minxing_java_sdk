package com.minxing.client.utils;

import com.minxing.client.model.ApiErrorException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

public class HttpUtil {
    public static String putJson(String url, Map<String, String> headers, String JsonBody) throws ApiErrorException {
        final URL httpurl;
        final StringBuilder result = new StringBuilder();
        try {
            httpurl = new URL(url);

            final HttpURLConnection httpConn = (HttpURLConnection) httpurl.openConnection();
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setRequestMethod("PUT");
            for (String key : headers.keySet()) {
                httpConn.setRequestProperty(key, headers.get(key));
            }
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            final OutputStream outputStream = httpConn.getOutputStream();
            outputStream.write(JsonBody.getBytes("utf-8"));
            outputStream.flush();
            outputStream.close();
            final InputStream inputStream = httpConn.getInputStream();
            if (httpConn.getResponseCode() != 200 && httpConn.getResponseCode() != 201 && httpConn.getResponseCode() != 202 && httpConn.getResponseCode() != 204) {
                throw new ApiErrorException(httpConn.getResponseCode(), httpConn.getResponseMessage());
            }
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(),"utf-8"));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            bufferedReader.close();
            inputStream.close();
            httpConn.disconnect();
        } catch (MalformedURLException e) {
            throw new ApiErrorException(e.getMessage(), 0, e);
        } catch (UnsupportedEncodingException e) {
            throw new ApiErrorException(e.getMessage(), 0, e);
        } catch (ProtocolException e) {
            throw new ApiErrorException(e.getMessage(), 0, e);
        } catch (IOException e) {
            throw new ApiErrorException(e.getMessage(), 0, e);
        }
        return result.toString();
    }

    public static String postJson(String url, Map<String, String> headers, String JsonBody) throws ApiErrorException {
        final URL httpurl;
        final StringBuilder result = new StringBuilder();
        try {
            httpurl = new URL(url);

            final HttpURLConnection httpConn = (HttpURLConnection) httpurl.openConnection();
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setRequestMethod("POST");
            for (String key : headers.keySet()) {
                httpConn.setRequestProperty(key, headers.get(key));
            }
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            final OutputStream outputStream = httpConn.getOutputStream();
            outputStream.write(JsonBody.getBytes("utf-8"));
            outputStream.flush();
            outputStream.close();
            final InputStream inputStream = httpConn.getInputStream();
            if (httpConn.getResponseCode() != 200 && httpConn.getResponseCode() != 201 && httpConn.getResponseCode() != 202 && httpConn.getResponseCode() != 204) {
                throw new ApiErrorException(httpConn.getResponseCode(), httpConn.getResponseMessage());
            }
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(),"utf-8"));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            bufferedReader.close();
            inputStream.close();
            httpConn.disconnect();
        } catch (MalformedURLException e) {
            throw new ApiErrorException(e.getMessage(), 0, e);
        } catch (UnsupportedEncodingException e) {
            throw new ApiErrorException(e.getMessage(), 0, e);
        } catch (ProtocolException e) {
            throw new ApiErrorException(e.getMessage(), 0, e);
        } catch (IOException e) {
            throw new ApiErrorException(e.getMessage(), 0, e);
        }
        return result.toString();
    }

    public static String get(String url, Map<String, String> headers) throws ApiErrorException {
        final URL httpurl;
        final StringBuilder result = new StringBuilder();
        try {
            httpurl = new URL(url);

            final HttpURLConnection httpConn = (HttpURLConnection) httpurl.openConnection();
            httpConn.setRequestMethod("GET");
            for (String key : headers.keySet()) {
                httpConn.setRequestProperty(key, headers.get(key));
            }
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            final InputStream inputStream = httpConn.getInputStream();
            if (httpConn.getResponseCode() != 200 && httpConn.getResponseCode() != 201 && httpConn.getResponseCode() != 202 && httpConn.getResponseCode() != 204) {
                throw new ApiErrorException(httpConn.getResponseCode(), httpConn.getResponseMessage());
            }
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(),"utf-8"));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            bufferedReader.close();
            inputStream.close();
            httpConn.disconnect();
        } catch (MalformedURLException e) {
            throw new ApiErrorException(e.getMessage(), 0, e);
        } catch (UnsupportedEncodingException e) {
            throw new ApiErrorException(e.getMessage(), 0, e);
        } catch (ProtocolException e) {
            throw new ApiErrorException(e.getMessage(), 0, e);
        } catch (IOException e) {
            throw new ApiErrorException(e.getMessage(), 0, e);
        }


        return result.toString();
    }
    public static String delete(String url, Map<String, String> headers) throws ApiErrorException {
        final URL httpurl;
        final StringBuilder result = new StringBuilder();
        try {
            httpurl = new URL(url);

            final HttpURLConnection httpConn = (HttpURLConnection) httpurl.openConnection();
            httpConn.setRequestMethod("DELETE");
            for (String key : headers.keySet()) {
                httpConn.setRequestProperty(key, headers.get(key));
            }
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            final InputStream inputStream = httpConn.getInputStream();
            if (httpConn.getResponseCode() != 200 && httpConn.getResponseCode() != 201 && httpConn.getResponseCode() != 202 && httpConn.getResponseCode() != 204) {
                throw new ApiErrorException(httpConn.getResponseCode(), httpConn.getResponseMessage());
            }
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(),"utf-8"));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            bufferedReader.close();
            inputStream.close();
            httpConn.disconnect();
        } catch (MalformedURLException e) {
            throw new ApiErrorException(e.getMessage(), 0, e);
        } catch (UnsupportedEncodingException e) {
            throw new ApiErrorException(e.getMessage(), 0, e);
        } catch (ProtocolException e) {
            throw new ApiErrorException(e.getMessage(), 0, e);
        } catch (IOException e) {
            throw new ApiErrorException(e.getMessage(), 0, e);
        }


        return result.toString();
    }
}
