## Command Line Interface to macrobase

This module supports creating standard pipelines 
which can then be called from the command line with
configuration parameters set in yaml files.

Pipelines consist of operators from macrobase-lib
hooked together with possible pre/post processing.

BatchPipeline is the default pipeline.

To run a simple pipeline with a percentile classifier
& itemset mining explanation:

From the project root directory:
```
cd core
mvn clean; mvn install

cd ../cli
mvn clean; mvn package

cd ..
bin/cli.sh cli/demo/conf.yaml
```

Logging settings are stored in `cli/config/logback.xml`