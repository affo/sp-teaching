package my.flink.examples.fault_tolerance;

/**
 * Created by affo on 17/08/17.
 */
public class K {
    public static final String LOCALHOST = "192.168.99.100";

    public static final String KAFKA_HOST = LOCALHOST;
    public static final String INFLUXDB_HOST = LOCALHOST;

    /*
    // configuration to using with Docker
    public static final String KAFKA_HOST = "kafka";
    public static final String INFLUXDB_HOST = "influx";
    */

    public static final String INFLUXDB_DBNAME = "flink";
}
