package regions_tree;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return String.format("(%s, %s)", x, y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public static List<Point> readPoints(String fileName) throws FileNotFoundException {
        List<Point> pointsList = new ArrayList<>();
        Scanner scanner = new Scanner(new File(fileName));

        while (scanner.hasNext()) {
            pointsList.add(new Point(scanner.nextDouble(), scanner.nextDouble()));
        }
        scanner.close();
        return pointsList;
    }
}

class Segment {
    private Point begin;
    private Point end;

    public Segment(Point begin, Point end) {
        this.begin = begin;
        this.end = end;
    }

    public void setBegin(Point begin) {
        this.begin = begin;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public String toString() {
        return this.begin.toString() + ":" + this.end.toString();
    }

    public Point getBegin() {
        return begin;
    }
}

class Node {
    Segment segment;
    private Node left;
    private Node right;
    private List<Segment> innerSegments;

    public Node(Segment segment, Node left, Node right) {
        this.segment = segment;
        this.left = left;
        this.right = right;
        this.innerSegments = new ArrayList<>();
    }

    public Segment getSegment() {
        return segment;
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public List<Segment> getInnerSegments() {
        return innerSegments;
    }

    public void setInnerSegments(List<Segment> innerSegments) {
        this.innerSegments = innerSegments;
    }

}

public class Main {

    private static Node insert(Node root, Segment i) {
        if (root == null) {
            return new Node(i, null, null);
        }
        double b = root.segment.getBegin().getX();
        double e = root.segment.getEnd().getX();
        if (i.getBegin().getX() <= b && i.getEnd().getX() >= e) {
            root.getInnerSegments().add(i);
        }
        if (i.getBegin().getX() <= (e + b) / 2) {
            root.setLeft(insert(root.getLeft(), i));
        }
        if (i.getEnd().getX() > (e + b) / 2) {
            root.setRight(insert(root.getRight(), i));
        }
        return root;
    }

    private static List<Segment> createSegments(List<Point> array) {
        List<Segment> result = new ArrayList<>();
        int size = array.size();
        for (int i = 0; i < size - 1; i++) {
            Segment newSegment = new Segment(array.get(i), array.get(i + 1));
            result.add(newSegment);
        }
        return result;
    }

    private static void order(Node root, List<Node> nodes) {
        if (root == null) {
            return;
        }

        order(root.getLeft(), nodes);
        nodes.add(root);
        order(root.getRight(), nodes);
    }

    private static void extract(List<Point> result, Segment regionSegment, List<Node> nodesForRegion) {
        for (Node node : nodesForRegion) {
            Point b = node.segment.getBegin();
            Point e = node.segment.getEnd();
            if (b.getY() > regionSegment.getEnd().getY() || b.getY() < regionSegment.getBegin().getY()) {
                continue;
            } else {
                if (!result.contains(b)) {
                    result.add(b);
                }
            }
            if (!result.contains(e)) {
                result.add(e);
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        List<Point> points = Point.readPoints("src/points.txt");
        List<Point> regionSearch = Point.readPoints("src/region_search.txt");
        List<Point> result = new ArrayList<>();

        int n = points.size();

        List<Segment> segments = createSegments(points);

        Node root = new Node(new Segment(points.get(0), points.get(n - 1)), null, null);
        for (Segment segment : segments) {
            root = insert(root, segment);
        }

        Segment regionSegment = new Segment(regionSearch.get(0), regionSearch.get(2));
        root = insert(root, regionSegment);

        List<Node> nodes = new ArrayList<>();
        order(root, nodes);
        List<Node> regionNodes = new ArrayList<>();
        for (Node value : nodes) {
            if (value.getInnerSegments().contains(regionSegment)) {
                regionNodes.add(value);
            }
        }

        extract(result, regionSegment, regionNodes);
        System.out.println(result);
        System.out.println("Total " + result.size());
    }
}