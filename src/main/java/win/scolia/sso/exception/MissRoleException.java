package win.scolia.sso.exception;

/**
 * 无此角色
 */
public class MissRoleException extends MissException {
    private static final long serialVersionUID = 836403182055684615L;

    public MissRoleException() {
    }

    public MissRoleException(String message) {
        super(message);
    }

    public MissRoleException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissRoleException(Throwable cause) {
        super(cause);
    }
}
