package method;

// Thin compatibility wrapper so file can remain under 'method' folder without causing package mismatch.
@Deprecated
public class Product extends model.Product {
    public Product(String productname, String price2) {
        super(productname, price2);
    }
}
