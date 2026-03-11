package method;

public class Product {
	private String name;
	private String price;
	
    // Proper constructor to set fields
	public Product(String productname, String price2) {
		this.name = productname;
		this.price = price2;
	}

	// remove invalid no-arg initializer method; keep getters
	public String getName() {
		return name;
	}
	public String getPrice() {
		return price;
	}

}