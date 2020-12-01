package com.ydp.mylibrary.http2;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 对返回数据进行拆分解析
 */
public class ResultJsonDeser implements JsonDeserializer<RequestResult<?>> {

    @Override
    public RequestResult<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

        RequestResult response = new RequestResult();
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();

            Object codeObj = jsonObject.get("code");

            if (codeObj == null) {

                JsonElement resCode = jsonObject.get("resCode");
                if (resCode != null && "0".equals(resCode.getAsString()))
                    response.code = 0;
                else
                    response.code = 250;
                JsonElement resMsg = jsonObject.get("resMsg");
                if (resMsg != null)
                    response.message = resMsg.getAsString();
                JsonElement data = jsonObject.get("data");


                if(data != null){

                    try {
                        Type itemType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];

                        response.data = context.deserialize(data, itemType);
                    } catch (JsonSyntaxException e) {
                        response.code = 2;
                        response.message = "服务器数据解析异常" + e.getMessage();
                    }

                }

            } else {
                int code = Integer.parseInt(codeObj.toString());
                response.code = code;
                response.message = jsonObject.get("message").getAsString();
                Type itemType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];

                try {
                    response.data = context.deserialize(jsonObject.get("data"), itemType);
                } catch (JsonSyntaxException e) {
                    response.code = 2;
                    response.message = "服务器数据解析异常"  + e.getMessage();
                }

            }
        }
        return response;
    }

}
