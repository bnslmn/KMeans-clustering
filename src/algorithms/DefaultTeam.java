package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;


public class DefaultTeam {
    private final Random rd = new Random();


    /**
     * KNN Algorithm (K-Means) for k = 5
     *
     * Compute 5 sets of points with the classical k-means algorithm
     *
     *  @param points  : Set of points
     *  @return  5 sets of points (clusters)
     * */
    public ArrayList<ArrayList<Point>> calculKMeans(ArrayList<Point> points) {

        Point[] means = new Point[5];  // K = 5 clusters
        ArrayList<ArrayList<Point>> kMeans = new ArrayList<>(); // the data structure containing all the clusters

        for (int j = 0; j < 5; j++) {
            kMeans.add(j, new ArrayList<>());
            means[j] = new Point(rd.nextInt(points.size()), rd.nextInt(points.size()));
        }
        // now, let's divide the points into the 5 clusters (choose the closest cluster's mean point)
        double shortDist;
        for (Point p : points) {
            shortDist = Double.MAX_VALUE;
            int toAdd = 0;
            for (int i = 0; i <= 4; i++) {
                if (p.distance(means[i]) < shortDist) {
                    shortDist = p.distance(means[i]);
                    toAdd = i;
                }
            }
            kMeans.get(toAdd).add(p);
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
     *   - compute new distances from the new mean for each points and flip them into the appropriate cluster
     *
     * The algorithm ends when no changes were done in the previous iteration
     *
     * @return True if changes in the cluster were done, if else it returns False.
     *
     * */
    private boolean adjustCluster(Point[] means, ArrayList<ArrayList<Point>> kMeans) {
        int i;
        double distMin;
        for (i = 0; i < 5; i++) {
            if (kMeans.get(i).size() == 0) continue;
            means[i] = barycentre(kMeans.get(i));
        }

        boolean flip = false;
        ArrayList<Point> cluster;
        for (i = 0; i <= 4; i++) {
            cluster = kMeans.get(i);
            for (int k = 0; k < cluster.size(); k++) {
                Point p = cluster.get(k); // set of points of the actual cluster
                distMin = p.distance(means[i]);// it's suffiscient to initialize using the current mean distancefrom the point
                for (int j = 0; j <= 4; j++) {
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