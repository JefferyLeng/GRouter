package cc.guider.architeature.router.template;

import android.content.Intent;
import android.os.Bundle;

import cc.guider.architeature.grouter.api.ParameterLoad;
import cc.guider.architeature.router.MainActivity;

/**
 * 处理@Parameter注解的模板代码
 * APT + JavaPoet生成代码 需要和Activity同包
 * @author JefferyLeng
 * @date 2019-08-21
 */
public class ParameterTemplate implements ParameterLoad {

    @Override
    public void loadParameter(Object target) {
        MainActivity activity = (MainActivity) target;
        Intent intent = activity.getIntent();
        Bundle bundle = intent.getExtras();
    }
}
