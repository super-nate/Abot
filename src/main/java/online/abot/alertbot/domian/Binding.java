package online.abot.alertbot.domian;

import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

public class Binding implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;

    @NotEmpty
    private String imId;

    @NotEmpty
    private String accountId;

    private int isEnabled;



    public Binding(@NotEmpty String imId, @NotEmpty String accountId) {
        this.imId = imId;
        this.accountId = accountId;
        this.isEnabled = 1;
    }

    public Binding(@NotEmpty String imId, @NotEmpty String accountId, int isEnabled) {
        this.imId = imId;
        this.accountId = accountId;
        this.isEnabled = isEnabled;
    }

    public Binding(int id, @NotEmpty String imId, @NotEmpty String accountId, boolean isEnabled) {
        this.id = id;
        this.imId = imId;
        this.accountId = accountId;
        this.isEnabled = isEnabled?1:0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImId() {
        return imId;
    }

    public void setImId(String imId) {
        this.imId = imId;
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
