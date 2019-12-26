package com.DarkBlue.Player;

import com.DarkBlue.Utilities.*;

import java.util.ArrayList;

import com.DarkBlue.Board.*;
import com.DarkBlue.Move.*;
import com.DarkBlue.GUI.*;

public final class Computer extends Player{

    /**/
    /*
    NAME
        public Computer(final ChessColor a_color, final Board a_board);
    
    SYNOPSIS
        public Computer(final ChessColor a_color, final Board a_board);
    
        ChessColor a_color -----> The player's color.
        
        Board a_board ----------> The current state of the board.
    
    DESCRIPTION
        This constructor instantiates a new Computer object.
        The type is automatically set the PlayerType.COMPUTER.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Computer(final ChessColor a_color, final Board a_board){
        super(a_color, a_board, PlayerType.COMPUTER);
    }
    
    /**/
    /*
    NAME
        public Computer(final Player a_player, final Board a_board);
    
    SYNOPSIS
        public Computer(final Player a_player, final Board a_board);
    
        Player a_player --------> The player to be copied.
        
        Board a_board ----------> The current state of the board.
    
    DESCRIPTION
        This constructor instantiates a new Computer object.
        This is meant to be the copy constructor.
        The type is automatically set the PlayerType.COMPUTER.
    
    RETURNS
        Nothing
    
    AUTHOR
        Ryan King
    */
    public Computer(final Player a_player, final Board a_board){
        super(a_player.GetColor(), a_board, a_player.GetType());
    }
    
    /**/
    /*
    NAME
        public final Move Search();
    
    SYNOPSIS
        public final Move Search();
    
        No parameters.
    
    DESCRIPTION
        This method returns the best move from the move tree.
    
    RETURNS
        Move: The best move from the move tree.
    
    AUTHOR
        Ryan King
    */
    public final Move Search(final int a_level, final Board a_board, final Player a_white, final Player a_black){
        /*int bestIndex = Utilities.ZERO;*/
        //int total = Utilities.ZERO;
        Move bestMove = null;
        if(a_level == DarkBlue.GetMaxSearchDepth()){
            return bestMove;
        }else{
            Board clone = Board.GetDeepCopy(a_board);
            ArrayList<Move> attackMoves = GetAttackingMoves();
            for(int index = Utilities.ONE; index < attackMoves.size(); index++){
                if(attackMoves.get(index).GetMoveType() == MoveType.EN_PASSANT){
                    //total += attackMoves.get(index).GetVictim().GetValue();
                    clone = clone.EnPassant((EnPassantMove)attackMoves.get(index), a_white, a_black);
                }else{
                    //total += attackMoves.get(index).GetVictim().GetValue();
                    clone = clone.Attack((AttackingMove)attackMoves.get(index), a_white, a_black);
                }
            }
            return Search(a_level + Utilities.ONE, clone, a_white, a_black);
        }
    }
    
    /**/
    /*
    NAME
        public final ArrayList<Move> GetAttackingMoves();
    
    SYNOPSIS
        public final ArrayList<Move> GetAttackingMoves();
    
        No parameters.
    
    DESCRIPTION
        This method returns an ArrayList comprised only of 
        attacking moves, e.g. normal attacking moves and
        en passant moves, by checking the move type of each.
    
    RETURNS
        ArrayList<Move> attackMoves: All of the attack moves this player can make.
    
    AUTHOR
        Ryan King
    */
    public final ArrayList<Move> GetAttackingMoves(){
        ArrayList<Move> attackMoves = new ArrayList<>();
        for(int index = Utilities.ZERO; index < this.m_allCurrentLegalMoves.size(); index++){
            if(this.m_allCurrentLegalMoves.get(index).IsAttacking() || this.m_allCurrentLegalMoves.get(index).IsEnPassant()){
                attackMoves.add(this.m_allCurrentLegalMoves.get(index));
            }
        }
        return attackMoves;
    }
    
    /**/
    /*
    NAME
        public final int GetMaxIndex(final ArrayList<Move> a_moves);
    
    SYNOPSIS
        public final int GetMaxIndex(final ArrayList<Move> a_moves);
    
        ArrayList<Move> a_moves --------> The moves to be maximized.
    
    DESCRIPTION
        This method returns the index of the move whose victim has
        the highest value out of all of the moves with victims.
        The loop iterates through every move and returns the
        result at the end.
    
    RETURNS
        int maxIndex: The index whose move has the highest victim value.
    
    AUTHOR
        Ryan King
    */
    public final int GetMaxIndex(final ArrayList<Move> a_moves){
        int maxIndex = Utilities.ZERO;
        for(int index = Utilities.ONE; index < a_moves.size(); index++){
            if(!a_moves.get(index).HasVictim()){
                continue;
            }else{
                if(a_moves.get(maxIndex).GetVictim().GetValue() < a_moves.get(index).GetVictim().GetValue()){
                    maxIndex = index;
                }
            }
        }
        return maxIndex;
    }
    
    /**/
    /*
    NAME
        public final int GetMinIndex(final ArrayList<Move> a_moves);
    
    SYNOPSIS
        public final int GetMinIndex(final ArrayList<Move> a_moves);
    
        ArrayList<Move> a_moves --------> The moves to be minimized.
    
    DESCRIPTION
        This method returns the index of the move whose victim has
        the lowest value out of all of the moves with victims.
        The loop iterates through every move and returns the
        result at the end.
    
    RETURNS
        int minIndex: The index whose move has the lowest victim value.
    
    AUTHOR
        Ryan King
    */
    public final int GetMinIndex(final ArrayList<Move> a_moves){
        int minIndex = Utilities.ZERO;
        for(int index = Utilities.ONE; index < a_moves.size(); index++){
            if(!a_moves.get(index).HasVictim()){
                continue;
            }else{
                if(a_moves.get(minIndex).GetVictim().GetValue() > a_moves.get(index).GetVictim().GetValue()){
                    minIndex = index;
                }
            }
        }
        return minIndex;
    }
}