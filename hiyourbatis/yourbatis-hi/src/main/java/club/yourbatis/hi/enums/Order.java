package club.yourbatis.hi.enums;

public enum Order {
    ASC("asc"),DESC("desc");
    private String value;
    Order(String value){this.value = value;}
    public String getValue() {
        return value;
    }
}
