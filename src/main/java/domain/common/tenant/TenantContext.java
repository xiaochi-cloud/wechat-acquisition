package com.wechat.acquisition.domain.common.tenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 多租户上下文
 * 
 * 职责：管理当前请求的租户信息
 */
public class TenantContext {
    private static final Logger log = LoggerFactory.getLogger(TenantContext.class);
    
    private static final ThreadLocal<String> TENANT_ID_HOLDER = new ThreadLocal<>();
    
    /**
     * 设置当前租户 ID
     */
    public static void setTenantId(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("租户 ID 不能为空");
        }
        TENANT_ID_HOLDER.set(tenantId);
        log.debug("设置租户 ID: {}", tenantId);
    }
    
    /**
     * 获取当前租户 ID
     */
    public static String getTenantId() {
        String tenantId = TENANT_ID_HOLDER.get();
        if (tenantId == null) {
            throw new IllegalStateException("未设置租户 ID");
        }
        return tenantId;
    }
    
    /**
     * 清除当前租户 ID
     */
    public static void clear() {
        TENANT_ID_HOLDER.remove();
        log.debug("清除租户 ID");
    }
    
    /**
     * 检查是否为系统租户
     */
    public static boolean isSystemTenant() {
        String tenantId = TENANT_ID_HOLDER.get();
        return "system".equals(tenantId);
    }
}
