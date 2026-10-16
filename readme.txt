Programming Assignment 7: Fraud Detection

/* *****************************************************************************
 *  Describe how you implemented the Clustering constructor
 **************************************************************************** */

I created an EdgeWeightedGraph of the m locations and created each edge with
weight equal to the distance between locations.
Then I used KruskalMST to generate a minimum spanning tree.
Using a MinPQ, I chose the m-k least weighted edges and added them to an
EdgeWeightedGraph to create my CC object.

/* *****************************************************************************
 *  Describe how you implemented the WeakLearner constructor
 **************************************************************************** */

I first found the total weight of all the 0's and all the 1's in the input.
This is used to find the weight of a given stump, which is equal to

 [totalOneWeight] + [sum of 0's at/below vP] - [sum of 1's at/below vP]
 if sp = 0

 [totalZeroWeight] - [sum of 0's at/below vP] + [sum of 1's at/below vP]
 if sp = 1

As I iterate through each dimension, 'cumulativeWeight' will represent
[sum of 0's at/below vP] - [sum of 1's at/below vP]

For each dimension:
    - for each d-coordinate in the input:
        - update cumulative weight according to the points at the coordinate
        - if this stump has the greatest weight so far, store it

After testing all possible stumps, we will have the best one.

/* *****************************************************************************
 *  Consider the large_training.txt and large_test.txt datasets.
 *  Run the boosting algorithm with different values of k and T (iterations),
 *  and calculate the test data set accuracy and plot them below.
 *
 *  (Note: if you implemented the constructor of WeakLearner in O(kn^2) time
 *  you should use the small_training.txt and small_test.txt datasets instead,
 *  otherwise this will take too long)
 **************************************************************************** */

      k          T         test accuracy       time (seconds)
   --------------------------------------------------------------------------
     10         10              0.925               0.585
     30         10              0.895               0.74
     100        10              0.8125              0.973
     10         30              0.93375             0.715
     10         100             0.95875             0.989
     10         1000            0.96625             4.107

/* *****************************************************************************
 *  Find the values of k and T that maximize the test data set accuracy,
 *  while running under 10 second. Write them down (as well as the accuracy)
 *  and explain:
 *   1. Your strategy to find the optimal k, T.
 *   2. Why a small value of T leads to low test accuracy.
 *   3. Why a k that is too small or too big leads to low test accuracy.
 **************************************************************************** */

k = 18, T = 1800, test accuracy: 0.97625, time elapsed: 9.927

1. I noticed that increasing T increases test accuracy but has diminishing returns
    as T gets larger, so I set T = 1000 and tested values of k until I found the
    optimal value. Then I increased T until I reached just under 10 seconds.

2. A small value of T means that there aren't many decision stumps to capture the
    pattern of the training data. In an extreme example, if k = 10 and T = 9
    then there will be at least one dimension that we aren't using to predict at
    all. The greater T is, the better we can differentiate between points.

3. Too small k prevents the model from differentiating between points which may
    influence the label
   Too large k means that the model will be overly fit to the training data and
    is more affected by random variations not present in the testing data.

/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback
 *  on how much you learned from doing the assignment, and whether
 *  you enjoyed doing it.
 **************************************************************************** */
