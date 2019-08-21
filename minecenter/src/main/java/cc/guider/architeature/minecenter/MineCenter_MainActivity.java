package cc.guider.architeature.minecenter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import cc.guider.architeature.annotation.GRouter;
import cc.guider.architeature.annotation.Parameter;
import cc.guider.architeature.grouter.api.manager.ParameterManager;
import cc.guider.architeature.grouter.api.manager.RouterManager;

@GRouter(path = "/minecenter/MineCenter_MainActivity")
public class MineCenter_MainActivity extends AppCompatActivity {

    @Parameter
    String userName;

    @Parameter
    int age;

    @Parameter
    boolean isSuccess;

    @Parameter
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_center_main_activity);
        ParameterManager.getInstance().loadParameter(this);
        Toast.makeText(getApplicationContext(), String.format("onCreate: >>> userName :%s,age = %d,isSuccess=%b,email=%s",userName,age,isSuccess,email), Toast.LENGTH_LONG).show();
    }

    public void jumpToOrder(View view) {
        RouterManager.getInstance()
                .build("/orderbiz/OrderBiz_MainActivity")
                .withBoolean("loginFlag",true)
                .navigation(this);
    }
}
