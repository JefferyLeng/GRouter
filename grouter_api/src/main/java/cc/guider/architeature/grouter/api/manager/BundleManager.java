package cc.guider.architeature.grouter.api.manager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cc.guider.architeature.grouter.api.BizCall;

/**
 * bundle 参数管理器
 * @author JefferyLeng
 * @date 2019-08-08
 */
public class BundleManager {

    private Bundle bundle = new Bundle();
    private boolean isResult;

    /**
     * 底层业务通信接口
     */
    private BizCall bizCall;

    BizCall getBizCall() {
        return bizCall;
    }

    void setBizCall(BizCall bizCall) {
        this.bizCall = bizCall;
    }

    Bundle getBundle() {
        return bundle;
    }
    boolean isResult() {
        return isResult;
    }

    // @NonNull不允许传null，@Nullable可以传null
    public BundleManager withString(@NonNull String key, @Nullable String value) {
        bundle.putString(key, value);
        return this;
    }

    public BundleManager withBoolean(@NonNull String key, boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public BundleManager withInt(@NonNull String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public BundleManager withBundle(@NonNull Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    /**
     * result的返回 会执行setResult(resultCode,intent) 和 finish();
     * @param key
     * @param value
     * @return
     */
    public BundleManager withResultString(@NonNull String key, @Nullable String value) {
        bundle.putString(key, value);
        isResult = true;
        return this;
    }

    public BundleManager withResultInt(@NonNull String key, @Nullable int value) {
        bundle.putInt(key, value);
        isResult = true;
        return this;
    }

    /**
     * todo 扩展更多类型支持
     * @param key
     * @param value
     * @return
     */
    public BundleManager withResultBoolean(@NonNull String key, @Nullable boolean value) {
        bundle.putBoolean(key, value);
        isResult = true;
        return this;
    }

    /**
     * 携带参数跳转到RouterManager
     * @param context
     * @return
     */
    public Object navigation(Context context) {
        return RouterManager.getInstance().navigation(context, this, -1);
    }

    /**
     * 携带参数跳转到RouterManager 支持返回值
     * @param context
     * @param code  可能是requestCode，也可能是resultCode。取决于isResult
     * @return
     */
    public Object navigation(Context context, int code) {
        return RouterManager.getInstance().navigation(context, this, code);
    }
}
