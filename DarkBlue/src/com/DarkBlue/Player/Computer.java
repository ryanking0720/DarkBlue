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
        public final boolean IsHuman();
    
    SYNOPSIS
        public final boolean IsHuman();
    
        No parameters.
    
    DESCRIPTION
        This method returns if this player's type is human.
    
    RETURNS
        boolean: Always returns false.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsHuman(){
        return false;
    }
    
    /**/
    /*
    NAME
        public final boolean IsComputer();
    
    SYNOPSIS
        public final boolean IsComputer();
    
        No parameters.
    
    DESCRIPTION
        This method returns if this player's type is computer.
    
    RETURNS
        boolean: Always returns true.
    
    AUTHOR
        Ryan King
    */
    @Override
    public final boolean IsComputer(){
        return true;
    }
    
    @Override
    public final PlayerType GetPlayerType(){
    	return PlayerType.COMPUTER;
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
}