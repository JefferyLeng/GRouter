package cc.guider.architeature.router.template;

import java.util.HashMap;
import java.util.Map;

import cc.guider.architeature.annotation.model.GRouterBean;
import cc.guider.architeature.grouter.api.GRouterLoadPath;
import cc.guider.architeature.router.MainActivity;

public class GRouter$$Path$$MineCenter implements GRouterLoadPath {


    @Override
    public Map<String, GRouterBean> loadPath() {
        HashMap<String, GRouterBean> grouterBeanHashMap = new HashMap<>();

        //for循环添加 order 模拟apt生成
        grouterBeanHashMap.put("minecenter", new GRouterBean.Builder()
                .setType(GRouterBean.Type.ACTIVITY)
                .setClazz(MainActivity.class)
                .setGroup("minecenter")
                .setPath("/minecenter/Test1Ativity")
                .build());

        grouterBeanHashMap.put("minecenter", new GRouterBean.Builder()
                .setType(GRouterBean.Type.ACTIVITY)
                .setClazz(MainActivity.class)
                .setGroup("minecenter")
                .setPath("/minecenter/Test2Ativity")
                .build());

        return grouterBeanHashMap;
    }
}
