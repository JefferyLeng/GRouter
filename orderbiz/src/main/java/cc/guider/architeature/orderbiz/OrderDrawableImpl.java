package cc.guider.architeature.orderbiz;

import cc.guider.architeature.annotation.GRouter;
import cc.guider.architeature.common.utils.orderbiz.OrderDrawable;

/**
 * 提供给其他moudle使用的
 * @author JefferyLeng
 * @date 2019-08-21
 */
@GRouter(path = "/orderbiz/getOrderDrawable")
public class OrderDrawableImpl implements OrderDrawable {

    @Override
    public int getDrawable() {
        return R.drawable.ic_ac_unit_black_24dp;
    }
}
