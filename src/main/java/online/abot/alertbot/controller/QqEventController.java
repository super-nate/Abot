package online.abot.alertbot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import online.abot.alertbot.constant.Constants;
import online.abot.alertbot.imp.QqServiceImpl;
import online.abot.alertbot.service.ImService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;

@RestController
public class QqEventController {

    private static final Logger LOGGER = Logger.getLogger(QqEventController.class);
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    @Value("${secret.token}")
    private String SECRET;

    @Autowired
    @Qualifier("NewQqService")
    ImService qqService;


    @PostMapping("/callback")
    @ResponseBody
    public String handleEvent(@RequestBody String request, HttpServletRequest servletRequest/*ServerHttpRequest servletRequest*/){

        //check X-Signature
        //String xSig = servletRequest.getHeaders().get("X-Signature").get(0).substring(5);
        String xSig = servletRequest.getHeader("X-Signature").substring(5);
        /*LOGGER.info(servletRequest.getHeaders().get("X-Signature").get(0));
        LOGGER.info(request);*/

        if(StringUtils.isEmpty(xSig)){
            return "";
        }

        try {
           /* SecretKeySpec keySpec = new SecretKeySpec(SECRET.getBytes(),"HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(keySpec);
            byte[] msg = mac.doFinal(request.getBytes());*/
            String sig = calculateRFC2104HMAC(request,SECRET);//new String(msg);

            if (!xSig.equals(sig)){
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //parse request
        JSONObject jsonObject = JSON.parseObject(request);
        String postType = jsonObject.getString("post_type");
        String subType = jsonObject.getString("sub_type");
        JSONObject response = new JSONObject();
        if ("message".equals(postType) && "friend".equals(subType)){
            String accountId = jsonObject.getString("message");
            String  qqId = Constants.QQ_PREFIX + jsonObject.getString("user_id");
            if (accountId.length()<30) {
                response.put("reply", "请确认您输入的是恒星账号");
                return response.toJSONString();
            }
            LOGGER.info("QQ message record: " + qqId + ": " + accountId);
            boolean result = qqService.subscribe(qqId, accountId);
            if (result) {
                response.put("reply", "绑定成功！");

            } else {
                response.put("reply", "重复绑定无效！");
            }
        }

        if ("request".equals(postType)){
            String requestType = jsonObject.getString("request_type");
            if("friend".equals(requestType)){
                response.put("approve",true);
            }
        }
        return response.toJSONString();
    }


    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    public static String calculateRFC2104HMAC(String data, String key)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
    {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return toHexString(mac.doFinal(data.getBytes()));
    }
}
