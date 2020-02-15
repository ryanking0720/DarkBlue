package com.DarkBlue.Player;

import com.DarkBlue.Board.Board;
import com.DarkBlue.GUI.DarkBlue;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Utilities.Factory;
import com.DarkBlue.Utilities.Utilities;
import com.DarkBlue.Move.Move;
import com.DarkBlue.Move.RegularMove;
import com.DarkBlue.Move.AttackingMove;
import com.DarkBlue.Move.CastlingMove;
import com.DarkBlue.Move.EnPassantMove;

import java.util.ArrayList;
import java.util.Random;

public class Minimax{
	
	public static final int CHECK_FACTOR = 50;
	public static final int CHECKMATE_FACTOR = 100000;
	public static final int DEPTH_FACTOR = 100;
	public static final int CASTLING_FACTOR = 60;
	public static final Random random = new Random();

	private static Move m_bestMove;
	
	private static Board m_clone;
	
	private Minimax(){
		throw new RuntimeException("You cannot instantiate me!");
	}
	
	/**/
    /*
    NAME
        public static int Minimize(final Board a_board, final Player a_white, final Player a_black, final int a_depth, final int a_highest, final int a_lowest);
    
    SYNOPSIS
        public static int Minimize(final Board a_board, final Player a_white, final Player a_black, final int a_depth, final int a_highest, final int a_lowest);
    
        Board a_board ------------> The board to evaluate.
        
        Player a_white -----------> The white player.
        
        Player a_black -----------> The black player.
        
        int a_depth --------------> The current search depth.
        
        int a_highest ------------> The highest seen value so far.
        
        int a_lowest -------------> The lowest seen value so far.
    
    DESCRIPTION
        This method finds the board with the lowest value in terms of piece placement
        along with other conditions such as castling, check, checkmate, etc. after a move
        has been applied to it. It will return the final value once the search depth reaches zero.
    
    RETURNS
        int lowestValue: The lowest board evaluation value found.
    
    AUTHOR
        Amir Afghani, Black Widow Chess: https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/player/ai/MiniMax.java
        Modifications written specifically for this engine by Ryan King.
    */
	public static int Minimize(final Board a_board, final Player a_white, final Player a_black, final int a_depth, final int a_highest, final int a_lowest){
		
		final Player mover = (a_board.WhoseTurnIsIt().IsWhite() ? a_white : a_black);
		final Player opponent = (mover.IsWhite() ? a_black : a_white);
		final String history = (mover.IsWhite() ? DarkBlue.GetWhiteHistory() : DarkBlue.GetBlackHistory());
		int lowestValue = a_lowest;
		
		if(a_depth == Utilities.ZERO){
			return Score(mover, opponent, a_board, a_depth);
		}
		
		/*
		final Runnable r = new Runnable() {
			@Override
			public final void run() {
		*/
				// Reverse the array depending on the side playing?
				//ArrayList<Move> moves = Sort(mover, opponent, a_board, a_depth, history);
				ArrayList<Move> moves = mover.UglyMoves();
		
				for(Move move : moves){
					Board clone = Board.GetDeepCopy(a_board);
					Player tempWhite = (a_white.IsHuman() ? new Human(a_white, clone) : new Computer(a_white, clone));
					Player tempBlack = (a_black.IsHuman() ? new Human(a_black, clone) : new Computer(a_black, clone));

					clone = MakeMove(clone, move, tempWhite, tempBlack);
			
					tempWhite.Refresh(clone);
					tempBlack.Refresh(clone);
					
					lowestValue = Math.min(lowestValue, Maximize(clone, tempWhite, tempBlack, a_depth - Utilities.ONE, a_highest, lowestValue));
					
					if(a_highest >= lowestValue){
						return a_highest;
					}
				}
		/*
			}
		};
		
		new Thread(r).start();
		*/
		return lowestValue;
	}
	
