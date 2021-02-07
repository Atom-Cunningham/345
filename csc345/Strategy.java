
/* Strategy interface.  Any player strategy must be able to:
 *
 *     (a) determine if the player will attack, given the current board, and
 *     (b) produce a reference to the player's territory that will
 *         be the source of the attack, and
 *     (c) produce a reference to the enemy territory that the attacker
 *         is to attack.
 *
 *  It is expected that willAttack(Map) is called before getAttacker()
 *  and getDefender() are called.
 */

public interface Strategy
{
    public void setPlayer (Player whom);
    public boolean willAttack (Map board);
    public Territory getAttacker ();
    public Territory getDefender ();
}
