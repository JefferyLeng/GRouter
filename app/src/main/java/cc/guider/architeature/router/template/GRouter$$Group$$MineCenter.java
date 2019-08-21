package cc.guider.architeature.router.template;

import java.util.HashMap;
import java.util.Map;

import cc.guider.architeature.grouter.api.GRouterLoadGroup;
import cc.guider.architeature.grouter.api.GRouterLoadPath;

public class GRouter$$Group$$MineCenter implements GRouterLoadGroup {
    @Override
    public Map<String, Class<? extends GRouterLoadPath>> loadGroup() {
        HashMap<String, Class<? extends GRouterLoadPath>> map = new HashMap<>();
        map.put("minecenter",GRouter$$Path$$MineCenter.class);
        return map;
    }
}
