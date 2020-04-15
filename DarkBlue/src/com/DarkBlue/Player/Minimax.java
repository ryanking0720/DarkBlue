package com.DarkBlue.Player;

import com.DarkBlue.Board.Board;
import com.DarkBlue.GUI.DarkBlue;
import com.DarkBlue.Utilities.BoardUtilities;
import com.DarkBlue.Utilities.ChessColor;
import com.DarkBlue.Utilities.Factory;
import com.DarkBlue.Utilities.MoveEvaluation;
import com.DarkBlue.Utilities.Utilities;
import com.DarkBlue.Move.Move;
import com.DarkBlue.Move.RegularMove;
import com.DarkBlue.Piece.Pawn;
import com.DarkBlue.Piece.Piece;
import com.DarkBlue.Move.AttackingMove;
import com.DarkBlue.Move.CastlingMove;
import com.DarkBlue.Move.EnPassantMove;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * This interface is the bread and butter of the computer player.
 * 
 * It has a move searching algorithm that finds the best moves
 * based on its value in terms of piece quantity and position.
 * 
 * These values are determined by evaluating the value of each piece
 * as well as the value of the tile it is resting on. Arrays in the 
 * MoveEvaluation interface are brought in to be searched. Each piece
 * has its own array even if it's a different color, except for the
 * queen and the knight. Each board evaluation is a sum of these numbers.
 * 
 * The values are analyzed to determine which is either highest or lowest,
 * depending on the player's color. Alpha-beta pruning is used to establish
 * lower and upper bounds that can be checked in order to get rid of irrelevant
 * moves that will not change the state of the board much from what has already been discovered.
 * 
 * All moves are sorted when they are taken from each player. Sorted order is as follows:
 * 
 * 1. Moves that put the opponent into checkmate
 * 2. Moves that put the opponent into check
 * 3. Attacking moves
 * 4. Regular moves
 * 5. En passant moves
 */
public interface Minimax{

	// Symbolic constants
	public static final double PAWN_VALUE = 10;
	public static final double BISHOP_OR_KNIGHT_VALUE = 30;
	public static final double ROOK_VALUE = 50;
	public static final double QUEEN_VALUE = 90;
	public static final double KING_VALUE = 900;

	/**/
    /*
    NAME
        public static Move MinimaxRoot(final int a_depth, final Board a_board, final Player a_white, final Player a_black, final boolean a_isMaximizer);
    
    SYNOPSIS
        public static Move MinimaxRoot(final int a_depth, final Board a_board, final Player a_white, final Player a_black, final boolean a_isMaximizer);
    
    	int a_depth --------------> The AI search depth.
    
        Board a_board ------------> The board to evaluate.
        
        Player a_white -----------> The white player.
        
        Player a_black -----------> The black player.
        
        boolean a_isMaximizer ----> If the computer player is the maximizing player.
       
    DESCRIPTION
        This method searches for the best possible move out of the pool of possible moves for the computer player.
        It maximizes and minimizes the board recursively depending on who's moving.
        If a move is found that places the opponent's king in checkmate, it will break out immediately and return that move.

    RETURNS
        Move bestMove: The computer's best possible move.
    
    AUTHOR
        Lauri Hartikka, A step-by-step guide to building a simple chess AI, https://jsfiddle.net/q76uzxwe/1/
        Modifications written specifically for this engine by Ryan King.
    */
	public static Move MinimaxRoot(final int a_depth, final Board a_board, final Player a_white, final Player a_black, final boolean a_isMaximizer, final ChessColor a_callerColor){
		// bestMove will hold the best move found by the board evaluation
		Move bestMove = null;

		// Order the moves so the best ones come first (e.g. checkmate, check, attacks, castling, etc.)
		final ArrayList<Move> MOVES = Minimax.Sort((a_board.WhoseTurnIsIt().IsWhite() ? a_white : a_black));
		
		// Set default values for our variables
		double currentValue = Utilities.ZERO;
		double bestValue = Integer.MIN_VALUE;
		
		for(final Move MOVE : MOVES){
			// This will hold the board after the move has been made
			final Board RESULT = MOVE.GetTransitionalBoard();
			
			// These new players will be evaluated independently of the ones passed in
			final Player WHITE = new Human(ChessColor.WHITE, RESULT);
			final Player BLACK = new Human(ChessColor.BLACK, RESULT);
			
			WHITE.Refresh(RESULT);
			BLACK.Refresh(RESULT);
			
			// Recursively search for the best value
			currentValue = Recurse(a_depth - Utilities.ONE, RESULT, WHITE, BLACK, Integer.MIN_VALUE, Integer.MAX_VALUE, !a_isMaximizer, a_callerColor);

			// Update the value if the next one found is better; update the move accordingly
			if(currentValue >= bestValue){
				bestValue = currentValue;
				bestMove = MOVE;
				
				// Stop evaluating if the move places the opponent into checkmate
				if(bestMove.PlacesOpponentIntoCheckmate()){
					return bestMove;
				}
			}
		}
				
		return bestMove;		
	}
	
