package com.netflix.appinfo;

/**
 * @author Nitesh Kant
 */
@SuppressWarnings("deprecation")
public class HealthCheckCallbackToHandlerBridge implements HealthCheckHandler {

    private final HealthCheckCallback callback;

    public HealthCheckCallbackToHandlerBridge() {
        callback = null;
    }

    public HealthCheckCallbackToHandlerBridge(HealthCheckCallback callback) {
        this.callback = callback;
    }

    @Override
    /**
     * 判断callback是否为空 ， 如果为空，则以当前实例的状态为准
     * 判断传入的当前实例的状态是否等于STARTING， OUT_OF_SERVICE 这两个状态，如果等于，则以当前实例的状态为准
     */
    public InstanceInfo.InstanceStatus getStatus(InstanceInfo.InstanceStatus currentStatus) {
        // 判断instance的状态。
        if (null == callback || InstanceInfo.InstanceStatus.STARTING == currentStatus
                || InstanceInfo.InstanceStatus.OUT_OF_SERVICE == currentStatus) { // Do not go to healthcheck handler if the status is starting or OOS.
            return currentStatus;
        }

        return callback.isHealthy() ? InstanceInfo.InstanceStatus.UP : InstanceInfo.InstanceStatus.DOWN;
    }
}
