package algorithms;

import java.awt.Point;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class DefaultTeam {
    private final Random rd = new Random();

    public ArrayList<ArrayList<Point>> calculKMeans(ArrayList<Point> points) {

        // Initialization
        Point[] means = new Point[5];

        ArrayList<ArrayList<Point>> kMeans = new ArrayList<>();
        kMeans.add(0, new ArrayList<Point>());
        kMeans.add(1, new ArrayList<Point>());
        kMeans.add(2, new ArrayList<Point>());
        kMeans.add(3, new ArrayList<Point>());
        kMeans.add(4, new ArrayList<Point>());
        int i = 0;
        boolean trouve = false;
        while (i <= 4) {
            Point mean = new Point((int) rd.nextInt(points.size()), (int) rd.nextInt(points.size()));
            for(int j = 0 ; j <=4; j++){
                if(means[j] == null) continue;
                if ( mean.x == means[j].x && mean.y == means[j].y){
                    trouve = true;
                    break;
                }
            }
            if(!trouve){
                means[i] = mean;
                i++;
            }
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
        while(swap){
            swap = adjustCluster(means, kMeans);
        }
        return kMeans;
    }

    private boolean adjustCluster(Point[] means, ArrayList<ArrayList<Point>> kMeans) {
        int i;
        double distMin;
        for (i = 0; i < 5; i++){
            if(kMeans.get(i).size() == 0) continue;
            means[i] = barycentre(kMeans.get(i));
        }

        //kMeans = [p1p2p3...p_n ;     p1p2p3...p_m  ;  ....   ;   p1p2...p_k]
        boolean swap = false;
        for (i = 0; i <= 4; i++) {
            for (int k = 0 ; k < kMeans.get(i).size() ; k++) {
                Point p = kMeans.get(i).get(k);
                distMin = p.distance(means[i]);
                for (int j = 0; j <= 4; j++) {
                    if (i == j) continue;
                    if (p.distance(means[j]) < distMin) {
                        //swap
                        swap = true;
                        distMin = p.distance(means[i]);
                        if(kMeans.get(i).remove(p))
                             kMeans.get(j).add(p);
                    }
                }
            }
        }
        return swap;
    }
    public Point barycentre(ArrayList<Point> points) {
        double x = 0;
        double y = 0;
        for (Point point : points) {
            x = x + point.getX();
            y = y + point.getY();
        }
        return new Point((int)x / points.size(), (int)y / points.size());
    }
}