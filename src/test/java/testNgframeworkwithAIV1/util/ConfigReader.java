package testNgframeworkwithAIV1.util;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
	private Properties properties = new Properties();

	public ConfigReader() {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
			if (is != null) {
				properties.load(is);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to load config.properties", e);
		}
	}

	public String getBaseUrl() {
		String sys = System.getProperty("baseUrl");
		if (sys != null && !sys.isEmpty())
			return sys;
		return properties.getProperty("baseUrl", "https://www.guru99.com/");
	}

	public String getBrowser() {
		String sys = System.getProperty("browser");
		if (sys != null && !sys.isEmpty())
			return sys;
		return properties.getProperty("browser", "chrome");
	}

	public boolean isHeadless() {
		String sys = System.getProperty("headless");
		if (sys != null && !sys.isEmpty())
			return Boolean.parseBoolean(sys);
		return Boolean.parseBoolean(properties.getProperty("headless", "false"));
	}

	public int getImplicitWait() {
		String sys = System.getProperty("implicitWait");
		if (sys != null && !sys.isEmpty()) {
			try {
				return Integer.parseInt(sys);
			} catch (NumberFormatException ignored) {
			}
		}
		return Integer.parseInt(properties.getProperty("implicitWait", "10"));
	}
}