	/**/
    /*
    NAME
        public static double Recurse(final int a_depth, final Board a_board, final Player a_white, final Player a_black, double a_alpha, double a_beta, final boolean a_isMaximizer);
    
    SYNOPSIS
        public static double Recurse(final int a_depth, final Board a_board, final Player a_white, final Player a_black, double a_alpha, double a_beta, final boolean a_isMaximizer);
    
    	int a_depth --------------> The AI search depth.
    
        Board a_board ------------> The board to evaluate.
        
        Player a_white -----------> The white player.
        
        Player a_black -----------> The black player.
        
        double a_alpha -----------> The best value for the maximizer.
        
        double a_beta ------------> The best value for the minimizer.
        
        boolean a_isMaximizer ----> If the player is the maximizing player.
       
    DESCRIPTION
        This method searches for the best possible board value from the pool of possible moves for the computer player.
        It maximizes and minimizes the board recursively depending on who's moving.
        This uses alpha-beta pruning, so moves that are determined to give a value lower than alpha or higher than beta will be ignored.

    RETURNS
        double bestValue: The best board evaluation found.
    
    AUTHOR
    	Lauri Hartikka, A step-by-step guide to building a simple chess AI, https://jsfiddle.net/q76uzxwe/1/
        Modifications written specifically for this engine by Ryan King.
    */
	public static double Recurse(final int a_depth, final Board a_board, final Player a_white, final Player a_black, double a_alpha, double a_beta, final boolean a_isMaximizer, final ChessColor a_callerColor){
		// Base case: The search depth is as deep as it can go
		if(a_depth == Utilities.ZERO){
		    return -Evaluate(a_board, a_callerColor);
		}

		// This will hold the current player's moves
		final ArrayList<Move> MOVES = Minimax.Sort((a_board.WhoseTurnIsIt().IsWhite() ? a_white : a_black));
		
		// bestValue will hold the current best board evaluation
		double bestValue;
		
		if(a_isMaximizer){
			// All values found will be higher than this
			bestValue = a_alpha;
			
			for(final Move MOVE : MOVES){
				// Make a deep copy of the board with the move made on it
				final Board RESULT = MOVE.GetTransitionalBoard();
				
				// Initialize temporary players to determine evaluations on this board
				final Player WHITE = new Human(ChessColor.WHITE, RESULT);
				final Player BLACK = new Human(ChessColor.BLACK, RESULT);
				
				WHITE.Refresh(RESULT);
				BLACK.Refresh(RESULT);
				
				// This will be an alias to whoever is playing next
				final Player CURRENT_PLAYER = (RESULT.WhoseTurnIsIt().IsWhite() ? WHITE : BLACK);
				
				// This will contain a pawn that can be promoted if the player has one
				final Pawn PROMOTED_PAWN = CURRENT_PLAYER.GetPromotedPawn(RESULT);
				
				// Evaluate the move as normal if no promotion can be made
				if(PROMOTED_PAWN == null){
					// Find the highest value recursively
					bestValue = Math.max(bestValue, Recurse(a_depth - Utilities.ONE, RESULT, WHITE, BLACK, a_alpha, a_beta, !a_isMaximizer, a_callerColor));
				
					// Keep track of the boundaries
					a_alpha = Math.max(a_alpha, bestValue);
				}else{
					// Change the pawn to a promoted piece and then continue evaluating
					for(int i = Utilities.ZERO; i < Utilities.FOUR; i++){
						final Board PROMOTION = PROMOTED_PAWN.Promote(RESULT, i);
						
						WHITE.Refresh(PROMOTION);
						BLACK.Refresh(PROMOTION);
						
						// Find the highest value recursively
						bestValue = Math.max(bestValue, Recurse(a_depth - Utilities.ONE, PROMOTION, WHITE, BLACK, a_alpha, a_beta, !a_isMaximizer, a_callerColor));
					
						// Keep track of the boundaries
						a_alpha = Math.max(a_alpha, bestValue);
						
						if(a_beta <= a_alpha){
							return bestValue;
						}
					}
				}
				// Discontinue evaluating if the lower bound is worse
				if(a_beta <= a_alpha){
					return bestValue;
				}
			}
			
			return bestValue;
		}else{
			// All values found will be lower than this
			bestValue = a_beta;
			
			for(final Move MOVE : MOVES){
				// Make a deep copy of the board with the move made on it
				final Board RESULT = MOVE.GetTransitionalBoard();
				
				// Initialize temporary players to determine evaluations on this board
				final Player WHITE = new Human(ChessColor.WHITE, RESULT);
				final Player BLACK = new Human(ChessColor.BLACK, RESULT);
				
				WHITE.Refresh(RESULT);
				BLACK.Refresh(RESULT);
				
				// CURRENT_PLAYER will be an alias to whoever is playing next
				final Player CURRENT_PLAYER = (RESULT.WhoseTurnIsIt().IsWhite() ? WHITE : BLACK);
				
				// PROMOTED_PAWN will contain a pawn that can be promoted if the player has one
				final Pawn PROMOTED_PAWN = CURRENT_PLAYER.GetPromotedPawn(RESULT);
				
				// Evaluate the move as normal if no promotion can be made
				if(PROMOTED_PAWN == null){
					// Find the lowest value recursively
					bestValue = Math.min(bestValue, Recurse(a_depth - Utilities.ONE, RESULT, WHITE, BLACK, a_alpha, a_beta, !a_isMaximizer, a_callerColor));
				
					// Keep track of the boundaries
					a_beta = Math.min(a_beta, bestValue);
				}else{
					// Change the pawn to a promoted piece and then continue evaluating
					for(int i = Utilities.ZERO; i < Utilities.FOUR; i++){
						final Board PROMOTION = PROMOTED_PAWN.Promote(RESULT, i);
						
						WHITE.Refresh(PROMOTION);
						BLACK.Refresh(PROMOTION);
						
						// Find the lowest value recursively
						bestValue = Math.min(bestValue, Recurse(a_depth - Utilities.ONE, PROMOTION, WHITE, BLACK, a_alpha, a_beta, !a_isMaximizer, a_callerColor));
					
						// Keep track of the boundaries
						a_beta = Math.min(a_beta, bestValue);
						
						if(a_beta <= a_alpha){
							return bestValue;
						}
					}
				}
				
				// Discontinue evaluating if the lower bound is worse
				if(a_beta <= a_alpha){
					return bestValue;
				}
			}
			
			return bestValue;
		}
	}	
	
