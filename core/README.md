## MacroBase core pipelines and REST Server

This module supports creating standard pipelines 
which can then be called from a JSON-based REST API
configuration parameters set in yaml files.

Pipelines consist of operators from macrobase-lib
hooked together with possible pre/post processing.

DefaultBatchPipeline is the default pipeline.

To run a simple pipeline from the REST server
using a percentile classifier & itemset mining explanation:

From the project root directory:
```
cd lib
mvn clean; mvn install

cd ../server
mvn clean; mvn package

cd ..;
bin/server.sh &
sleep 5;
server/demo/query.sh
```

Logging settings are stored in `config/logback.xml`