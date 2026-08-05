package marketplace;
/*
 */
import java.util.Objects;

public class TraderItem {

    private int itemId;
    private String itemName;
    private String itemType;
    private String description;
    private int price;
    private int stockQuantity;

    public TraderItem(
            int itemId,
            String itemName,
            String itemType,
            String description,
            int price,
            int stockQuantity
    ) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemType = itemType;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public TraderItem(
            String itemName,
            String itemType,
            String description,
            int price,
            int stockQuantity
    ) {
        this(0, itemName, itemType, description, price, stockQuantity);
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @Override
    public String toString() {
        return itemName;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof TraderItem other)) {
            return false;
        }
        return itemId == other.itemId
                && price == other.price
                && stockQuantity == other.stockQuantity
                && Objects.equals(itemName, other.itemName)
                && Objects.equals(itemType, other.itemType)
                && Objects.equals(description, other.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                itemId,
                itemName,
                itemType,
                description,
                price,
                stockQuantity
        );
    }
}

