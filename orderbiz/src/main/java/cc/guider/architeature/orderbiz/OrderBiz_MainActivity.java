package cc.guider.architeature.orderbiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cc.guider.architeature.annotation.GRouter;
import cc.guider.architeature.annotation.Parameter;
import cc.guider.architeature.common.utils.CommonUtil;
import cc.guider.architeature.grouter.api.manager.ParameterManager;
import cc.guider.architeature.grouter.api.manager.RouterManager;

@GRouter(path = "/orderbiz/OrderBiz_MainActivity")
public class OrderBiz_MainActivity extends AppCompatActivity {

    @Parameter
    String name;

    @Parameter(name = "agep")
    int age;

    @Parameter(name = "loginFlag")
    boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_biz_main_activity);
        ParameterManager.getInstance().loadParameter(this);
        Log.d(CommonUtil.LOG_TAG, "onCreate: >>> OrderBiz_MainActivity : name = " + name + ",age = " + age);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), "isLogin" + isLogin, Toast.LENGTH_SHORT).show();
    }

    public void backToMain(View view) {
        if (isLogin) {
            RouterManager.getInstance()
                    .build("/app/MainActivity")
                    .navigation(this);
            return;
        }
        RouterManager.getInstance()
                .build("/app/MainActivity")
                .withResultInt("orderBackResult",156)
                .navigation(this,001);
    }
}
