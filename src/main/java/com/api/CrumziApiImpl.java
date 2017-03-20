package com.api;

import com.cards.MyResponse;
import com.cards.Payload;
import com.clients.List;
import com.clients.MyClients;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;



public class CrumziApiImpl implements CrumziApi {
    @Override
    public java.util.List<List> getBuyerCards(String sessionToken, long date_from) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        ResponseHandler<String> handler = new BasicResponseHandler();
        ObjectNode request = mapper.createObjectNode();

        HttpPost webshopPost = new HttpPost("http://rest-seller-api.crumzi.com/api/private/cards/seller/getlist");
        StringEntity entity = new StringEntity(request.toString());
        webshopPost.setEntity(entity);

        webshopPost.setHeader("Content-type", "application/json");
        webshopPost.setHeader("Session-Token", sessionToken);

        String body = String.format("{\n" +
                "  \"card_type_id\" : null,    \n" +
                "  \"search_string\" : null,    \n" +
                "  \"date_created_from\" : %s,\n" +
                "  \"date_created_to\" : null,  \n" +
                "  \"cursor\": \n" +
                "  {\n" +
                "    \"page_no\": \"0\",      \n" +
                "    \"page_size\" : \"500\"    \n" +
                "  }\n" +
                "}", date_from);
        entity = new StringEntity(body);
        webshopPost.setEntity(entity);


        CloseableHttpResponse webshopResponce = httpclient.execute(webshopPost);
        String webshopBody = handler.handleResponse(webshopResponce);
        MyClients myClients = mapper.readValue(webshopBody, MyClients.class);
        httpclient.close();
        if (Integer.parseInt(myClients.getResponseStatus().split("\\.")[1]) == 200) {
            return myClients.getPayload().getList();
        } else throw new IllegalStateException("api/private/cards/seller/getlist (resp1) error ");

    }
    @Override
    public Payload getInfoBuyCard(String cartId, String sessionToken) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        ResponseHandler<String> handler = new BasicResponseHandler();
        ObjectNode request = mapper.createObjectNode();

        HttpPost webshopPost = new HttpPost("http://rest-seller-api.crumzi.com/api/private/cards/seller/get");
        StringEntity entity = new StringEntity(request.toString());

        webshopPost.setEntity(entity);
        webshopPost.setHeader("Content-type", "application/json");
        webshopPost.setHeader("Session-Token", sessionToken);
        entity = new StringEntity("{\"id\":\"" + cartId + "\"}");
        webshopPost.setEntity(entity);

        CloseableHttpResponse webshopResponce = httpclient.execute(webshopPost);
        String webshopBody = handler.handleResponse(webshopResponce);
        MyResponse myResponse = mapper.readValue(webshopBody, MyResponse.class);
        httpclient.close();
        if (Integer.parseInt(myResponse.getResponse_status().split("\\.")[1]) == 200) {
            return myResponse.getPayload();
        } else throw new IllegalStateException("api/private/cards/seller/get (resp2) error ");


    }
}
