/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.ai;

import ancienttiles.HumanIntelligence;
import ancienttiles.Tile;
import java.awt.Image;
import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author krr428
 */
public class HoverTransitP extends HoverTransit
{

    private Set<Point> visitedTiles;

    public HoverTransitP(Image i)
    {
        super(i);
    }

    public HoverTransitP(Image i, int x, int y, String layer)
    {
        super(i, x, y, layer);
    }

    @Override
    protected void initMoveVars()
    {
        visitedTiles = new HashSet<Point>();
        findPath();
    }

    private List<Point> getPossibleMoves()
    {
        List<Point> moves = new LinkedList<Point>();

        //Check along move path.
        if (canMove(xloc + moveX, yloc + moveY))
        {
            moves.add(new Point(moveX, moveY));
        }
        if (canMove(xloc + 1, yloc))
        {
            moves.add(new Point(1, 0));
        }
        if (canMove(xloc - 1, yloc))
        {
            moves.add(new Point(-1, 0));
        }
        if (canMove(xloc, yloc + 1))
        {
            moves.add(new Point(0, 1));
        }
        if (canMove(xloc, yloc - 1))
        {
            moves.add(new Point(0, -1));
        }

        return moves;
    }

    private List<Point> prioritizePath(List<Point> possibleMoves)
    {
        //Restructures the list so that it is better prioritized.
        LinkedList<Point> prioritized = new LinkedList<Point>();
        //First, see if the point is in the list.

        Point directionMove = null;
        Point backwardsMove = null;

        for (Point p : possibleMoves)
        {
            if (p.x == moveX && p.y == moveY)
            {
                //If it is moving in the direction we want, put it first.
                directionMove = p;
            }
            // if it is moving where we've already been, put it last.
            //  Still want to be able to move this way if necessary, just
            // last priority.
            else if (visitedTiles.contains(new Point(p.x + xloc, p.y + yloc)))
            {
                backwardsMove = p;
            }
            else
            {
                prioritized.add(p);
            }
        }

        if (directionMove != null)
        {
            prioritized.offerFirst(directionMove);
        }
        if (backwardsMove != null)
        {
            prioritized.offerLast(backwardsMove);
        }

        return prioritized;
    }

    protected void findPath()
    {
        List<Point> possibleMoves = prioritizePath(getPossibleMoves());

        for (Point p : possibleMoves)
        {
            if (canMove(p.x + xloc, p.y + yloc))
            {
                moveX = p.x;
                moveY = p.y;
                break;
            }
        }
    }

    @Override
    protected void turn()
    {
        findPath(); //Turn around?
        if (visitedTiles.contains(new Point(xloc + moveX, yloc + moveY)))
        {
            endOfLine();
        }
    }

    private void endOfLine()
    {
        this.setActivated(false);
        rider = null;
        visitedTiles.clear();
    }

    @Override
    public void move()
    {
        if (visitedTiles.contains(new Point(xloc + moveX, yloc + moveY)))
        {
            visitedTiles.clear();
        }
        super.move();
        visitedTiles.add(new Point(xloc, yloc));
    }

    @Override
    protected boolean canMove(int x, int y)
    {
        boolean rawCanMove = super.canMove(x, y);

        if (!rawCanMove)
        {
            return false;
        }

        for (Tile t : gameManager.getMapManager().getCurTiles(x, y))
        {
            if (t.hasAttribute("path"))
            {
                return true;
            }
        }

        return false; //Does not have a path to follow.

    }

    @Override
    public void movedOn(HumanIntelligence hi)
    {
        if (rider != hi)
        {
            findPath();
            setActivated(true);
            //Figure out what direction we are supposed to go.
            rider = hi;

        }
    }
//    @Override
//    protected boolean canMove(Tile t)
//    {
//        if (t.hasAttribute("ai")
//                || t.hasAttribute("wall")
//                || t.hasAttribute("floor"))
//        {
//            return false;
//        }
//        else
//        {
//            return true;
//        }
//    }
}
