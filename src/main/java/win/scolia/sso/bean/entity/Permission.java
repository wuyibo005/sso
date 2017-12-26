package win.scolia.sso.bean.entity;

import java.util.Date;

public class Permission {

    private Long permissionId;

    private String permission;

    private Date createTime;

    private Date lastModified;

    public Permission() {
    }

    public Permission(String permission) {
        this(permission, new Date(), new Date());
    }

    public Permission(String permission, Date lastModified) {
        this.permission = permission;
        this.lastModified = lastModified;
    }

    public Permission(String permission, Date createTime, Date lastModified) {
        this.permission = permission;
        this.createTime = createTime;
        this.lastModified = lastModified;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "permissionId=" + permissionId +
                ", permission='" + permission + '\'' +
                ", createTime=" + createTime +
                ", lastModified=" + lastModified +
                '}';
    }
}
