package online.abot.alertbot.domian;

import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

public class QqBinding implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;

    @NotEmpty
    private String qqId;

    @NotEmpty
    private String accountId;

    private int isEnabled;



    public QqBinding(@NotEmpty String qqId, @NotEmpty String accountId) {
        this.qqId = qqId;
        this.accountId = accountId;
        this.isEnabled = 1;
    }

    public QqBinding(@NotEmpty String qqId, @NotEmpty String accountId, int isEnabled) {
        this.qqId = qqId;
        this.accountId = accountId;
        this.isEnabled = isEnabled;
    }

    public QqBinding(int id, @NotEmpty String qqId, @NotEmpty String accountId, boolean isEnabled) {
        this.id = id;
        this.qqId = qqId;
        this.accountId = accountId;
        this.isEnabled = isEnabled?1:0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQqId() {
        return qqId;
    }

    public void setQqId(String qqId) {
        this.qqId = qqId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(int isEnabled) {
        this.isEnabled = isEnabled;
    }
}
