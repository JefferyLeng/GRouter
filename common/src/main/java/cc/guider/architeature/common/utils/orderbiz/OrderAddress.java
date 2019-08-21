package cc.guider.architeature.common.utils.orderbiz;

import cc.guider.architeature.grouter.api.BizCall;

/**
 * app模块要调用 订单模块的查询接口
 * @author JefferyLeng
 * @date 2019-08-21
 */
public interface OrderAddress extends BizCall {

    OrderAddressResponseBean getOrderAddress(String phone);
}
