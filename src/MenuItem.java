public class MenuItem {
    private int itemID;
    private String itemName;
    private double price;
    private String category;

    public MenuItem(int itemID, String itemName, double price, String category) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.price = price;
        this.category = category;
    }

    public int getItemID() { return itemID; }
    public String getItemName() { return itemName; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
}