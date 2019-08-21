package cc.guider.architeature.router;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cc.guider.architeature.annotation.GRouter;
import cc.guider.architeature.annotation.Parameter;
import cc.guider.architeature.common.utils.CommonUtil;
import cc.guider.architeature.common.utils.orderbiz.OrderAddress;
import cc.guider.architeature.common.utils.orderbiz.OrderAddressResponseBean;
import cc.guider.architeature.common.utils.orderbiz.OrderDrawable;
import cc.guider.architeature.grouter.api.manager.ParameterManager;
import cc.guider.architeature.grouter.api.manager.RouterManager;

@GRouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Parameter(name = "/orderbiz/getOrderAddress")
    OrderAddress orderAddress;

    @Parameter(name = "/orderbiz/getOrderDrawable")
    OrderDrawable orderDrawable;

    ImageView mIvPic;
    TextView mTvCity;
    OrderAddressResponseBean responseBean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.isRelease) {
            Log.e("APP", "当前为：集成化模式，除app可运行，其他子模块都是Android Library");
        } else {
            Log.e("APP", "当前为：组件化模式，app/order/personal子模块都可独立运行");
        }
        ParameterManager.getInstance().loadParameter(this);
        mIvPic = findViewById(R.id.imageView);
        mTvCity = findViewById(R.id.textView);
        mIvPic.setImageResource(orderDrawable.getDrawable());
        loadData();
    }

    private void loadData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                responseBean = orderAddress.getOrderAddress("1562103");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String city = responseBean.getResult().getCity();
                        Log.d(CommonUtil.LOG_TAG, "run: >>> city : " + city);
                        mTvCity.setText(city);
                    }
                });
            }
        }).start();

    }

    public void jumpOrder(View view) {
        RouterManager.getInstance()
                .build("/orderbiz/OrderBiz_MainActivity")
                .withString("name", "jeffery")
                .withInt("agep",23)
                .navigation(this,002);
    }

    public void jumpPersonal(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("userName", "jeffery");
        bundle.putInt("age", 23);
        bundle.putBoolean("isSuccess", true);
        bundle.putString("email", "JefferyLeng@guider.cc");

        RouterManager.getInstance()
                .build("/minecenter/MineCenter_MainActivity")
                .withBundle(bundle)
                .navigation(this, 110);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == 002 && resultCode == 001) {
            int orderBackResult = data.getIntExtra("orderBackResult", 0);
            Toast.makeText(getApplicationContext(), "order : " + orderBackResult, Toast.LENGTH_SHORT).show();
        }
        if (requestCode == 110) {
            String minecenterBackResult = data.getStringExtra("minecenterBackResult");
            Toast.makeText(getApplicationContext(), "minecenter : " + minecenterBackResult, Toast.LENGTH_SHORT).show();
        }
    }
}
