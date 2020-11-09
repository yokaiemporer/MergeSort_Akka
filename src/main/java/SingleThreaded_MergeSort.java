import scala.concurrent.impl.FutureConvertersImpl;

import java.util.ArrayList;
import java.util.Scanner;

//Single threaded program for merge sort
public class SingleThreaded_MergeSort {
    private ArrayList<Integer> inputArray;

    public ArrayList<Integer> getSortedArray() {
        return inputArray;
    }

    public SingleThreaded_MergeSort(ArrayList<Integer> inputArray) {
        this.inputArray = inputArray;
    }

    public void sortGivenArray() {
        divide(0, this.inputArray.size() - 1);
    }

    public void divide(int startIndex, int endIndex) {

        if (startIndex < endIndex && (endIndex - startIndex) >= 1) {
            int mid = (endIndex + startIndex) / 2;
            divide(startIndex, mid);
            divide(mid + 1, endIndex);

            merger(startIndex, mid, endIndex);
        }
    }

    public void merger(int startIndex, int midIndex, int endIndex) {

        ArrayList<Integer> mergedSortedArray = new ArrayList<Integer>();

        int leftIndex = startIndex;
        int rightIndex = midIndex + 1;

        while (leftIndex <= midIndex && rightIndex <= endIndex) {
            if (inputArray.get(leftIndex) <= inputArray.get(rightIndex)) {
                mergedSortedArray.add(inputArray.get(leftIndex));
                leftIndex++;
            } else {
                mergedSortedArray.add(inputArray.get(rightIndex));
                rightIndex++;
            }
        }


        while (leftIndex <= midIndex) {
            mergedSortedArray.add(inputArray.get(leftIndex));
            leftIndex++;
        }

        while (rightIndex <= endIndex) {
            mergedSortedArray.add(inputArray.get(rightIndex));
            rightIndex++;
        }

        int i = 0;
        int j = startIndex;

        while (i < mergedSortedArray.size()) {
            inputArray.set(j, mergedSortedArray.get(i++));
            j++;
        }
    }
//    public long perform(ArrayList<Integer> unsortedArray)
//    {
//
//
//    }

    public static void main(String[] args) {
        ArrayList<Integer> arr=new ArrayList<>();
//        Scanner sc=new Scanner(System.in);
//        System.out.println("Enter # integer array elements followed by  elements next:");
//        int n=sc.nextInt();

//        while(n-->0)
//            arr.add(sc.nextInt());
        for(int i=21474836;i>0;i--)
        {
            arr.add(i);
        }
        long startTime = System.currentTimeMillis();
        System.out.println("Started...");
        SingleThreaded_MergeSort ms = new SingleThreaded_MergeSort(arr);
        ms.sortGivenArray();
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + ((endTime-startTime)) + "ms");
    }
    //approx exec time 10651ms

}
//borrowed from elsewhere lol