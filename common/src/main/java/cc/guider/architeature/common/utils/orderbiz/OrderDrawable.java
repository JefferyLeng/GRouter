package cc.guider.architeature.common.utils.orderbiz;

import cc.guider.architeature.grouter.api.BizCall;

/**
 * 订单模块对外暴露接口 其他模块可以获取res资源
 * @author JefferyLeng
 * @date 2019-08-21
 */
public interface OrderDrawable extends BizCall {

    int getDrawable();
}
