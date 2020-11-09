
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.Adapter;

import java.util.*;

import akka.compat.Future;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.Int;
import scala.concurrent.Await;

import java.io.Serializable;
import java.time.Duration;
import java.util.stream.Collectors;


public class Merge extends AbstractBehavior<Merge.Command>{
    public ActorRef<Merge.Command> parent;
    public interface Command extends Serializable{}
    private Merge(ActorContext<Merge.Command> context) {
        super(context);
    }
    public static Behavior<Command> create(){
        return Behaviors.setup(Merge::new);
    }
    private int i=0,j=0;
    private ArrayList<Integer> arr_cache=new ArrayList<>();
    public long startTime;
    public static class StartCommand implements Command{
        private static final long serialVersionUID=1L;
        private ArrayList<Integer> array=new ArrayList<>();
        public long startTime;
        public StartCommand(ArrayList<Integer> array,long startTime) {
            this.array = array;
            this.startTime=startTime;
        }

        public ArrayList<Integer> getArray() {
            return array;
        }
    }
    public static class Partition implements Command
    {
        private static final long serialVersionUID=1L;
        private ArrayList<Integer> array=new ArrayList<>();
        private ActorRef<Merge.Command> controller;
        private int start,end;
        private int MAX_SIZE;
        public Partition(ArrayList<Integer> array,ActorRef<Merge.Command> controller,int start,int end,int n)
        {
            this.array=array;
            this.controller=controller;
            this.start=start;
            this.end=end;
            this.MAX_SIZE=n;
        }

        public int getMAX_SIZE() {
            return MAX_SIZE;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public ArrayList<Integer> getArray() {
            return array;
        }

        public ActorRef<Command> getController() {
            return controller;
        }
    }
    public static class MergeSort implements Command
    {
        private static final long serialVersionUID=1L;
        private ArrayList<Integer> array=new ArrayList<>();
        private ActorRef<Merge.Command> controller;
        private int MAX_SIZE;
        public MergeSort(ArrayList<Integer> array,int n)
        {
            this.array=array;
            this.MAX_SIZE=n;

        }
        public ArrayList<Integer> getArray() {
            return array;
        }

        public int getMAX_SIZE() {
            return MAX_SIZE;
        }
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartCommand.class,message->{
                    System.out.println("wassup, merge sort akka has started");
                    ArrayList<Integer> arr= message.getArray();
                    ActorRef<Command> root = getContext().spawn(Merge.create(), "0-10");
                    startTime=message.startTime;
                    //start partitioning
                    root.tell(new Partition(arr,getContext().getSelf(),0,message.getArray().size(),message.getArray().size()));

                    return this;
                })
                .onMessage(Partition.class,message->{
                    ArrayList<Integer> array=message.getArray();
                    parent=message.getController();
//                    System.out.println(array.size());
                    if(array.size()<=1)
                    {
                        this.getContext().classicActorContext().parent().tell(new MergeSort(array,message.getMAX_SIZE()),Adapter.toClassic(getContext().getSelf()));
                    }
                    else {

                        String p1=message.getStart()+"-" + (((message.getStart()+message.getEnd())/ 2));
                        String p2=(((message.getStart()+message.getEnd())/ 2))+ "-" + message.getEnd();
                        ActorRef<Command> partition1 = getContext().spawn(Merge.create(), p1);
                        ActorRef<Command> partition2 = getContext().spawn(Merge.create(), p2);
                        partition1.tell(new Partition(new ArrayList<>(array.subList(0, array.size() / 2 )),getContext().getSelf(), message.getStart(), (((message.getStart()+message.getEnd())/ 2)),message.getMAX_SIZE()));
                        partition2.tell(new Partition(new ArrayList<>(array.subList(array.size() / 2, array.size())),getContext().getSelf(),(((message.getStart()+message.getEnd())/ 2)), message.getEnd(),message.getMAX_SIZE()));

                    }
                    return this;

                })
                .onMessage(MergeSort.class,message->{
                        //do merge sort

                    ActorRef<Command> parent=Adapter.toTyped(this.getContext().classicActorContext().parent());

                    if(message.getArray().size()>=message.getMAX_SIZE())
                    {
                        long endTime = System.currentTimeMillis();
                        System.out.println("Job done.");
                        System.out.println(getContext().getSelf().path());
                        System.out.println(message.getArray().toString());
                        System.out.println("Total execution time: " + ((endTime-startTime)) + "ms");
//                        System.out.println(startTime+" "+ endTime);
                        getContext().stop(getContext().getSelf());

                    }
                    else if(arr_cache.size()>0){
                        //#do merge sort

                        ArrayList<Integer> arr_dum=new ArrayList<>();
                        ArrayList<Integer> arr_msg=message.getArray();
                        while(i<arr_cache.size()&&j<arr_msg.size())
                        {
                            if(arr_cache.get(i)<=arr_msg.get(j)) {
                                arr_dum.add(arr_cache.get(i++));
                            }
                            else {
                                arr_dum.add(arr_msg.get(j++));
                            }
                        }
                        while(i<arr_cache.size()) {
                            arr_dum.add(arr_cache.get(i++));
                        }
                        while(j<arr_msg.size()) {
                            arr_dum.add(arr_msg.get(j++));
                        }
//                        arr_dum.addAll(message.getArray());
//                        arrdum=arrdum.stream().sorted().collect(Collectors.toCollection(ArrayList::new));
//                        System.out.println(arr_dum);
                        this.getContext().classicActorContext().parent().tell(new MergeSort(arr_dum, message.getMAX_SIZE()), Adapter.toClassic(getContext().getSelf()));
                        getContext().stop(getContext().getSelf());
                    }
                    else
                    {
                        //store array for future use when the current actor receives its other child array.
                        arr_cache= message.getArray();

                    }
                    return this;
                })
                .build();
    }

}
/* Dont mind these
 *
 * https://www.toptal.com/scala/concurrency-and-fault-tolerance-made-easy-an-intro-to-akka
 * */