	/**/
    /*
    NAME
        public static int Maximize(final Board a_board, final Player a_white, final Player a_black, final int a_depth, final int a_highest, final int a_lowest);
    
    SYNOPSIS
        public static int Maximize(final Board a_board, final Player a_white, final Player a_black, final int a_depth, final int a_highest, final int a_lowest);
    
        Board a_board ------------> The board to evaluate.
        
        Player a_white -----------> The white player.
        
        Player a_black -----------> The black player.
        
        int a_depth --------------> The current search depth.
        
        int a_highest ------------> The highest seen value so far.
        
        int a_lowest -------------> The lowest seen value so far.
    
    DESCRIPTION
        This method finds the board with the highest value in terms of piece placement
        along with other conditions such as castling, check, checkmate, etc. after a move
        has been applied to it. It will return the final value once the search depth reaches zero.
    
    RETURNS
        int highestValue: The highest board evaulation value found.
    
    AUTHOR
        Amir Afghani, Black Widow Chess: https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/player/ai/AlphaBetaWithMoveOrdering.java
        Modifications written specifically for this engine by Ryan King.
        Runnable with fields for alpha and beta taken from: https://stackoverflow.com/questions/1299837/cannot-refer-to-a-non-final-variable-inside-an-inner-class-defined-in-a-differen
    */
	public static int Maximize(final Board a_board, final Player a_white, final Player a_black, final int a_depth, final int a_highest, final int a_lowest){
		
		final Player mover = (a_board.WhoseTurnIsIt() == ChessColor.WHITE ? a_white : a_black);
		final Player opponent = (mover.IsWhite() ? a_black : a_white);
		final String history = (mover.IsWhite() ? DarkBlue.GetWhiteHistory() : DarkBlue.GetBlackHistory());		
		int highestValue = a_highest;
		
		if(a_depth == Utilities.ZERO){
			return Score(mover, opponent, a_board, a_depth);
		}	
		
		/*
		final Runnable r = new Runnable() {
			@Override
			public final void run() {
		*/
				ArrayList<Move> moves = Sort(mover, opponent, a_board, a_depth, history);
		
				for(Move move : moves){				
					
					Board clone = Board.GetDeepCopy(a_board);
					final Player tempWhite = (a_white.IsHuman() ? new Human(a_white, clone) : new Computer(a_white, clone));
					final Player tempBlack = (a_black.IsHuman() ? new Human(a_black, clone) : new Computer(a_black, clone));

					clone = MakeMove(clone, move, tempWhite, tempBlack);
			
					tempWhite.Refresh(clone);
					tempBlack.Refresh(clone);
			
					highestValue = Math.max(highestValue, Minimize(clone, tempWhite, tempBlack, a_depth - Utilities.ONE, highestValue, a_lowest));
					
					if(a_lowest <= highestValue){
						return a_lowest;
					}
				}// end of for loop
		/*
			}// end of run()
		};// end of Runnable
		
		new Thread(r).start();
		*/
		return highestValue;
	}
	
