import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCountWithStopwords {

    // Mapper Class
    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();
        private HashSet<String> stopwords = new HashSet<>();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            // Load stopwords from HDFS
            Configuration conf = context.getConfiguration();
            FileSystem fs = FileSystem.get(conf);
            Path stopwordsPath = new Path("/input/stopwords.txt"); // Path to stopwords file in HDFS
            BufferedReader reader = new BufferedReader(new InputStreamReader(fs.open(stopwordsPath)));
            String line;
            while ((line = reader.readLine()) != null) {
                stopwords.add(line.trim().toLowerCase()); // Add stopwords to the HashSet
            }
            reader.close();
            System.out.println("Stopwords loaded: " + stopwords); // Debug statement
        }

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreTokens()) {
                String token = itr.nextToken().toLowerCase().replaceAll("[^a-zA-Z]", ""); // Remove punctuation and convert to lowercase
                System.out.println("Processing word: " + token); // Debug statement
                if (!stopwords.contains(token)) { // Ignore stopwords
                    word.set(token);
                    context.write(word, one); // Emit (word, 1)
                    System.out.println("Emitted word: " + token); // Debug statement
                } else {
                    System.out.println("Filtered out stopword: " + token); // Debug statement
                }
            }
        }
    }

    // Reducer Class
    public static class IntSumReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get(); // Sum up the counts for each word
            }
            result.set(sum);
            context.write(key, result); // Emit (word, count)
        }
    }

    // Main Method
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count with stopwords");
        job.setJarByClass(WordCountWithStopwords.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // Input and output paths
        FileInputFormat.addInputPath(job, new Path(args[0])); // Input directory
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // Output directory

        // Exit with success or failure
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
