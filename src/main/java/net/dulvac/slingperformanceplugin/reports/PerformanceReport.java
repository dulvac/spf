package net.dulvac.slingperformanceplugin.reports;

import hudson.model.AbstractBuild;
import net.dulvac.slingperformanceplugin.PerformanceBuildAction;
import net.dulvac.slingperformanceplugin.SlingPerformanceReportMap;
import net.dulvac.slingperformanceplugin.SlingReportSample;
import net.dulvac.slingperformanceplugin.Messages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a single performance report, which consists of multiple {@link TestRunReport}s for different URLs that was
 * tested.
 *
 * This object belongs under {@link SlingPerformanceReportMap}.
 */
public class PerformanceReport extends AbstractReport implements
        Comparable<PerformanceReport> {

    private PerformanceBuildAction buildAction;
    private SlingReportSample reportSample;
    private List<SlingReportSample> reportSamples = new ArrayList<SlingReportSample>();
    private String reportFileName;
    private String reportName;
    private boolean multipleSampleReport;

    /**
     * {@link TestRunReport}s keyed by their {@link TestRunReport#getStaplerUri()}.
     */
    private final Map<String, TestRunReport> testReportMap = new LinkedHashMap<String, TestRunReport>();
    private PerformanceReport lastBuildReport;

    public void addSample(SlingReportSample pReportSample) {
        String testName = pReportSample.getTestName();
        if (testName == null) {
            return;
        }

        TestRunReport uriReport = testReportMap.get(testName);

        if (uriReport == null) {
            uriReport = new TestRunReport(this);
            testReportMap.put(testName, uriReport);
        }
        uriReport.addReportSample(pReportSample);
        reportSamples.add(pReportSample);
    }

    public int compareTo(PerformanceReport jmReport) {
        if (this == jmReport) {
            return 0;
        }
        return getReportName().compareTo(jmReport.getReportName());
    }

    public long getMedian() {
        return this.reportSample.getMedian();
    }

    public AbstractBuild<?, ?> getBuild() {
        return buildAction.getBuild();
    }

    PerformanceBuildAction getBuildAction() {
        return buildAction;
    }

    public String getDisplayName() {
        return Messages.Report_DisplayName();
    }

    public TestRunReport getDynamic(String token) throws IOException {
        return getUriReportMap().get(token);
    }

    public SlingReportSample getReportSample() {
        return reportSample;
    }

    public List<SlingReportSample> getReportSamples() {
        return reportSamples;
    }

    public long getMax() {
        return this.reportSample.getMax();
    }

    public long getMin() {
        return this.reportSample.getMin();
    }

    public long get10Percentile() {
        return this.reportSample.get10Percentile();
    }

    public long get90Percentile() {
        return this.reportSample.get90Percentile();
    }

    public String getReportFileName() {
        return reportFileName;
    }

    public List<TestRunReport> getUriListOrdered() {
        Collection<TestRunReport> uriCollection = getUriReportMap().values();
        List<TestRunReport> UriReportList = new ArrayList<TestRunReport>(uriCollection);
        return UriReportList;
    }

    public Map<String, TestRunReport> getUriReportMap() {
        return testReportMap;
    }

    public void setBuildAction(PerformanceBuildAction buildAction) {
        this.buildAction = buildAction;
    }

    public void setReportSample(SlingReportSample reportSample) {
        this.reportSample = reportSample;
    }

    public void setReportFileName(String reportFileName) {
        this.reportFileName = reportFileName;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public boolean isMultipleSampleReport() {
        return multipleSampleReport;
    }

    public void setMultipleSampleReport(boolean multipleSampleReport) {
        this.multipleSampleReport = multipleSampleReport;
    }

  /*public void setLastBuildReport( PerformanceReport lastBuildReport ) {
    Map<String, TestRunReport> lastBuildUriReportMap = lastBuildReport.getUriReportMap();
    for (Map.Entry<String, TestRunReport> item : testReportMap.entrySet()) {
        TestRunReport lastBuildUri = lastBuildUriReportMap.get( item.getKey() );
        if ( lastBuildUri != null ) {
            item.getValue().addLastBuildUriReport( lastBuildUri );
        } else {
        }
    }
    this.lastBuildReport = lastBuildReport;
  }
  */


    public long getMedianDiff() {
        if (lastBuildReport == null) {
            return 0;
        }
        return getMedian() - lastBuildReport.getMedian();
    }


    public String getLastBuildHttpCodeIfChanged() {
        return "";
    }

}
