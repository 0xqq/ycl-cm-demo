package com.run.ycl.xml.utils;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class XMLParaseUtils {

    /**
     * 将xml文件转换成jsonString
     * @param path
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public static String xml2jsonString(String path) throws JSONException, IOException {
        return xml2jsonString(path, Charset.defaultCharset().toString());
    }

    /**
     *
     * @param path
     * @param charset
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public static String xml2jsonString(String path, String charset) throws JSONException {
        InputStream in = null;
        try {
            in = new FileInputStream(path);
            String xml = IOUtils.toString(in, charset);
            JSONObject xmlJSONObj = XML.toJSONObject(xml);
            return xmlJSONObj.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     *
     * @param path
     * @param charset
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public static JSONObject xml2jsonObject(String path, String charset) throws JSONException {
        InputStream in = null;
        JSONObject xmlJSONObj = null;
        try {
            in = new FileInputStream(path);
            String xml = IOUtils.toString(in, charset);
            xmlJSONObj = XML.toJSONObject(xml);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return xmlJSONObj;
    }
    public static void main(String[] args) throws JSONException, IOException {

        String path = "F:\\ycl\\YCL_配置\\protocol\\source_609\\run_standard.xml";
        String string = xml2jsonString(path,"utf-8");
        System.out.println(string);

    }
}
