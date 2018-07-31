package com.run.ycl.xml;

import com.run.ycl.xml.utils.RedisUtils;
import com.run.ycl.xml.utils.XMLParaseUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConfigManager {

    public static void main(String[] args) {
        //协议数据转换映射
        String in2OutMapPath = "F:\\ycl\\ycl-demo-data\\run_standard.xml";
        Map<String, String> in2OutMap = uploadStandardIn2Out(in2OutMapPath);
        saveMapToRedis(in2OutMap);

        //协议数据集字段说明
        //WA_BASIC_0021 数据待待补充
        String indexXmlPath = "F:\\ycl\\ycl-demo-data\\run_index.xml";
        Map<String, String> indexMap = uploadIndexXml(indexXmlPath);
        saveMapToRedis(indexMap);

        //dataMappingXML加载
        String dataMappingPath = "F:\\ycl\\ycl-demo-data\\DataMapping.xml";
        Map<String, String> dataMappingMap = uploadDataMappingXml(dataMappingPath);
        saveMapToRedis(dataMappingMap);

        //DataValidateXML加载
        String dataValidataXmlPath = "F:\\ycl\\ycl-demo-data\\DataValidate.xml";
        Map<String, String> dataValidataMap = uploadDataValidataXml(dataValidataXmlPath);
        saveMapToRedis(dataValidataMap);

        //NormalizingXML加载
        String normalizingPath = "F:\\ycl\\ycl-demo-data\\DataValidate.xml";
        Map<String, String> normalizingMap = uploadNormalizingXml(normalizingPath);
        saveMapToRedis(dataValidataMap);

        System.out.println("end");
    }

    //加载NormalizingXml
    public static Map<String, String> uploadNormalizingXml(String path) {
        Map normalizingMap = new HashMap();
        JSONObject standardJsonObject = XMLParaseUtils.xml2jsonObject(path,"utf-8");
        JSONObject standardSource = standardJsonObject.getJSONObject("PolicyFile");
        JSONArray jsonArray = null;

        try {
            jsonArray = standardSource.getJSONArray("Normalizing");
            Iterator<Object>  normalizingIterator = jsonArray.iterator();
            //处理CodeMap
            while (normalizingIterator.hasNext()) {
                JSONObject normalizingdata = (JSONObject) normalizingIterator.next();
                normalizingMap.put("Normalizing_" + normalizingdata.getString("DataSet"), normalizingdata.toString());
            }
        } catch (JSONException exception) {
            //解决只有一个item时，xml转JsonObject时，Item被转成JsonObject
            JSONObject normalizingdata = standardSource.getJSONObject("Normalizing");;
            normalizingMap.put("Normalizing_" + normalizingdata.getString("DataSet"), normalizingdata.toString());
        }

        System.out.println(normalizingMap.toString());
        return normalizingMap;
    }

    //加载DataValidateXML
    public static Map<String, String> uploadDataValidataXml(String path) {
        Map dataValidataMap = new HashMap();
        JSONObject standardJsonObject = XMLParaseUtils.xml2jsonObject(path,"utf-8");
        JSONObject standardSource = standardJsonObject.getJSONObject("PolicyFile");
        JSONArray jsonArray = null;

        try {
            jsonArray = standardSource.getJSONArray("Normalizing");
            Iterator<Object>  normalizingIterator = jsonArray.iterator();
            //处理CodeMap
            while (normalizingIterator.hasNext()) {
                JSONObject normalizingdata = (JSONObject) normalizingIterator.next();
                dataValidataMap.put("DataValidata_" + normalizingdata.getString("DataSet"), normalizingdata.toString());
            }
        } catch (JSONException exception) {
            //解决只有一个item时，xml转JsonObject时，Item被转成JsonObject
            JSONObject normalizingdata = standardSource.getJSONObject("Normalizing");;
            dataValidataMap.put("DataValidata_" + normalizingdata.getString("DataSet"), normalizingdata.toString());
        }

        System.out.println(dataValidataMap.toString());
        return dataValidataMap;
    }

    //加载dataMappingXML
    public static Map<String, String> uploadDataMappingXml(String path) {
        Map dataMappingMap = new HashMap();
        JSONObject standardJsonObject = XMLParaseUtils.xml2jsonObject(path,"utf-8");
        JSONObject standardSource = standardJsonObject.getJSONObject("PolicyFile");
        JSONArray jsonArray = null;

        if (standardSource.has("CodeMap")) {
            try {jsonArray = standardSource.getJSONArray("CodeMap");
                Iterator<Object>  codeMapIterator = jsonArray.iterator();
                //处理CodeMap
                while (codeMapIterator.hasNext()) {
                    JSONObject codeMapdata = (JSONObject) codeMapIterator.next();
                    dataMappingMap.put("Data_" + codeMapdata.getString("Name"), codeMapdata.toString());
                }
            } catch (JSONException exception) {
                //解决只有一个item时，xml转JsonObject时，Item被转成JsonObject
                JSONObject codeMapdata = standardSource.getJSONObject("CodeMap");;
                dataMappingMap.put("CodeMap_" + codeMapdata.getString("Name"), codeMapdata.toString());
            }
        }

        if (standardSource.has("Normalizing")) {
            try {jsonArray = standardSource.getJSONArray("Normalizing");
                Iterator<Object>  normalizingIterator = jsonArray.iterator();
                //处理Normalizing
                while (normalizingIterator.hasNext()) {
                    JSONObject normalizingdata = (JSONObject) normalizingIterator.next();
                    dataMappingMap.put(normalizingdata.getString("SDataSet") + "_2_" + normalizingdata.getString("DDataSet"), normalizingdata.toString());
                }
            } catch (JSONException exception) {
                //解决只有一个item时，xml转JsonObject时，Item被转成JsonObject
                JSONObject normalizingdata = standardSource.getJSONObject("Normalizing");;
                dataMappingMap.put(normalizingdata.getString("SDataSet") + "_2_" + normalizingdata.getString("DDataSet"), normalizingdata.toString());
            }
        }
        System.out.println(dataMappingMap.toString());
        return dataMappingMap;
    }

    //加载输入输出数据集说明
    public static Map<String, String> uploadIndexXml(String path) {
        Map indexMap = new HashMap();
        JSONObject standardJsonObject = XMLParaseUtils.xml2jsonObject(path,"utf-8");
        JSONObject standardSource = standardJsonObject.getJSONObject("MESSAGE").getJSONObject("DATASET").getJSONObject("DATA").getJSONObject("DATASET");
        JSONArray jsonArray = null;
        try {
            jsonArray = standardSource.getJSONArray("DATA");
            Iterator<Object>  iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                //data表示每个数据协议的说明 A010004(数据集代码)
                JSONObject data = (JSONObject) iterator.next();

                Iterator<Object> itemIterator = data.getJSONArray("ITEM").iterator();
                String a010004 = "";
                while (itemIterator.hasNext()) {
                    JSONObject item = (JSONObject) itemIterator.next();
                    if (item.optString("key", "").equals("A010004")) {
                        a010004 = item.getString("val");
                    }
                }
                indexMap.put("index_des_" + a010004, data.toString());
            }
        } catch (JSONException exception) {
            //解决只有一个item时，xml转JsonObject时，Item被转成JsonObject
            JSONObject data = standardSource.getJSONObject("DATA");;

            Iterator<Object> itemIterator = data.getJSONArray("ITEM").iterator();
            String a010004 = "";
            while (itemIterator.hasNext()) {
                JSONObject item = (JSONObject) itemIterator.next();
                if (item.optString("key", "").equals("A010004")) {
                    a010004 = item.getString("val");
                }
            }
            indexMap.put("index_des_" + a010004, data.toString());
        }

        System.out.println(indexMap.toString());
        return indexMap;
    }

    //加载输入输出映射关系
    public static Map<String, String> uploadStandardIn2Out(String path) {
        Map in2outMap = new HashMap();
        //输入输出映射关系描述
        JSONObject standardJsonObject = XMLParaseUtils.xml2jsonObject(path,"utf-8");
        JSONObject standardSource = standardJsonObject.getJSONObject("PolicyFile").getJSONObject("source_609");
        JSONArray jsonArray = null;
        try {
            jsonArray = standardSource.getJSONArray("Item");
            Iterator<Object>  iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                JSONObject jsonObject = (JSONObject) iterator.next();
                String dataSrc = jsonObject.getString("DataSrc");
                String dataOut = jsonObject.getString("DstOut");

                System.out.println("src:" + dataSrc + "======des:" + dataOut);
                in2outMap.put("in2out_" + dataSrc.toUpperCase(), dataOut.toUpperCase());
            }
        } catch (JSONException exception) {
            //解决只有一个item时，xml转JsonObject时，Item被转成JsonObject
            JSONObject jsonObject = standardSource.getJSONObject("Item");
            String dataSrc = jsonObject.getString("DataSrc");
            String dataOut = jsonObject.getString("DstOut");

            System.out.println("src:" + dataSrc + "======des:" + dataOut);
            in2outMap.put("in2out_" + dataSrc.toUpperCase(), dataOut.toUpperCase());
        }

        System.out.println(in2outMap.toString());
        return in2outMap;
    }
    private static void saveMapToRedis(Map<String, String> map) {
        Jedis jedis = RedisUtils.getRedisClient();
        map.forEach((String k, String v) -> {
            jedis.set(k, v);
        });
        jedis.close();
    }
}
