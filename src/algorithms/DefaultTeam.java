package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class DefaultTeam {
    private final Random rd = new Random();

    public ArrayList<ArrayList<Point>> calculKMeans(ArrayList<Point> points) {

        // Initialization
        Point[] means = new Point[5];

        ArrayList<ArrayList<Point>> kMeans = new ArrayList<>();

        for (int j = 0; j < 5; j++)
            kMeans.add(j, new ArrayList<>());

        int i = 0;
        while (i <= 4) {
            means[i] = new Point(rd.nextInt(points.size()), rd.nextInt(points.size()));
            i++;
        }

        // means = [Point1 , Point2 , Point3, Point4, Point5]

        // Maintenant, partageons les points en cinq selon le mean le plus proche
        double distMin;
        for (Point p : points) {
            distMin = Double.MAX_VALUE;
            int toAdd = 0;
            for (i = 0; i <= 4; i++) {
                if (p.distance(means[i]) < distMin) {
                    distMin = p.distance(means[i]);
                    toAdd = i;
                }
            }
            kMeans.get(toAdd).add(p);
        }

        boolean swap = true;
        while (swap) {
            swap = adjustCluster(means, kMeans);
        }
        return kMeans;
    }

    private boolean adjustCluster(Point[] means, ArrayList<ArrayList<Point>> kMeans) {
        int i;
        double distMin;
        for (i = 0; i < 5; i++) {
            if (kMeans.get(i).size() == 0) continue;
            means[i] = barycentre(kMeans.get(i));
        }

        //kMeans = [p1p2p3...p_n ;     p1p2p3...p_m  ;  ....   ;   p1p2...p_k]
        boolean bascule = false;
        for (i = 0; i <= 4; i++) {
            for (int k = 0; k < kMeans.get(i).size(); k++) {
                Point p = kMeans.get(i).get(k); // points du cluster actuel
                distMin = p.distance(means[i]);// distance entre le point et son mean actuel
                for (int j = 0; j <= 4; j++) {
                    if (i == j) continue;
                    if (p.distance(means[j]) < distMin) { // si il existe un autre mean plus proche ==> basculer mean
                        distMin = p.distance(means[j]);
                        if (kMeans.get(i).remove(p)) {
                            if (kMeans.get(j).add(p))
                                bascule = true;
                        }
                    }
                }
            }
        }
        return bascule;
    }

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