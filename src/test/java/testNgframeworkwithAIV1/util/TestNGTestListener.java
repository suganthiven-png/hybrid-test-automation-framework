package testNgframeworkwithAIV1.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;

import testNgframeworkwithAIV1.util.ExtentManager;
import testNgframeworkwithAIV1.util.ScreenshotUtil;

public class TestNGTestListener implements ITestListener {
    private String timestamp() { return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); }

    private static ExtentReports extent = ExtentManager.getExtentReports();
    private static Map<String, ExtentTest> tests = new ConcurrentHashMap<>();

    private String pageSourceRelativeLink(String absolutePageSourcePath) {
        if (absolutePageSourcePath == null) return null;
        try {
            Path p = Paths.get(absolutePageSourcePath);
            String fileName = p.getFileName().toString();
            // Extent report is generated under test-output/extent-report. Pagesources are under test-output/pagesources.
            // Relative path from extent-report folder to pagesources is ../pagesources/<fileName>
            return "../pagesources/" + fileName;
        } catch (Exception e) {
            return absolutePageSourcePath;
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        String name = result.getMethod().getMethodName();
        String cls = result.getTestClass().getName();
        System.out.println("[TEST START] " + timestamp() + " - " + cls + "#" + name);
        ExtentTest test = extent.createTest(cls + "#" + name);
        tests.put(getKey(result), test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String name = result.getMethod().getMethodName();
        String cls = result.getTestClass().getName();
        System.out.println("[TEST PASS] " + timestamp() + " - " + cls + "#" + name);
        ExtentTest test = tests.get(getKey(result));
        if (test != null) {
            boolean captureOnSuccess = Boolean.parseBoolean(System.getProperty("screenshotOnSuccess", "false"));
            if (captureOnSuccess) {
                String path = ScreenshotUtil.consumeLastScreenshot();
                if (path == null) path = ScreenshotUtil.takeScreenshot(name + "_SUCCESS");
                if (path != null) {
                    try { test.pass("Screenshot on success", MediaEntityBuilder.createScreenCaptureFromPath(path).build()); } catch (Exception e) { test.pass("Screenshot saved: " + path); }
                }
                String ps = ScreenshotUtil.consumeLastPageSource();
                if (ps != null) {
                    String rel = pageSourceRelativeLink(ps);
                    test.info("Page source: <a href='" + rel + "' target='_blank'>view</a>");
                }
            }
            test.log(Status.PASS, "Test passed");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String name = result.getMethod().getMethodName();
        String cls = result.getTestClass().getName();
        System.err.println("[TEST FAIL] " + timestamp() + " - " + cls + "#" + name);
        Throwable t = result.getThrowable(); if (t != null) t.printStackTrace();
        ExtentTest test = tests.get(getKey(result));
        if (test != null) {
            String path = ScreenshotUtil.consumeLastScreenshot();
            if (path == null) path = ScreenshotUtil.takeScreenshot(name + "_FAIL");
            if (path != null) {
                try { test.fail(t, MediaEntityBuilder.createScreenCaptureFromPath(path).build()); } catch (Exception e) { test.fail(t); test.info("Screenshot saved at: " + path); }
            } else { test.fail(t); }
            String ps = ScreenshotUtil.consumeLastPageSource();
            if (ps != null) {
                String rel = pageSourceRelativeLink(ps);
                test.info("Page source: <a href='" + rel + "' target='_blank'>view</a>");
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String name = result.getMethod().getMethodName();
        String cls = result.getTestClass().getName();
        System.out.println("[TEST SKIP] " + timestamp() + " - " + cls + "#" + name);
        ExtentTest test = tests.get(getKey(result)); if (test != null) test.log(Status.SKIP, "Test skipped");
    }

    @Override public void onTestFailedButWithinSuccessPercentage(ITestResult result) { }
    @Override public void onStart(ITestContext context) { }
    @Override public void onFinish(ITestContext context) { extent.flush(); }

    private String getKey(ITestResult result) {
        return result.getTestClass().getName() + "#" + result.getMethod().getMethodName() + "@" + result.getMethod().getCurrentInvocationCount();
    }
}