	/**/
    /*
    NAME
        public static Move Search(final Board a_board, final Player a_white, final Player a_black, final int a_depth);
    
    SYNOPSIS
        public static Move Search(final Board a_board, final Player a_white, final Player a_black, final int a_depth);
    
        Board a_board ------------> The board to evaluate.
        
        Player a_white -----------> The white player.
        
        Player a_black -----------> The black player.
        
        int a_depth --------------> The current search depth.
       
    DESCRIPTION
        This method searches for the best possible move out of the pool of possible moves for the computer player.
        It maximizes and minimizes the board recursively depending on who's moving.
        If a move is found that places the opponent's king in checkmate, it will break out immediately and return that move.

    RETURNS
        Move bestMove: The computer's best possible move.
    
    AUTHOR
        Amir Afghani, Black Widow Chess: https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/player/ai/MiniMax.java
        Modifications written specifically for this engine by Ryan King.
    */
	public static Move Search(final Board a_board, final Player a_white, final Player a_black, final int a_depth){
		int highestValue = Integer.MIN_VALUE;
		int lowestValue = Integer.MAX_VALUE;
		int currentValue = Utilities.ZERO;
		
		Move bestMove = null;
		
		final Player currentPlayer = (a_board.WhoseTurnIsIt().IsWhite() ? a_white : a_black);
		
		final ArrayList<Move> moves = Minimax.Sort((a_board.WhoseTurnIsIt().IsWhite() ? a_white : a_black), (a_board.WhoseTurnIsIt().IsWhite() ? a_black : a_white), a_board, a_depth, (a_board.WhoseTurnIsIt().IsWhite() ? DarkBlue.GetWhiteHistory() : DarkBlue.GetBlackHistory()));
		
		final int numberOfMoves = moves.size();
		
		final ChessColor turn = a_board.WhoseTurnIsIt();
		
		for(int index = Utilities.ZERO; index < moves.size(); index++){
			
			/*
			final Runnable r = new Runnable() {
				@Override
				public final void run() {
			*/
			
			final Move move = moves.get(index);
			
			Board clone = Board.GetDeepCopy(a_board);
			final Player tempWhite = (a_white.IsHuman() ? new Human(a_white, clone) : new Computer(a_white, clone));
			final Player tempBlack = (a_black.IsHuman() ? new Human(a_black, clone) : new Computer(a_black, clone));
			
			tempWhite.Refresh(clone);
			tempBlack.Refresh(clone);
			
			clone = MakeMove(clone, move, tempWhite, tempBlack);
			
			tempWhite.Refresh(clone);
			tempBlack.Refresh(clone);
			
			// Original condition: clone.WhoseTurnIsIt().IsWhite()
			currentValue = (clone.WhoseTurnIsIt() == DarkBlue.GetHumanColor() ? Minimize(clone, tempWhite, tempBlack, a_depth - Utilities.ONE, Integer.MIN_VALUE, Integer.MAX_VALUE) : Maximize(clone, tempWhite, tempBlack, a_depth - Utilities.ONE, Integer.MIN_VALUE, Integer.MAX_VALUE));
			
			if(turn.IsWhite() && currentValue > highestValue){
				highestValue = currentValue;
				bestMove = move;
				
				if(tempBlack.IsInCheckmate(clone)){
					return bestMove;
				}
			}else if(turn.IsBlack() && currentValue < lowestValue){
				lowestValue = currentValue;
				bestMove = move;
				
				if(tempWhite.IsInCheckmate(clone)){
					return bestMove;
				}				
			}
			
		/*	
			}// end of run()
		};// end of Runnable
		
		new Thread(r).start();
		*/
			
		}// end of for loop
		
		return bestMove;
	}
	
	/*
	public static final Move Execute(final Board a_board, final Player a_white, final Player a_black, final int a_depth){
		
	}
	*/
	
	// Make every value positive for the computer. If so, the algorithm will be consistent.
	// The human can do whatever s/he wants and the computer will be the one to worry about.
	
	/**/
    /*
    NAME
        public static Board MakeMove(final Board a_board, final Move a_move, final Player a_white, final Player a_black);
    
    SYNOPSIS
        public static Board MakeMove(final Board a_board, final Move a_move, final Player a_white, final Player a_black);
    
        Board a_board ------------> The board.
        
        Move a_move --------------> The move to be made.
        
        Player a_white -----------> The white player.
        
        Player a_black -----------> The black player.
       
    DESCRIPTION
        This method makes any type of move on the given board.

    RETURNS
        The board with the newly-made move.
    
    AUTHOR
        Ryan King
    */
	public static Board MakeMove(final Board a_board, final Move a_move, final Player a_white, final Player a_black){
		if(a_move.IsEnPassant()){
			return a_board.EnPassant((EnPassantMove)a_move, a_white, a_black);
		}else if(a_move.IsCastling()){
			return a_board.Castle((CastlingMove)a_move);
		}else if(a_move.IsAttacking()){
			return a_board.Attack((AttackingMove)a_move, a_white, a_black);
		}else{
			return a_board.Move((RegularMove)a_move);
		}
	}
	
	public static int Score(final Player a_player, final Player a_opponent, final Board a_board, final int a_depth){
		if(a_player.IsWhite()){
			return EvaluatePlayer(a_player, a_opponent, a_board, a_depth, (a_player.IsWhite() ? DarkBlue.GetWhiteHistory() : DarkBlue.GetBlackHistory())) - EvaluatePlayer(a_opponent, a_player, a_board, a_depth, (a_opponent.IsWhite() ? DarkBlue.GetWhiteHistory() : DarkBlue.GetBlackHistory()));
		}else{
			return EvaluatePlayer(a_opponent, a_player, a_board, a_depth, (a_opponent.IsWhite() ? DarkBlue.GetWhiteHistory() : DarkBlue.GetBlackHistory())) - EvaluatePlayer(a_player, a_opponent, a_board, a_depth, (a_player.IsWhite() ? DarkBlue.GetWhiteHistory() : DarkBlue.GetBlackHistory()));
		}
	}
	
