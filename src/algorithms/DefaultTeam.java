/*
   KNN Algorithm
   @author : Amine BENSLIMANE
   Master 2 STL, Sorbonne Université
   November 2021
 */
package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;


public class DefaultTeam {
    private static final Random rd = new Random();
    /**
     * The number of clusters in the K-Means Algorithm
     */
    private final static int K = 5;
    /**
     * KNN Algorithm (K-Means) for k = 5
     * <p>
     * Compute 5 sets of points with the classical k-means algorithm
     *
     * @param points : Set of points
     * @return K sets of points (clusters)
     */
    public ArrayList<ArrayList<Point>> calculKMeans(ArrayList<Point> points) {

        Point[] means = new Point[K];  // K cluster's means
        ArrayList<ArrayList<Point>> kMeans = new ArrayList<>(); // the data structure containing all the clusters

        //constructing the data structure
        for (int j = 0; j < K; j++) {
            kMeans.add(new ArrayList<>());
        }
        // KNN ++ Initialization of the mean points
        initializeKnnMeans(points, means);

        // now, let's divide the points into the K clusters (choose the closest cluster's mean point)
        for (int p = 0; p < points.size() / 2; p++) {
            // lets do 2 in 1 for optimizing the algorithm
            Point p1 = points.get(p);
            Point p2 = points.get(points.size() - 1 - p);
            int toAdd1 = 0;
            int toAdd2 = 0;
            double shortDist1 = Double.MAX_VALUE;
            double shortDist2 = Double.MAX_VALUE;
            double dist1;
            double dist2;

            for (int i = 0; i < K; i++) {
                dist1 = p1.distance(means[i]);
                dist2 = p2.distance(means[i]);
                if (dist1 < shortDist1) {
                    shortDist1 = dist1;
                    toAdd1 = i;
                }
                if (dist2 < shortDist2) {
                    shortDist1 = dist2;
                    toAdd2 = i;
                }
            }
            kMeans.get(toAdd1).add(p1);
            kMeans.get(toAdd2).add(p2);
        }

        while (adjustCluster(means, kMeans)) {
            // repeat the process until there's no changes comparing to a previous iteration
            adjustCluster(means, kMeans);
        }
        return kMeans;
    }
    /**
     * KNN ++ Initialization Algorithm
     * <p>
     * Choose a random mean point & for the k-1 others, pick the nearest point of one of the already picked mean points
     *
     * @param points : dataSet of points
     * @param means  : the array of means to initialize
     */
    private void initializeKnnMeans(ArrayList<Point> points, Point[] means) {
        final int SIZE = points.size();
        int nbMeans = 1;        // numbers of mean points initialized
        int chosenMean;    // index of the mean point to add
        double dist1;
        double dist2;
        means[0] = points.get(rd.nextInt(SIZE));    // choosing randomly the first mean points
        //  for the K-1 mean points, choose the nearest points of one of the already picked mean point
        while (nbMeans < K) {
            Point meanRef = new Point(means[rd.nextInt(nbMeans)]);  // one already picked mean point, searching his nearest point
            double distance = Double.MAX_VALUE;
            chosenMean = -1;
            for (int i = 0; i < SIZE / 2; i++) {
                dist1 = meanRef.distance(points.get(i));
                dist2 = meanRef.distance(points.get(SIZE - 1 - i));
                if (dist1 < distance && notContains(means,nbMeans,points.get(i))) {
                    distance = dist1;
                    chosenMean = i;
                }
                if (dist2 < distance && notContains(means,nbMeans,points.get(SIZE - 1 - i))) {
                    distance = dist2;
                    chosenMean = SIZE -1 -i;
                }
            }
            if(chosenMean >= 0) {
                means[nbMeans] = points.get(chosenMean);
                nbMeans++;
            }
        }
    }
    /**
     * @param means  :   Array of actual mean points
     * @param kMeans :  ArrayList of 5 Clusters
     *               <p>
     *               Analyse the clusters by :
     *               - adjusting its means by computing the barycenter of each set of points in a cluster
     *               - update clusters with new means for each point and flip them into the appropriate cluster if necessary
     *               <p>
     *               The algorithm ends when no changes were done in the previous iteration
     *
     *               The algorithm uses acceleration techniques, especially with sorting the means,
     *               see the article:
     *
     *               	Steven J. Phillips:
     *                  Acceleration of K-Means and Related Clustering Algorithms. ALENEX 2002: 166-177
     *
     * @return True if changes in the cluster were done, if else it returns False.
     */
    private boolean adjustCluster(Point[] means, ArrayList<ArrayList<Point>> kMeans) {
        int i;
        double distMin;
        boolean flip = false;
        ArrayList<Point> cluster;
        for (i = 0; i < K; i++) {
            cluster = kMeans.get(i);
            if (cluster.size() == 0) continue;
            means[i] = barycentre(cluster);
        }

        double[][] d = clusterDistances(means);

        int[][] m = sortedMeans(d);

        for (i = 0; i < K; i++) {
            cluster = kMeans.get(i);
            for (int k = 0; k < cluster.size(); k++) {
                Point p = cluster.get(k); // set of points of the actual cluster
                double inClassDist = p.distance(means[i]);
                distMin = inClassDist;  // it's sufficient to initialize using the current mean distance from the point
                int minClass = i;
                for (int j = 1; j < K; j++) {
                    int theClass = m[i][j];
                    if (d[i][theClass] >= 2 * inClassDist)
                        break;   // link to article
                    if (p.distance(means[theClass]) < distMin) { // a new mean is closer, must flip the point to the appropriate cluster !
                        distMin = p.distance(means[theClass]);
                        minClass = theClass;
                        flip= true;
                    }
                    if (minClass != i)
                        if(cluster.remove(p))
                            kMeans.get(minClass).add(p);
                }

                          // at least, one change have been done, must adjust the cluster...
                }
            }
        return flip;
    }

