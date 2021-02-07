
/*  MilquetoastStrategy.java.  The idea:  Do nothing!  Nothing at all!
 *
 *  Name origin:  Caspar Milquetoast was a comic strip character created
 *  in the 1920s by an American cartoonist named H. T. Webster.  Caspar's
 *  surname comes from a simple dish, Milk Toast, which is pretty much
 *  as its name suggests.  Caspar was similarly meek and inoffensive, and
 *  over time the term 'milquetoast' came to describe someone or something
 *  that is weak, timid, and/or unassertive.  The term describes this
 *  strategy quite well.
 *
 *  Author:  L. McCann, July 2007.
 */


public class MilquetoastStrategy implements Strategy
{
    Player player;  // The player playing meekly.


    public void setPlayer (Player whom) { player = whom; }


            // A devotee of the Milquetoast strategy will never attack
            // another player.

    public boolean willAttack (Map board) { return false; }


            // The Strategy interface requires that we create getAttacker()
            // and getDefender() methods.  However, as willAttack() will
            // never return true, these methods will never be called.

    public Territory getAttacker () { return null; }
    public Territory getDefender () { return null; }

}