	/**/
    /*
    NAME
        public static int EvaluatePlayer(final Player a_player, final Player a_opponent, final Board a_board, final int a_depth, final String a_moveHistory);
    
    SYNOPSIS
        public static int EvaluatePlayer(final Player a_player, final Player a_opponent, final Board a_board, final int a_depth, final String a_moveHistory);
    
    	Player a_player ----------> The current player.
    	
    	Player a_opponent --------> The opponent.
    
        Board a_board ------------> The board.
        
        int a_depth --------------> The search depth.
        
        String a_moveHistory -----> The player's move history in algebraic notation.

       
    DESCRIPTION
        This method returns an evaluation of the player's material.
        
    RETURNS
        The board with the newly-made move.
    
    AUTHOR
        Ryan King
    */
	public static int EvaluatePlayer(final Player a_player, final Player a_opponent, final Board a_board, final int a_depth, final String a_moveHistory){
		return a_player.PieceEvaluations() + a_player.AttackScore() + MobilityFactor(a_player, a_opponent) + CheckmateFactor(a_opponent, a_board, a_depth) + DepthFactor(a_depth) + CastlingFactor(a_player, a_moveHistory);
	}
	
	/**/
    /*
    NAME
        public static int CheckFactor(final Player a_player, final Board a_board);
    
    SYNOPSIS
        public static int CheckFactor(final Player a_player, final Board a_board);
    
    	Player a_player ----------> The current player.
   
        Board a_board ------------> The board.
       
    DESCRIPTION
        This method returns a bonus if the player is in check or zero otherwise.

)    RETURNS
        A bonus if the player is in check or zero otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Ryan King
    */
	public static int CheckFactor(final Player a_player, final Board a_board){
		if(a_player.IsInCheck(a_board)){
			return CHECK_FACTOR;
		}else{
			return Utilities.ZERO;
		}
	}
	
	/**/
    /*
    NAME
        public static int Sort(final Player a_player, final Player a_opponent, final Board a_board, final int a_depth, final String a_moveHistory);
    
    SYNOPSIS
        public static int Sort(final Player a_player, final Player a_opponent, final Board a_board, final int a_depth, final String a_moveHistory);
    
    	Player a_player ----------> The current player.
    	
    	Player a_opponent --------> The opponent.
    
        Board a_board ------------> The board.
        
        int a_depth --------------> The search depth.
        
        String a_moveHistory -----> The player's move history in algebraic notation.

       
    DESCRIPTION
        This method sorts the ArrayList of moves by ascending board value.

    RETURNS
        ArrayList<Move> sortedMoves: The list of sorted moves.
    
    AUTHOR
        Ryan King
    */
	public static ArrayList<Move> Sort(final Player a_player, final Player a_opponent, final Board a_board, final int a_depth, final String a_moveHistory){
		ArrayList<Move> moves = a_player.UglyMoves(), sortedMoves = new ArrayList<>();
		ArrayList<Integer> values = new ArrayList<>();

		int currentValue = Utilities.ZERO;
		
		for(int i = Utilities.ZERO; i < moves.size(); i++){
			Board clone = Board.GetDeepCopy(a_board);
            Human tempWhite = new Human(ChessColor.WHITE, clone);
            Human tempBlack = new Human(ChessColor.BLACK, clone);
            tempWhite.InitializePieces(clone);
            tempBlack.InitializePieces(clone);
            
            clone = Minimax.MakeMove(clone, moves.get(i), tempWhite, tempBlack);
            
            currentValue = EvaluatePlayer((a_board.WhoseTurnIsIt().IsWhite() ? tempBlack : tempWhite), (a_board.WhoseTurnIsIt().IsWhite() ? tempWhite : tempBlack), clone, a_depth, a_moveHistory);
                  
            values.add(currentValue);
		}
		
		int index;
		
		while(!moves.isEmpty()){
			index = (a_player.IsWhite() ? GetSmallestIndex(values) : GetLargestIndex(values));
			sortedMoves.add(Factory.MoveFactory(moves.get(index).GetPiece(), moves.get(index).GetNewRow(), moves.get(index).GetNewColumn(), moves.get(index).GetVictim(), a_board));
			moves.remove(index);
			values.remove(index);			
		}
		
		return sortedMoves;
	}
	
