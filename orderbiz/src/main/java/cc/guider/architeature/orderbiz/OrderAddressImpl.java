package cc.guider.architeature.orderbiz;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

import cc.guider.architeature.annotation.GRouter;
import cc.guider.architeature.common.utils.CommonUtil;
import cc.guider.architeature.common.utils.orderbiz.OrderAddress;
import cc.guider.architeature.common.utils.orderbiz.OrderAddressResponseBean;
import cc.guider.architeature.orderbiz.network.OrderRequestService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 *
 * @author JefferyLeng
 * @date 2019-08-21
 */
@GRouter(path = "/orderbiz/getOrderAddress")
public class OrderAddressImpl implements OrderAddress {

    private final static String BASE_URL = "http://apis.juhe.cn/";

    @Override
    public OrderAddressResponseBean getOrderAddress(String phone) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();
        OrderRequestService host = retrofit.create(OrderRequestService.class);

        // Retrofit GET同步请求
        Call<ResponseBody> call = host.get(phone, "5c7340e52bd7a64f4bc4caa440efe250");
        try {
            Response<ResponseBody> response  = call.execute();
            if (response != null && response.body() != null) {
                JSONObject jsonObject = JSON.parseObject(response.body().string());
                OrderAddressResponseBean orderBean = jsonObject.toJavaObject(OrderAddressResponseBean.class);
                Log.d(CommonUtil.LOG_TAG, "getOrderAddress json : " + orderBean.toString());
                return orderBean;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
