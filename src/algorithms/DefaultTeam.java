package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;


public class DefaultTeam {
    private final Random rd = new Random();

    private final static int K = 9;

    /**
     * KNN Algorithm (K-Means) for k = 5
     *
     * Compute 5 sets of points with the classical k-means algorithm
     *
     *  @param points  : Set of points
     *  @return  K sets of points (clusters)
     * */
    public ArrayList<ArrayList<Point>> calculKMeans(ArrayList<Point> points) {

        Point[] means = new Point[K];  // K cluster's means
        ArrayList<ArrayList<Point>> kMeans = new ArrayList<>(); // the data structure containing all the clusters

        for (int j = 0; j < K; j++) {
            kMeans.add(j, new ArrayList<>());
            means[j] = new Point(rd.nextInt(points.size()), rd.nextInt(points.size()));
        }
        // now, let's divide the points into the K clusters (choose the closest cluster's mean point)
        double shortDist;
        for (int p=0 ; p<points.size()/2 ; p++) {

            Point p1 = points.get(p);
            Point p2 = points.get(points.size()-1-p);

            int toAdd1 = 0;
            int toAdd2 = 0;
            double shortDist1 = Double.MAX_VALUE;
            double shortDist2 = Double.MAX_VALUE;
            for (int i = 0; i < K; i++) {
                if (p1.distance(means[i]) < shortDist1) {
                    shortDist1 = p1.distance(means[i]);
                    toAdd1 = i;
                }
                if (p2.distance(means[i]) < shortDist2) {
                    shortDist1 = p2.distance(means[i]);
                    toAdd2= i;
                }
            }
            kMeans.get(toAdd1).add(p1);
            kMeans.get(toAdd2).add(p2);
        }

        // repeat the process until there's no changes comparing to a previous iteration
        while (adjustCluster(means, kMeans)) { // called at least one time
            adjustCluster(means, kMeans) ;
        }
        return kMeans;
    }


    /**
     * @param means :   Array of actual mean points
     * @param kMeans :  ArrayList of 5 Clusters
     *
     * Analyse the clusters by :
     *   - adjusting its means by computing the barycenter of each set of points in a cluster
     *   - update clusters with new means for each point and flip them into the appropriate cluster if necessary
     *
     * The algorithm ends when no changes were done in the previous iteration
     *
     * @return True if changes in the cluster were done, if else it returns False.
     *
     * */
    private boolean adjustCluster(Point[] means, ArrayList<ArrayList<Point>> kMeans) {
        int i;
        double distMin;
        ArrayList<Point> cluster;
        for (i = 0; i < K; i++) {
            cluster = kMeans.get(i);
            if (cluster.size() == 0) continue;
            means[i] = barycentre(cluster);
        }
        boolean flip = false;
        for (i = 0; i < K; i++) {
            cluster = kMeans.get(i);
            for (int k = 0; k < cluster.size(); k++) {
                Point p = cluster.get(k); // set of points of the actual cluster
                distMin = p.distance(means[i]);// it's suffiscient to initialize using the current mean distancefrom the point
                for (int j = 0; j < K; j++) {
                    if (i == j) continue;
                    if (p.distance(means[j]) < distMin) { // a new mean is closer, must flip the point to the appropriate cluster !
                        distMin = p.distance(means[j]);
                        if (cluster.remove(p)) {
                            if (kMeans.get(j).add(p))
                                flip = true;
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
}