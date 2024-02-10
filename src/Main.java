import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    public static ArrayList<Interval> intervals = new ArrayList<>();
    public static ArrayList<Interval> parentsList = new ArrayList<>();
    public static ArrayList<sortedIntervalsList> sortedList = new ArrayList<>();
    public static ArrayList<Interval> ActiveStack = new ArrayList<>();
    public static ArrayList<Interval> SpecialInactiveStack = new ArrayList<>();

    public static class Interval {
        int StartPoint;
        int EndPoint;
        int Weight;
        Interval Successor;
        Interval Parent;
        int size;
        int weightsize;
        boolean inActiveStack=false;
        public Interval(int Start, int End, int Weight) {
            this.StartPoint = Start;
            this.EndPoint = End;
            this.Weight = Weight;
            this.size = 1;
            this.weightsize=-1;
        }
    }
    public static class sortedIntervalsList {
        int point;
        Interval interval;
        Boolean side;

        public sortedIntervalsList(int point, Interval interval, Boolean side) {
            this.point = point;
            this.interval = interval;
            this.side = side;
        }
    }
    public static ArrayList<Interval> readIntervals(String filePath) throws IOException {
        ArrayList<Interval> intervals = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 3) {
                    int start = Integer.parseInt(parts[0]);
                    int end = Integer.parseInt(parts[1]);
                    int weight = Integer.parseInt(parts[2]);
                    intervals.add(new Interval(start, end, weight));
                }
            }
        }
        return intervals;
    }
    public static void bucketSortIntervalsByEnd(ArrayList<Interval> intervals) {
        int minEndPoint = intervals.stream().min(Comparator.comparingInt(i -> i.EndPoint)).orElseThrow().EndPoint;
        int maxEndPoint = intervals.stream().max(Comparator.comparingInt(i -> i.EndPoint)).orElseThrow().EndPoint;
        int range = maxEndPoint - minEndPoint + 1;
        int bucketCount = intervals.size();
        List<List<Interval>> buckets = new ArrayList<>(bucketCount);
        for (int i = 0; i < bucketCount; i++) {
            buckets.add(new ArrayList<>());
        }
        for (Interval interval : intervals) {
            int bucketIndex = (interval.EndPoint - minEndPoint) * (bucketCount - 1) / range;
            buckets.get(bucketIndex).add(interval);
        }
        int index = 0;
        for (List<Interval> bucket : buckets) {
            insertionSort(bucket); // Sort bucket
            for (Interval interval : bucket) {
                intervals.set(index++, interval);
            }
        }
    }
    public static void insertionSort(List<Interval> intervals) {
        for (int i = 1; i < intervals.size(); i++) {
            Interval key = intervals.get(i);
            int j = i - 1;
            while (j >= 0 && intervals.get(j).EndPoint > key.EndPoint) {
                intervals.set(j + 1, intervals.get(j));
                j = j - 1;
            }
            intervals.set(j + 1, key);
        }
    }
    public static void sortSortedIntervalsList() {
        int minPoint = sortedList.stream().min(Comparator.comparingInt(i -> i.point)).orElseThrow().point;
        int maxPoint = sortedList.stream().max(Comparator.comparingInt(i -> i.point)).orElseThrow().point;
        int range = maxPoint - minPoint + 1;
        int bucketCount = Math.min(sortedList.size(), range);
        List<List<sortedIntervalsList>> buckets = new ArrayList<>(bucketCount);
        for (int i = 0; i < 10000; i++) {
            buckets.add(new ArrayList<>());
        }
        for (sortedIntervalsList item : sortedList) {
            int bucketIndex = (int) ((double) (item.point - minPoint) / range * bucketCount);
            buckets.get(bucketIndex).add(item);
        }
        sortedList.clear();
        for (List<sortedIntervalsList> bucket : buckets) {
            bucketInsertionSort(bucket);
            sortedList.addAll(bucket);
        }
    }
    public static void bucketInsertionSort(List<sortedIntervalsList> bucket) {
        for (int i = 1; i < bucket.size(); i++) {
            sortedIntervalsList key = bucket.get(i);
            int j = i - 1;
            while (j >= 0 && (bucket.get(j).point > key.point ||
                    (bucket.get(j).point == key.point && !bucket.get(j).side && key.side))) {
                bucket.set(j + 1, bucket.get(j));
                j--;
            }
            bucket.set(j + 1, key);
        }
    }
    public static void Successor(ArrayList<Interval> intervals) {
        Interval successor = sortedList.get(sortedList.size()-1).interval;
        for (int i = sortedList.size() - 1; i >= 0; i--) {
            if (sortedList.get(i).side) {
                sortedList.get(i).interval.Successor = successor;
            } else {
                successor = sortedList.get(i).interval;
            }
        }
    }
    public static Interval Find(Interval interval) {
        if (interval.Parent != interval) {
            interval.Parent = Find(interval.Parent);
        }
        return interval.Parent;
    }
    static boolean Union(Interval x, Interval y) {
        Interval rootX = Find(x);
        Interval rootY = Find(y);
        if (rootY == rootX) return false;
        if (rootX.size < rootY.size) {
            rootX.Parent = rootY;
            rootY.size += rootX.size;
        } else {
            rootY.Parent = rootX;
            rootX.size += rootY.size;
        }
        return true;
    }
    public static void main(String[] args) throws IOException {
        String filePath = "C:/Users/Tarahan IT/OneDrive/Desktop/Ds.txt";
        intervals = readIntervals(filePath);
//        int num = scanner.nextInt();
//        scanner.nextLine();
//        for (int i = 0; i < num; i++) {
//            int StartPoint = scanner.nextInt();
//            int EndPoint = scanner.nextInt();
//            int Weight = scanner.nextInt();
//            scanner.nextLine();
//            Interval interval = new Interval(StartPoint, EndPoint, Weight);
//            intervals.add(interval);
        bucketSortIntervalsByEnd(intervals);
        for (Interval interval : intervals) {
            System.out.println("Interval: Start = " + interval.StartPoint + ", End = " + interval.EndPoint + ", Weight = " + interval.Weight);
        }
        for (Interval interval : intervals) {
            sortedList.add(new sortedIntervalsList(interval.StartPoint, interval, true));
            sortedList.add(new sortedIntervalsList(interval.EndPoint, interval, false));
        }
        for (Interval interval : intervals){
            interval.Parent=interval;
            parentsList.add(interval);
        }
        sortSortedIntervalsList();
        Successor(intervals);
        for (Interval interval : intervals) {
            System.out.println(interval.Successor.StartPoint);
        }
        intervals.get(0).weightsize=intervals.get(0).Weight;
        ActiveStack.add(intervals.get(0));
        for (int i = 1 ; i<intervals.size() ; i++){
            if(!Find(intervals.get(i).Successor).inActiveStack)
                Union(Find(intervals.get(i).Successor),intervals.get(i));
            else if (Find(intervals.get(i).Successor).inActiveStack) {
                intervals.get(i).weightsize=intervals.get(i).Weight+Find(intervals.get(i).Successor).Parent.weightsize;
                while (intervals.get(i).weightsize<ActiveStack.get(ActiveStack.size()-1).weightsize){
                    Union(intervals.get(i),ActiveStack.get(ActiveStack.size()-1));
                    ActiveStack.remove(ActiveStack.size()-1);
                    Find(intervals.get(i)).Parent=intervals.get(i);
                }
                while (!SpecialInactiveStack.isEmpty()){
                    Union(intervals.get(i) , SpecialInactiveStack.get(SpecialInactiveStack.size()-1));
                    SpecialInactiveStack.remove(SpecialInactiveStack.size()-1);
                }
            } else if (Find(intervals.get(i).Successor)==intervals.get(i)) {
                intervals.get(i).inActiveStack = false;
                SpecialInactiveStack.add(intervals.get(i));
            }
        }
        for (Interval interval : intervals) {
            if (interval.weightsize == -1 && Find(interval).Parent != null) {
                interval.weightsize = interval.Weight + Find(interval).Parent.weightsize;
            }
        }
        int counterPrint = 1;
        for (Interval interval : intervals) {
            System.out.println(counterPrint + " " + interval.weightsize);
            counterPrint++;
        }
    }
}
