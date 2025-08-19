public class Act1 {
  public static void main(String[] args) {
    int dividend = 21;
    int divisor = 7;

    // divide
    int remainder = dividend % divisor;

    // Remainder odd or even
    if (remainder % 2 == 0) {
      System.out.println("RESULT: Even");
    } else {
      System.out.println("RESULT: Odd");
    }
  }
}