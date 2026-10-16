import java.util.Arrays;
import java.util.Comparator;

public class WeakLearner {
    private int dP, vP, sP, k; // k is dimension of input

    // train the weak learner
    public WeakLearner(int[][] input, double[] weights, int[] labels) {
        // corner cases
        if (input == null || weights == null || labels == null)
            throw new IllegalArgumentException("cannot be called with null argument");
        if (input.length == 0)
            throw new IllegalArgumentException("input length must be >0");
        for (int[] summary : input)
            if (summary.length == 0)
                throw new IllegalArgumentException("summary lengths must be >0");
        if (weights.length != input.length || labels.length != input.length)
            throw new IllegalArgumentException("argument lengths must be equal");

        // get the total weight of all points labeled 0, 1
        double maxWeight = 0, totalZeroWeight = 0, totalOneWeight = 0;
        for (int i = 0; i < weights.length; i++) {
            if (weights[i] < 0)
                throw new IllegalArgumentException("weights must be nonnegative");

            if (labels[i] == 0) totalZeroWeight += weights[i];
            else if (labels[i] == 1) totalOneWeight += weights[i];
            else throw new IllegalArgumentException("labels must be 0 or 1");
        }

        // find the best stump for each dimension
        for (int d = 0; d < input[0].length; d++) {
            // array containing d-coordinates of input along with label and weight
            double[][] dcoords = new double[input.length][3];
            for (int i = 0; i < input.length; i++) {
                dcoords[i][0] = input[i][d];
                dcoords[i][1] = labels[i];
                dcoords[i][2] = weights[i];
            }
            // sort points by the d-coordinate
            Arrays.sort(dcoords, new ByDimensionComparator());

            // cumulativeWeight of points at or below stump for sP = 0
            // points labeled 1 are considered negative weight
            double cumulativeWeight = 0;
            for (int i = 0; i < input.length; i++) {
                // add/subtract weights of all points at the next coord
                int currentCoord = (int) dcoords[i][0];
                while (i < input.length && dcoords[i][0] == currentCoord) {
                    if (dcoords[i][1] == 0) cumulativeWeight += dcoords[i++][2];
                    else cumulativeWeight -= dcoords[i++][2];
                }
                i--;

                if (totalOneWeight + cumulativeWeight > maxWeight) {
                    maxWeight = totalOneWeight + cumulativeWeight;
                    dP = d;
                    vP = currentCoord;
                    sP = 0;
                }
                if (totalZeroWeight - cumulativeWeight > maxWeight) {
                    maxWeight = totalZeroWeight - cumulativeWeight;
                    dP = d;
                    vP = currentCoord;
                    sP = 1;
                }
            }
        }
        k = input[0].length;
    }

    private static class ByDimensionComparator implements Comparator<double[]> {
        public ByDimensionComparator() {
        }

        public int compare(double[] a, double[] b) {
            return Double.compare(a[0], b[0]);
        }
    }

    // return the prediction of the learner for a new sample
    public int predict(int[] sample) {
        if (sample == null)
            throw new IllegalArgumentException("cannot be called with null argument");
        if (sample.length != k)
            throw new IllegalArgumentException("incompatible argument length");

        if (sP == 0) {
            if (sample[dP] <= vP) return 0;
            else return 1;
        }
        else {
            if (sample[dP] <= vP) return 1;
            else return 0;
        }
    }

    // return the dimension the learner uses to separate the data
    public int dimensionPredictor() {
        return dP;
    }

    // return the value the learner uses to separate the data
    public int valuePredictor() {
        return vP;
    }

    // return the sign the learner uses to separate the data
    public int signPredictor() {
        return sP;
    }

    // unit testing
    public static void main(String[] args) {

    }
}
