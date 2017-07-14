package edu.stanford.futuredata.pipeline;

import edu.stanford.futuredata.macrobase.analysis.classify.PercentileClassifier;
import edu.stanford.futuredata.macrobase.analysis.summary.APrioriSummarizer;
import edu.stanford.futuredata.macrobase.analysis.summary.Explanation;
import edu.stanford.futuredata.macrobase.datamodel.DataFrame;
import edu.stanford.futuredata.macrobase.datamodel.Schema;
import edu.stanford.futuredata.macrobase.ingest.CSVDataFrameLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simplest default pipeline: load, classify, and then explain
 */
public class BasicBatchPipeline implements Pipeline {
    Logger log = LoggerFactory.getLogger(Pipeline.class);

    public enum ClassifierType { PERCENTILE };

    public static class BatchQuery {
        private ClassifierType type;
        private List<String> metrics;
        private double cutoff;
        private List<String> attributes;
        private double minSupport;
        private double minRiskRatio;
        private String inputURI;

        private boolean pctileHigh;
        private boolean pctileLow;

        public BatchQuery pctileHigh(boolean pctileHigh) {
            this.pctileHigh = pctileHigh;
            return this;
        }

        public BatchQuery pctileLow(boolean pctileLow) {
            this.pctileLow = pctileLow;
            return this;
        }

        public BatchQuery type(ClassifierType type) {
            this.type = type;
            return this;
        }

        public BatchQuery metrics(List<String> metrics) {
            this.metrics = metrics;
            return this;
        }

        public BatchQuery attributes(List<String> attributes) {
            this.attributes = attributes;
            return this;
        }

        public BatchQuery minSupport(double minSupport) {
            this.minSupport = minSupport;
            return this;
        }

        public BatchQuery minRiskRatio(double minRiskRatio) {
            this.minRiskRatio = minRiskRatio;
            return this;
        }

        public BatchQuery inputURI(String inputURI) {
            this.inputURI = inputURI;
            return this;
        }

        public BatchQuery cutoff(double cutoff) {
            this.cutoff = cutoff;
            return this;
        }
    }

    private final BatchQuery q;

    public BasicBatchPipeline(BatchQuery query) {
        this.q = query;
    }

    @Override
    public Explanation results() throws Exception {
        log.error("{}", q.metrics);
        // for now, only support percentile classification over single metric
        assert(q.metrics != null &&
               q.metrics.size() == 1);
        String metric = q.metrics.get(0);

        Map<String, Schema.ColType> colTypes = new HashMap<>();
        colTypes.put(metric, Schema.ColType.DOUBLE);

        assert(q.inputURI.substring(0, 3).equals("csv"));
        CSVDataFrameLoader loader = new CSVDataFrameLoader(q.inputURI.substring(6));
        loader.setColumnTypes(colTypes);

        long startTime = System.currentTimeMillis();
        DataFrame df = loader.load();
        long elapsed = System.currentTimeMillis() - startTime;

        log.info("Loading time: {}", elapsed);
        log.info("{} rows", df.getNumRows());
        log.info("Metric: {}", q.metrics.get(0));
        log.info("Attributes: {}", q.attributes);

        assert (q.type == ClassifierType.PERCENTILE);
        PercentileClassifier classifier = new PercentileClassifier(metric);
        classifier.setPercentile(q.cutoff);
        classifier.setIncludeHigh(q.pctileHigh);
        classifier.setIncludeLow(q.pctileLow);
        classifier.process(df);
        df = classifier.getResults();
        log.info("Outlier cutoffs: {} {}",
                 classifier.getLowCutoff(),
                 classifier.getHighCutoff()
                );

        APrioriSummarizer summarizer = new APrioriSummarizer();
        summarizer.setOutlierColumn(classifier.getOutputColumnName());
        summarizer.setAttributes(q.attributes);
        summarizer.setMinSupport(q.minSupport);
        summarizer.setMinRiskRatio(q.minRiskRatio);

        startTime = System.currentTimeMillis();
        summarizer.process(df);
        elapsed = System.currentTimeMillis() - startTime;
        log.info("Summarization time: {}", elapsed);
        Explanation output = summarizer.getResults();

        return output;
    }
}
