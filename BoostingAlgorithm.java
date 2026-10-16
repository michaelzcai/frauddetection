import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class BoostingAlgorithm {
    private Clustering clustering;
    private int[][] input; // reduced input (nxk)
    private double[] weights;
    private int[] labels;
    private Queue<WeakLearner> weakLearners = new Queue<WeakLearner>(); // generated weak learners

    // create the clusters and initialize your data structures
    public BoostingAlgorithm(int[][] input, int[] labels, Point2D[] locations, int k) {
        // corner cases
        if (input == null || labels == null || locations == null)
            throw new IllegalArgumentException("cannot be called with null argument");
        if (input.length == 0)
            throw new IllegalArgumentException("input length must be >0");
        if (k < 1 || k > input.length)
            throw new IllegalArgumentException("invalid number of clusters");
        if (labels.length != input.length || locations.length != input[0].length)
            throw new IllegalArgumentException("argument lengths must be equal");

        // initialize instance variables
        clustering = new Clustering(locations, k);
        this.input = new int[input.length][];
        weights = new double[input.length];
        this.labels = labels;
        for (int i = 0; i < input.length; i++) {
            this.input[i] = clustering.reduceDimensions(input[i]);
            weights[i] = 1.0 / input.length;
        }
    }

    // return the current weight of the ith point
    public double weightOf(int i) {
        return weights[i];
    }

    // apply one step of the boosting algorithm
    public void iterate() {
        // create new weak learner and add it to the queue
        WeakLearner newWL = new WeakLearner(input, weights, labels);
        weakLearners.enqueue(newWL);

        // adjust weights
        double sumWeights = 0;
        for (int i = 0; i < input.length; i++) {
            if (newWL.predict(input[i]) != labels[i]) weights[i] *= 2;
            sumWeights += weights[i];
        }
        for (int i = 0; i < weights.length; i++) {
            weights[i] /= sumWeights;
        }
    }

    // return the prediction of the learner for a new sample
    public int predict(int[] sample) {
        int numOnes = 0;
        sample = clustering.reduceDimensions(sample);
        for (WeakLearner WL : weakLearners) numOnes += WL.predict(sample);
        if (2 * numOnes > weakLearners.size()) return 1;
        else return 0;
    }

    // unit testing
    public static void main(String[] args) {
        Stopwatch timer = new Stopwatch();
        // read in the terms from a file
        DataSet training = new DataSet(args[0]);
        DataSet testing = new DataSet(args[1]);
        int k = Integer.parseInt(args[2]);
        int numIterations = Integer.parseInt(args[3]);

        int[][] trainingInput = training.getInput();
        int[][] testingInput = testing.getInput();
        int[] trainingLabels = training.getLabels();
        int[] testingLabels = testing.getLabels();
        Point2D[] trainingLocations = training.getLocations();

        // train the model
        BoostingAlgorithm model = new BoostingAlgorithm(
                trainingInput, trainingLabels, trainingLocations, k
        );
        for (int t = 0; t < numIterations; t++)
            model.iterate();

        // calculate the training data set accuracy
        double trainingAccuracy = 0;
        for (int i = 0; i < training.getN(); i++)
            if (model.predict(trainingInput[i]) == trainingLabels[i])
                trainingAccuracy += 1;
        trainingAccuracy /= training.getN();

        // calculate the test data set accuracy
        double testingAccuracy = 0;
        for (int i = 0; i < testing.getN(); i++)
            if (model.predict(testingInput[i]) == testingLabels[i])
                testingAccuracy += 1;
        testingAccuracy /= testing.getN();

        StdOut.println("Training accuracy of model: " + trainingAccuracy);
        StdOut.println("Test accuracy of model: " + testingAccuracy);
        StdOut.println("Time elapsed: " + timer.elapsedTime());
    }
}
