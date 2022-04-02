Answer:using System;

using System.Linq;

class Solution  

{

   public static int Assignment(int number)  

   {

       // Consider that int.MaxValue equals to 2147483647

       var siblingString = String.Join("", number.ToString().ToCharArray().OrderByDescending(n => n));

       int sibling = -1;

       if (!int.TryParse(siblingString, out sibling) || sibling > 100000000)

       {

           return -1;

       }

       return sibling;

   }

}