package edu.stanford.futuredata.macrobase.cli;

import edu.stanford.futuredata.pipeline.BasicBatchPipeline;
import edu.stanford.futuredata.pipeline.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Runs a pipeline based on parameters saved in a config file.
 * New custom pipelines can be created by implementing the Pipeline interface and
 * adding a loader to the PipelineMapper.
 *
 * see demo/conf.yaml
 */
public class CliRunner {
    public static class CliConfig {
        private Map<String, Object> values;

        public CliConfig(Map<String, Object> values) {
            this.values = values;
        }

        public static CliConfig loadFromYaml(String fileName) throws Exception {
            BufferedReader r = new BufferedReader(new FileReader(fileName));
            Yaml yaml = new Yaml();
            Map<String, Object> conf = (Map<String, Object>) yaml.load(r);
            return new CliConfig(conf);
        }

        @SuppressWarnings("unchecked")
        public <T> T getAs(String key) {
            return (T)values.get(key);
        }

        public Object get(String key) {
            return values.get(key);
        }

        public Map<String, Object> getValues() {
            return values;
        }

        public BasicBatchPipeline.BatchQuery getBatchQuery() {
            return new BasicBatchPipeline.BatchQuery()
                    .metrics(Arrays.asList((String)getAs("metric")))
                    .inputURI("csv://" + getAs("inputFile"))
                    .cutoff(getAs("percentile"))
                    .pctileHigh(getAs("includeHi"))
                    .pctileLow(getAs("includeLo"))
                    .attributes(getAs("attributes"))
                    .minRiskRatio(getAs("minRiskRatio"))
                    .minSupport(getAs("minSupport"));
        }
    }

    private static Logger log = LoggerFactory.getLogger(CliRunner.class);

    public static void main(String[] args) throws Exception {
        String configFile = args[0];
        CliConfig conf = CliConfig.loadFromYaml(configFile);
        Pipeline p = new BasicBatchPipeline(conf.getBatchQuery());
        log.info("results: {}", p.results());
    }
}
