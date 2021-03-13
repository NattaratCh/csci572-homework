import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by nuning on 3/12/21.
 */
public class InvertedIndexJob {
    public static class WordCountMapper extends Mapper<Object, Text, Text, Text> {
        private Text document = new Text();
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] valueStr = value.toString().trim().split("\\s+", 2);
            String documentId = valueStr[0];
            String cleanValue = valueStr[1].replaceAll("[^a-zA-Z]", " ").toLowerCase();
            StringTokenizer itr = new StringTokenizer(cleanValue);
            while (itr.hasMoreTokens()) {
                String v = itr.nextToken().trim();
                if (!v.equals("")) {
                    word.set(v);
                    document.set(documentId);
                    context.write(word, document);
                }
            }
        }
    }

    public static class WordCountReducer extends Reducer<Text, Text, Text, Text> {
        private Text result = new Text();

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Map<String, Integer> map = new HashMap();
            for (Text val : values) {
                String documentId = val.toString();
                int count = 1;
                if (map.containsKey(documentId)) {
                    count = map.get(documentId) + 1;
                }
                map.put(documentId, count);
            }


            StringBuilder sb = new StringBuilder();
            for(String docId: map.keySet()) {
                sb.append(docId + ":" + map.get(docId) + "\t");
            }
            result.set(sb.toString());
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(InvertedIndexJob.class);
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