	/**/
    /*
    NAME
        public static int GetSmallestIndex(ArrayList<Integer> a_values);
    
    SYNOPSIS
        public static int GetSmallestIndex(ArrayList<Integer> a_values);
    
    	ArrayList<Integer> a_values -----> The values of all evaluated boards.
       
    DESCRIPTION
        This method returns the index pointing to the smallest value.

    RETURNS
        int largest: The index pointing to the smallest value.
    
    AUTHOR
        Ryan King
    */
	public static int GetSmallestIndex(ArrayList<Integer> a_values){
		int smallest = Utilities.ZERO;
		
		for(int i = Utilities.ONE; i < a_values.size(); i++){
			if(a_values.get(i) < a_values.get(smallest)){
				smallest = i;
			}
		}
		
		return smallest;
	}
	
	/**/
    /*
    NAME
        public static int GetLargestIndex(ArrayList<Integer> a_values);
    
    SYNOPSIS
        public static int GetLargestIndex(ArrayList<Integer> a_values);
    
    	ArrayList<Integer> a_values -----> The values of all evaluated boards.
       
    DESCRIPTION
        This method returns the index pointing to the largest value.

    RETURNS
        int largest: The index pointing to the largest value.
    
    AUTHOR
        Ryan King
    */
	public static int GetLargestIndex(ArrayList<Integer> a_values){
		int largest = Utilities.ZERO;
		
		for(int i = Utilities.ONE; i < a_values.size(); i++){
			if(a_values.get(i) > a_values.get(largest)){
				largest = i;
			}
		}
		
		return largest;
	}
	
	/**/
    /*
    NAME
        public static int CheckmateFactor(final Player a_player, final Board a_board);
    
    SYNOPSIS
        public static int CheckmateFactor(final Player a_player, final Board a_board);
    
    	Player a_player ----------> The current player.
   
        Board a_board ------------> The board.
       
    DESCRIPTION
        This method returns a bonus if the player is in checkmate or zero otherwise.

    RETURNS
        A bonus if the player is in checkmate or zero otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Amir Afghani, Black Widow Chess: https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/player/ai/MiniMax.java
    */
	public static int CheckmateFactor(final Player a_player, final Board a_board, final int a_depth){
		return (a_player.IsInCheckmate(a_board) ? CHECKMATE_FACTOR * a_depth : CheckFactor(a_player, a_board));
	}
	
	public static int MobilityFactor(final Player a_player, final Player a_opponent){
		return ((int)(a_player.HowManyMoves() * 100.0f) / (a_opponent.HowManyMoves() > Utilities.ZERO ? a_opponent.HowManyMoves() : Utilities.ONE)) * DEPTH_FACTOR;
	}
	
	/**/
    /*
    NAME
        public static int DepthFactor(final int a_depth);
    
    SYNOPSIS
        public static int DepthFactor(final int a_depth);
    
    	int a_depth --------> The current AI search depth.
       
    DESCRIPTION
        This method returns a bonus times the depth if the depth is above zero;
        otherwise it returns one.

    RETURNS
        A bonus times a nonzero search depth or one if the depth is zero.
        One of these two options will always occur.
    
    AUTHOR
        Amir Afghani, Black Widow Chess: https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/player/ai/MiniMax.java
    */
	public static int DepthFactor(final int a_depth){
		if(a_depth == Utilities.ZERO){
			return Utilities.ONE;
		}else{
			return DEPTH_FACTOR * a_depth;
		}
	}
	
	/**/
    /*
    NAME
        public static int CastlingFactor(final Player a_player, final Board a_board);
    
    SYNOPSIS
        public static int CastlingFactor(final Player a_player, final Board a_board);
    
    	Player a_player ----------> The current player.
   
        Board a_board ------------> The board.
       
    DESCRIPTION
        This method returns a bonus if the player has castled or zero otherwise.

    RETURNS
        A bonus if the player has castled or zero otherwise.
        One of these two options will always occur.
    
    AUTHOR
        Amir Afghani, Black Widow Chess: https://github.com/amir650/BlackWidow-Chess/blob/master/src/com/chess/engine/classic/player/ai/MiniMax.java
    */
	public static int CastlingFactor(final Player a_player, final String a_moveHistory){
		if(a_player.HasCastled(a_moveHistory)){
			return CASTLING_FACTOR;
		}else{
			return Utilities.ZERO;
		}
	}
}