package canvasolutions.kreemcabs.drivers.model;


public class TransactionPojo {
    private int id;
    private String amount;
    private String date;
    private String id_user;
    private String cat_user;
    private String type;
    private String image;
    private String time;

    public TransactionPojo(int id, String amount, String date, String id_user, String cat_user, String type, String image, String time) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.id_user = id_user;
        this.cat_user = cat_user;
        this.type = type;
        this.image = image;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getCat_user() {
        return cat_user;
    }

    public void setCat_user(String cat_user) {
        this.cat_user = cat_user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}