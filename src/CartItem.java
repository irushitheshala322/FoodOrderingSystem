public class CartItem {
    private MenuItem menuItem;
    private int quantity;

    public CartItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }
    public void incrementQuantity() { this.quantity++; }

    public double getTotalPrice() {
        return menuItem.getPrice() * quantity;
    }
}