	/**/
    /*
    NAME
        public static double Evaluate(final Board a_board);
    
    SYNOPSIS
        public static double Evaluate(final Board a_board);
    
        Board a_board ------------> The board to evaluate.
      
    DESCRIPTION
        This method evaluates all pieces and their positions on the board.

    RETURNS
        double evaluation: The value of all pieces on the board.
    
    AUTHOR
    	Lauri Hartikka, A step-by-step guide to building a simple chess AI, https://jsfiddle.net/q76uzxwe/1/
        Modifications written specifically for this engine by Ryan King.
    */
	public static double Evaluate(final Board a_board, final ChessColor a_callerColor){
		double evaluation = Utilities.ZERO;
		
		// Null arguments do not return any value of significance
		if(a_board == null || a_callerColor == null){
		    return evaluation;
		}
		
		// Evaluate every tile of the board
		for(int i = Utilities.ZERO; i < Utilities.SIXTY_FOUR; i++){
			final int ROW = i / Utilities.EIGHT;
			final int COLUMN = i % Utilities.EIGHT;
			
			// Do not evaluate any empty tiles
			if(a_board.GetTile(ROW, COLUMN).IsEmpty()){
				continue;
			}
			
			// Get the value of the piece
			final Piece PIECE = a_board.GetTile(ROW, COLUMN).GetPiece();
			
			evaluation += GetPieceValue(PIECE, ROW, COLUMN, a_callerColor);
		}
		
		return evaluation;
	}
	
	/**/
    /*
    NAME
        public static double GetPieceValue(final Piece a_piece, final int a_x, final int a_y);
    
    SYNOPSIS
        public static double GetPieceValue(final Piece a_piece, final int a_x, final int a_y);
    
        Piece a_piece --------------> The piece to evaluate.
        
        int a_x --------------------> The row the piece is on.
        
        int a_y --------------------> The column the piece is on.
       
    DESCRIPTION
        This method evaluates a piece on its tile.

    RETURNS
        double: The value of the given piece on its tile, or 0 if the piece was invalid.
        One of these two options will always occur.
    
    AUTHOR
    	Lauri Hartikka, A step-by-step guide to building a simple chess AI, https://jsfiddle.net/q76uzxwe/1/
        Modifications written specifically for this engine by Ryan King.
    */
	public static double GetPieceValue(final Piece a_piece, final int a_x, final int a_y, final ChessColor a_callerColor){
		// Null or invalid arguments do not return any value of significance
	    if (a_piece == null || !BoardUtilities.HasValidCoordinates(a_y, a_x)){
	        return Utilities.ZERO;
	    }

	    // Find the absolute value of the piece, e.g. 10, 30, 50, 70, 90, or 900
	    final double ABSOLUTE_VALUE = GetAbsoluteValue(a_piece, a_x ,a_y);
	    
	    // Negate the value if the piece is the caller's color or keep it positive if it's not
	    return (a_piece.GetColor().IsEnemy(a_callerColor) ? ABSOLUTE_VALUE : -ABSOLUTE_VALUE);
	}