    /**
     * Sorting the means (for each i mean, m[i][j] is the sorted array of distance to other means
     *
     * @param d : inter-mean distances D[i][j] = d(u_i , u_j) for u_i, u_j means
     *
     * @return  Construct the k x k array M in which row i is a permutation of 1..k, representing
     *          the classes in increasing order of distance of their means from u_i
     *
     * See the article : Steven J. Philips, Acceleration of K-Means and Related Clustering Algorithms,
     *                   AT&T Labs-Research, DOI : 10.1007/3-540-45643-0_13
     * */
    private static int[][] sortedMeans(double[][] d) {
        int[][] c = new int[K][K];
        double min;
        ArrayList<Integer> sorted = new ArrayList<>();
        for(int i=0 ; i < K ; i++){
            sorted.clear();
            for(int j = 0 ; j < K; j++){
                min = Double.MAX_VALUE;
                for(int k = 0 ; k < K; k++){
                    if(sorted.contains(k)) continue;
                    if(d[i][k] < min){
                        min = d[i][k];
                        c[i][j] = k;
                    }
                }
                sorted.add(c[i][j]);
            }
        }
        return c;
    }

    /**
     *      Simple function which compute for an array means[i], all the inter-distances d[i][j]
     *      between these means
     *
     * @param means  : Array of K mean points
     * @return Array K x K of the mean point's inter-distances
     * */
    private double[][] clusterDistances(Point[] means) {
        double[][] d = new double[K][K];
        for(int i = 0 ; i < 5 ; i++)
            for(int j = 0 ; j < 5 ; j++)
                d[i][j] = means[i].distance(means[j]);
        return d;
    }

    /***
     * Compute the barycenter of a set of points
     *
     * Used in this context to search for mean points
     *
     * @param points : set of points
     * @return the barycenter of the set
     *
     */
    private Point barycentre(ArrayList<Point> points) {
        double x = 0;
        double y = 0;
        for (Point point : points) {
            x = x + point.getX();
            y = y + point.getY();
        }
        return new Point((int) x / points.size(), (int) y / points.size());
    }
    /***
     *  Checks if a point is not contained in an array mean of a given size
     *  We use this function in the KNN ++ initialization
     *  It allows us to know if a point is already in the means array, we implemented this function
     *  to avoid having a point which is a mean for many clusters
     *
     * @param means : Actual array of mean (in construction)
     * @param size :  The actual number of mean points initialized
     * @return (if p is not in means) boolean expression
     */
    private boolean notContains(Point[] means, int size, Point p) {
        for(int i=0;i<size;i++)
            if(means[i].distance(p) == 0)
                return false;
        return true;
    }
}