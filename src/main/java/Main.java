import akka.actor.typed.ActorSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        ActorSystem<Merge.Command> mergesort=ActorSystem.create(Merge.create(),"mergesort");
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter # integer array elements followed by  elements next:");
        int n=sc.nextInt();
        ArrayList<Integer> arr=new ArrayList<>();
        while(n-->0)
            arr.add(sc.nextInt());
        long startTime = System.currentTimeMillis();
        mergesort.tell(new Merge.StartCommand(arr,startTime));

    //big oof moment
    }
}

/*
* 2, 5, 3, 1, 2, 6, 8, 3, 6, 5
*new ArrayList<>(Arrays.asList(10,80,30,90,40,50,70))
*
*
7
3 4 5 1 7 6 2
*
* */