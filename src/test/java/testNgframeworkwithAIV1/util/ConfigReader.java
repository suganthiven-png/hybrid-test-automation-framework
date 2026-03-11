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

	private String trimIfNotNull(String s) {
		return s == null ? null : s.trim();
	}

	public String getBaseUrl() {
		String sys = trimIfNotNull(System.getProperty("baseUrl"));
		if (sys != null && !sys.isEmpty())
			return sys;
		return trimIfNotNull(properties.getProperty("baseUrl", "https://www.guru99.com/"));
	}

	public String getBrowser() {
		String sys = trimIfNotNull(System.getProperty("browser"));
		if (sys != null && !sys.isEmpty())
			return sys;
		return trimIfNotNull(properties.getProperty("browser", "chrome"));
	}

	public boolean isHeadless() {
		String sys = trimIfNotNull(System.getProperty("headless"));
		if (sys != null && !sys.isEmpty())
			return Boolean.parseBoolean(sys);
		return Boolean.parseBoolean(trimIfNotNull(properties.getProperty("headless", "false")));
	}

	public int getImplicitWait() {
		String sys = trimIfNotNull(System.getProperty("implicitWait"));
		if (sys != null && !sys.isEmpty()) {
			try {
				return Integer.parseInt(sys);
			} catch (NumberFormatException ignored) {
			}
		}
		return Integer.parseInt(trimIfNotNull(properties.getProperty("implicitWait", "10")));
	}

	public int getThreadCount() {
		String sys = trimIfNotNull(System.getProperty("threadCount"));
		if (sys != null && !sys.isEmpty()) {
			try {
				return Integer.parseInt(sys);
			} catch (NumberFormatException ignored) {
			}
		}
		return Integer.parseInt(trimIfNotNull(properties.getProperty("threadCount", "4")));
	}

	public String getRemoteUrl() {
		String sys = trimIfNotNull(System.getProperty("selenium.remote.url"));
		if (sys != null && !sys.isEmpty())
			return sys;
		return trimIfNotNull(properties.getProperty("selenium.remote.url", ""));
	}
}