package my.flink.examples.transformations;

import my.flink.examples.common.FirstFieldTimestampExtractor;
import my.flink.examples.common.IntegersGenerator;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * Created by affo on 22/11/17.
 */
public class Join {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        IntegersGenerator generator = new IntegersGenerator(42);

        DataStreamSource<Tuple2<Integer, Integer>> s1 = env.fromElements(
                generator.next(2),
                generator.next(2),
                generator.next(7),
                generator.next(9),
                generator.next(4),
                generator.next(1));
        generator.clear();
        DataStreamSource<Tuple2<Integer, Integer>> s2 = env.fromElements(
                generator.next(1),
                generator.next(2),
                generator.next(4),
                generator.next(9),
                generator.next(3),
                generator.next(1));

        DataStream<Tuple2<Integer, Integer>> ts1 = s1.assignTimestampsAndWatermarks(new FirstFieldTimestampExtractor<>());
        DataStream<Tuple2<Integer, Integer>> ts2 = s2.assignTimestampsAndWatermarks(new FirstFieldTimestampExtractor<>());

        ts1.join(ts2)
                .where(new ValueKeySelector())
                .equalTo(new ValueKeySelector())
                .window(TumblingEventTimeWindows.of(Time.milliseconds(3)))
                .apply(
                        new JoinFunction<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>, Integer>() {
                            @Override
                            public Integer join(Tuple2<Integer, Integer> t1, Tuple2<Integer, Integer> t2) throws Exception {
                                return t1.f1 + t2.f1;
                            }
                        }
                )
                .print();

        env.execute();
    }

    private static class ValueKeySelector implements KeySelector<Tuple2<Integer, Integer>, Integer> {

        @Override
        public Integer getKey(Tuple2<Integer, Integer> tuple) throws Exception {
            return tuple.f1;
        }
    }
}
