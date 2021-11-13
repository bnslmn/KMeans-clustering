/*
   KNN Algorithm

   @author : Amine BENSLIMANE
   Master 2 STL, Sorbonne Université
   November 2021
 */
package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class DefaultTeam {
    private final Random rd = new Random();

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

        // repeat the process until there's no changes comparing to a previous iteration
        while (adjustCluster(means, kMeans)) { // called at least one time
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
        int chosenMean = -1;    // index of the mean point to add
        double dist1;
        double dist2;

        means[0] = points.get(rd.nextInt(SIZE));    // choosing randomly the first mean points

        //  for the K-1 mean points, choose the nearest points of one of the already picked mean point
        while (nbMeans < K) {
            Point meanRef = new Point(means[rd.nextInt(nbMeans)]);  // one already picked mean point, searching his nearest point
            double distance = Double.MAX_VALUE;
            for (int i = 0; i < SIZE / 2; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(SIZE - 1 - i);
                dist1 = meanRef.distance(p1);
                dist2 = meanRef.distance(p2);
                if (dist1 < distance && notContains(means, nbMeans, p1)) {
                    distance = dist1;
                    chosenMean = i;
                }
                if (dist2 < distance && notContains(means, nbMeans, p2)) {
                    distance = dist2;
                    chosenMean = SIZE - 1 - i;
                }
            }
            means[nbMeans] = points.get(chosenMean);
            nbMeans++;
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
        for (i = 0; i < K; i++) {
            cluster = kMeans.get(i);
            for (int k = 0; k < cluster.size(); k++) {
                Point p = cluster.get(k); // set of points of the actual cluster
                distMin = p.distance(means[i]);// it's suffiscient to initialize using the current mean distancefrom the point
                for (int j = 0; j < K; j++) {
                    if (i == j) continue;   // avoid comparing the points to their actual mean point
                    if (p.distance(means[j]) < distMin) { // a new mean is closer, must flip the point to the appropriate cluster !
                        distMin = p.distance(means[j]);
                        if (cluster.remove(p)) {
                            if (kMeans.get(j).add(p))
                                flip = true;    // at least, one change have been done, must adjust the cluster...
                        }
                    }
                }
            }
        }
        return flip;
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
    public Point barycentre(ArrayList<Point> points) {
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
    public boolean notContains(Point[] means, int size, Point p) {
        return Arrays.stream(means, 0, size).noneMatch(n -> n.distance(p) == 0);
    }
}