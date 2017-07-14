package edu.stanford.futuredata.macrobase.rest;

import edu.stanford.futuredata.pipeline.BasicBatchPipeline;

import java.util.List;

public class RestQuery {
    private class ClassifierRequest {
        private String type;
        private List<String> metrics;
        private double cutoff;
    }

    private class SummarizerRequest {
        private List<String> attributes;
        private double minSupport;
        private double minRiskRatio;
    }

    private String inputURI;
    private ClassifierRequest classifier;
    private SummarizerRequest summarizer;

    private boolean pctileHigh = true;
    private boolean pctileLow = false;

    public BasicBatchPipeline.BatchQuery getBatchQuery() {
        return new BasicBatchPipeline.BatchQuery()
                .inputURI(inputURI)
                .metrics(classifier.metrics)
                .cutoff(classifier.cutoff)
                .type(BasicBatchPipeline
                              .ClassifierType
                              .valueOf(classifier.type.toUpperCase()))
                .attributes(summarizer.attributes)
                .minSupport(summarizer.minSupport)
                .minRiskRatio(summarizer.minRiskRatio)
                .pctileHigh(pctileHigh)
                .pctileLow(pctileLow);
    }
}
