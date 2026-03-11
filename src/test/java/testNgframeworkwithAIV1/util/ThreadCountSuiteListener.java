package testNgframeworkwithAIV1.util;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.xml.XmlSuite;

/**
 * Suite listener that sets TestNG XmlSuite thread-count at runtime using ConfigReader/System property.
 * This allows passing -DthreadCount=8 to control TestNG parallel threads.
 */
public class ThreadCountSuiteListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        try {
            int threads = new ConfigReader().getThreadCount();
            XmlSuite xml = suite.getXmlSuite();
            if (xml != null && threads > 0) {
                xml.setThreadCount(threads);
            }
        } catch (Exception ignored) {
            // keep default if any error occurs
        }
    }

    @Override
    public void onFinish(ISuite suite) {
        // no-op
    }
}
