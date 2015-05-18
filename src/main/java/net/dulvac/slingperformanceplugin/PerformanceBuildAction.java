package net.dulvac.slingperformanceplugin;

import org.kohsuke.stapler.StaplerProxy;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import net.dulvac.slingperformanceplugin.parsers.GenericReportParser;
import hudson.util.StreamTaskListener;

public class PerformanceBuildAction implements Action, StaplerProxy {

    private final AbstractBuild<?, ?> build;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    private int Id = 0;

    /**
     * Configured parsers used to parse reports in this build. For compatibility reasons, this can be null.
     */
    private final List<GenericReportParser> parsers;

    private transient final PrintStream hudsonConsoleWriter;

    private transient WeakReference<SlingPerformanceReportMap> performanceReportMap;

    private static final Logger logger = Logger.getLogger(PerformanceBuildAction.class.getName());

    public PerformanceBuildAction(AbstractBuild<?, ?> pBuild, PrintStream logger, List<GenericReportParser> parsers) {
        build = pBuild;
        hudsonConsoleWriter = logger;
        this.parsers = parsers;
    }

    public GenericReportParser getParserByDisplayName(String displayName) {
        if (parsers != null) {
            for (GenericReportParser parser : parsers) {
                if (parser.getDescriptor().getDisplayName().equals(displayName)) { return parser; }
            }
        }
        return null;
    }

    public String getDisplayName() {
        return Messages.BuildAction_DisplayName();
    }

    public String getIconFileName() {
        return "graph.gif";
    }

    public String getUrlName() {
        return "performance";
    }

    public SlingPerformanceReportMap getTarget() {
        return getPerformanceReportMap();
    }

    public AbstractBuild<?, ?> getBuild() {
        return build;
    }

    public PrintStream getHudsonConsoleWriter() {
        return hudsonConsoleWriter;
    }

    public SlingPerformanceReportMap getPerformanceReportMap() {
        SlingPerformanceReportMap reportMap = null;
        WeakReference<SlingPerformanceReportMap> wr = this.performanceReportMap;
        if (wr != null) {
            reportMap = wr.get();
            if (reportMap != null) { return reportMap; }
        }

        try {
            reportMap = new SlingPerformanceReportMap(this, new StreamTaskListener(System.err), Id);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error creating new PerformanceReportMap()", e);
        }
        this.performanceReportMap = new WeakReference<SlingPerformanceReportMap>(
                reportMap);
        return reportMap;
    }

    public void setPerformanceReportMap(
            WeakReference<SlingPerformanceReportMap> performanceReportMap) {
        this.performanceReportMap = performanceReportMap;
    }
}
