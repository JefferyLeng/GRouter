package cc.guider.architeature.orderbiz.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 *
 * @author JefferyLeng
 * @date 2019-08-21
 */
public interface OrderRequestService {

    @GET("/mobile/get")
    Call<ResponseBody> get(@Query("phone")String phone,@Query("key") String key);

}