	/**/
    /*
    NAME
        public static double GetAbsoluteValue(final Piece a_piece, final int a_x, final int a_y);
    
    SYNOPSIS
        public static double GetAbsoluteValue(final Piece a_piece, final int a_x, final int a_y);
    
        Piece a_piece --------------> The piece to evaluate.
        
        int a_x --------------------> The row the piece is on.
        
        int a_y --------------------> The column the piece is on.
       
    DESCRIPTION
        This method evaluates a piece on its tile by returning its absolute value.
        Negation, if needed, will be performed later.

    RETURNS
        double: The value of the given piece on its tile, or 0 if the piece was invalid.
        One of these two options will always occur.
    
    AUTHOR
    	Lauri Hartikka, A step-by-step guide to building a simple chess AI, https://jsfiddle.net/q76uzxwe/1/
        Modifications written specifically for this engine by Ryan King.
    */
	public static double GetAbsoluteValue(final Piece a_piece, final int a_x , final int a_y){
		// Idiot proofing in case of null arguments
		if(a_piece == null || !BoardUtilities.HasValidCoordinates(a_y, a_x)){
			return Utilities.ZERO;
		}
		
	    if(a_piece.IsPawn()){
	        return PAWN_VALUE + (a_piece.IsWhite() ? MoveEvaluation.m_whitePawnPositions[a_y][a_x] : MoveEvaluation.m_blackPawnPositions[a_y][a_x]);
	    }else if(a_piece.IsRook()){
	        return ROOK_VALUE + (a_piece.IsWhite() ? MoveEvaluation.m_whiteRookPositions[a_y][a_x] : MoveEvaluation.m_blackRookPositions[a_y][a_x]);
	    }else if(a_piece.IsKnight()){
	        return BISHOP_OR_KNIGHT_VALUE + MoveEvaluation.m_knightPositions[a_y][a_x];
	    }else if(a_piece.IsBishop()){
	        return BISHOP_OR_KNIGHT_VALUE + (a_piece.IsWhite() ? MoveEvaluation.m_whiteBishopPositions[a_y][a_x] : MoveEvaluation.m_blackBishopPositions[a_y][a_x]);
	    }else if(a_piece.IsQueen()){
	        return QUEEN_VALUE + MoveEvaluation.m_queenPositions[a_y][a_x];
	    }else if(a_piece.IsKing()){
	        return KING_VALUE + (a_piece.IsWhite() ? MoveEvaluation.m_whiteKingPositions[a_y][a_x] : MoveEvaluation.m_blackKingPositions[a_y][a_x]);
	    }
	        
	    return Utilities.ZERO;
	}
	
	/**/
    /*
    NAME
        public static ArrayList<Move> Sort(final Player a_player);
    
    SYNOPSIS
        public static ArrayList<Move> Sort(final Player a_player);
    
    	Player a_player ----------> The current player.
       
    DESCRIPTION
        This method sorts the ArrayList of moves by ascending "priority", that is,
        moves that put the opponent's king into checkmate or check are placed first,
        as they are the most important.
        Attacking moves go next, then castling moves, then regular moves.

    RETURNS
        ArrayList<Move> sortedMoves: The list of sorted moves.
    
    AUTHOR
        Ryan King
    */
	public static ArrayList<Move> Sort(final Player a_player){
		// MOVES will contain the moves the player can make on this turn
		final ArrayList<Move> MOVES = new ArrayList<>();

		// Add the moves in order of priority
		MOVES.addAll(a_player.CheckmateMoves());
		MOVES.addAll(a_player.CheckMoves());
		MOVES.addAll(a_player.AttackingMoves());
		MOVES.addAll(a_player.CastlingMoves());
		MOVES.addAll(a_player.RegularMoves());
		MOVES.addAll(a_player.EnPassantMoves());
		
		// Get rid of duplicates in the list
		final LinkedHashSet<Move> SET = new LinkedHashSet<>(MOVES);
		
		// Make a new list with no duplicates
		final ArrayList<Move> SORTED_MOVES = new ArrayList<>(SET);
		
		// Return the list with no duplicates
		return SORTED_MOVES;
	}
}