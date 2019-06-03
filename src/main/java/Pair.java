/**
 * Generic pair class.
 */
public class Pair<A, B> {
   private final A fst;
   private final B snd;
   
   public Pair(A fst, B snd) {
       this.fst = fst;
       this.snd = snd;
   } 

   public A getFirst() {
     return fst;
   }

   public B getSecond() {
     return snd;
   }

   @Override
   public String toString() {
     return "(" + fst.toString() + ", " + snd.toString() + ")";
   }
}